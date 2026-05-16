package com.example.academic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.academic.entity.*;
import com.example.academic.mapper.*;
import com.example.academic.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    @Value("${ai.api.url}")
    private String apiUrl;

    @Value("${ai.api.key}")
    private String apiKey;

    @Value("${ai.api.model}")
    private String model;

    private final RestTemplate restTemplate;
    private final StudentMapper studentMapper;
    private final CourseScoreMapper courseScoreMapper;
    private final LibraryBorrowMapper libraryBorrowMapper;
    private final ConsumptionMapper consumptionMapper;

    @Override
    public String generateSuggestion(String studentId) {
        Student s = studentMapper.selectOne(
                new LambdaQueryWrapper<Student>().eq(Student::getStudentId, studentId));
        if (s == null) throw new RuntimeException("学生不存在");

        List<CourseScore> scores = courseScoreMapper.selectList(
                new LambdaQueryWrapper<CourseScore>().eq(CourseScore::getStudentId, studentId));
        BigDecimal avgGpa = scores.stream().map(CourseScore::getGpa).filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (!scores.isEmpty()) avgGpa = avgGpa.divide(BigDecimal.valueOf(scores.size()), 2, RoundingMode.HALF_UP);
        long failCount = scores.stream().filter(cs -> cs.getScore() != null && cs.getScore().compareTo(BigDecimal.valueOf(60)) < 0).count();
        long borrowCount = libraryBorrowMapper.selectCount(
                new LambdaQueryWrapper<LibraryBorrow>().eq(LibraryBorrow::getStudentId, studentId));
        List<Consumption> consumes = consumptionMapper.selectList(
                new LambdaQueryWrapper<Consumption>().eq(Consumption::getStudentId, studentId));
        BigDecimal total = consumes.stream().map(Consumption::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        long days = consumes.stream().map(c -> c.getConsumeTime().toLocalDate()).distinct().count();
        BigDecimal avgConsume = days > 0
                ? total.divide(BigDecimal.valueOf(days), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        String prompt = String.format(
                "你是一名大学辅导员，请根据以下学生数据给出学习建议：\n平均绩点：%s，挂科数：%d，本学期借阅次数：%d，日均消费金额：%s元。\n要求：建议具体、鼓励性强，不超过200字。",
                avgGpa, failCount, borrowCount, avgConsume);

        // Call LLM with 5s timeout
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(() -> callLlmApi(prompt));

        try {
            String result = future.get(5, TimeUnit.SECONDS);
            executor.shutdown();
            if (result != null && !result.isBlank()) return result.trim();
        } catch (TimeoutException e) {
            future.cancel(true);
            executor.shutdownNow();
        } catch (Exception e) {
            executor.shutdownNow();
        }

        return "当前无法获取AI建议，请稍后再试。建议您多与任课老师沟通，并合理安排学习时间。";
    }

    @SuppressWarnings("unchecked")
    private String callLlmApi(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("Content-Type", "application/json");

            Map<String, Object> body = Map.of(
                    "model", model,
                    "messages", List.of(Map.of("role", "user", "content", prompt)),
                    "max_tokens", 300,
                    "temperature", 0.7
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
