package com.example.academic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.academic.dto.StudentDetailVO;
import com.example.academic.dto.StudentVO;
import com.example.academic.entity.*;
import com.example.academic.mapper.*;
import com.example.academic.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentMapper studentMapper;
    private final CourseScoreMapper courseScoreMapper;
    private final LibraryBorrowMapper libraryBorrowMapper;
    private final ConsumptionMapper consumptionMapper;

    @Override
    public Page<StudentVO> listStudents(Integer page, Integer size, String keyword, String riskLevel, String className) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        if (className != null && !className.isEmpty()) {
            wrapper.eq(Student::getClassName, className);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Student::getName, keyword).or().like(Student::getStudentId, keyword));
        }
        Page<Student> studentPage = studentMapper.selectPage(new Page<>(page, size), wrapper);

        Page<StudentVO> result = new Page<>(page, size, studentPage.getTotal());
        result.setRecords(studentPage.getRecords().stream().map(s -> {
            StudentVO vo = new StudentVO();
            vo.setStudentId(s.getStudentId());
            vo.setName(s.getName());
            vo.setGender(s.getGender());
            vo.setMajor(s.getMajor());
            vo.setClassName(s.getClassName());
            vo.setEnrollmentYear(s.getEnrollmentYear());

            // GPA
            List<CourseScore> scores = courseScoreMapper.selectList(
                    new LambdaQueryWrapper<CourseScore>().eq(CourseScore::getStudentId, s.getStudentId()));
            BigDecimal avgGpa = scores.stream()
                    .map(CourseScore::getGpa)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (!scores.isEmpty()) {
                avgGpa = avgGpa.divide(BigDecimal.valueOf(scores.size()), 2, RoundingMode.HALF_UP);
            }
            vo.setAvgGpa(avgGpa);

            // Fail count
            long failCount = scores.stream().filter(cs -> cs.getScore() != null && cs.getScore().compareTo(BigDecimal.valueOf(60)) < 0).count();
            vo.setFailCount((int) failCount);

            // Risk
            String level = calculateRisk(avgGpa, (int) failCount);
            vo.setRiskLevel(level);
            vo.setRiskReason(buildRiskReason(avgGpa, (int) failCount));

            // Borrow
            Long borrowCount = libraryBorrowMapper.selectCount(
                    new LambdaQueryWrapper<LibraryBorrow>().eq(LibraryBorrow::getStudentId, s.getStudentId()));
            vo.setBorrowCount(borrowCount.intValue());

            // Avg daily consume
            List<Consumption> consumes = consumptionMapper.selectList(
                    new LambdaQueryWrapper<Consumption>().eq(Consumption::getStudentId, s.getStudentId()));
            BigDecimal total = consumes.stream().map(Consumption::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            long days = consumes.stream().map(c -> c.getConsumeTime().toLocalDate()).distinct().count();
            if (days > 0) {
                vo.setAvgDailyConsume(total.divide(BigDecimal.valueOf(days), 2, RoundingMode.HALF_UP));
            } else {
                vo.setAvgDailyConsume(BigDecimal.ZERO);
            }

            return vo;
        }).filter(vo -> {
            if (riskLevel != null && !riskLevel.isEmpty()) {
                return vo.getRiskLevel().equals(riskLevel);
            }
            return true;
        }).collect(Collectors.toList()));

        return result;
    }

    @Override
    public StudentDetailVO getStudentDetail(String studentId) {
        Student s = studentMapper.selectOne(
                new LambdaQueryWrapper<Student>().eq(Student::getStudentId, studentId));
        if (s == null) throw new RuntimeException("学生不存在");

        StudentDetailVO vo = new StudentDetailVO();
        vo.setStudentId(s.getStudentId());
        vo.setName(s.getName());
        vo.setGender(s.getGender());
        vo.setMajor(s.getMajor());
        vo.setClassName(s.getClassName());
        vo.setEnrollmentYear(s.getEnrollmentYear());

        List<CourseScore> scores = courseScoreMapper.selectList(
                new LambdaQueryWrapper<CourseScore>().eq(CourseScore::getStudentId, studentId));
        BigDecimal avgGpa = scores.stream().map(CourseScore::getGpa).filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (!scores.isEmpty()) {
            avgGpa = avgGpa.divide(BigDecimal.valueOf(scores.size()), 2, RoundingMode.HALF_UP);
        }
        vo.setAvgGpa(avgGpa);
        long failCount = scores.stream().filter(cs -> cs.getScore() != null && cs.getScore().compareTo(BigDecimal.valueOf(60)) < 0).count();
        vo.setFailCount((int) failCount);
        vo.setRiskLevel(calculateRisk(avgGpa, (int) failCount));
        vo.setRiskReason(buildRiskReason(avgGpa, (int) failCount));

        // GPA trend by semester
        Map<String, List<BigDecimal>> bySemester = scores.stream()
                .collect(Collectors.groupingBy(CourseScore::getSemester,
                        Collectors.mapping(CourseScore::getGpa, Collectors.toList())));
        List<Map<String, Object>> gpaTrend = new ArrayList<>();
        bySemester.forEach((sem, gpas) -> {
            BigDecimal semAvg = gpas.stream().filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(gpas.size()), 2, RoundingMode.HALF_UP);
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("semester", sem);
            m.put("avgGpa", semAvg);
            gpaTrend.add(m);
        });
        gpaTrend.sort(Comparator.comparing(m -> (String) m.get("semester")));
        vo.setGpaTrend(gpaTrend);

        // Borrow stats
        List<LibraryBorrow> borrows = libraryBorrowMapper.selectList(
                new LambdaQueryWrapper<LibraryBorrow>().eq(LibraryBorrow::getStudentId, studentId));
        vo.setTotalBorrowCount(borrows.size());
        long recentBorrows = borrows.stream()
                .filter(b -> b.getBorrowDate().isAfter(LocalDate.now().minusDays(30))).count();
        vo.setRecentBorrowCount((int) recentBorrows);

        // Borrow category distribution
        Map<String, Long> catCount = borrows.stream()
                .collect(Collectors.groupingBy(b -> b.getCategory() != null ? b.getCategory() : "其他", Collectors.counting()));
        List<Map<String, Object>> borrowCatDist = new ArrayList<>();
        catCount.forEach((cat, cnt) -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("category", cat);
            m.put("count", cnt);
            borrowCatDist.add(m);
        });
        vo.setBorrowCategoryDistribution(borrowCatDist);

        // Consume stats
        List<Consumption> consumes = consumptionMapper.selectList(
                new LambdaQueryWrapper<Consumption>().eq(Consumption::getStudentId, studentId));
        BigDecimal total = consumes.stream().map(Consumption::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        long days = consumes.stream().map(c -> c.getConsumeTime().toLocalDate()).distinct().count();
        if (days > 0) {
            vo.setAvgDailyConsume(total.divide(BigDecimal.valueOf(days), 2, RoundingMode.HALF_UP));
        } else {
            vo.setAvgDailyConsume(BigDecimal.ZERO);
        }

        // Consume type distribution
        Map<String, BigDecimal> typeAmount = new HashMap<>();
        for (Consumption c : consumes) {
            typeAmount.merge(c.getType(), c.getAmount(), BigDecimal::add);
        }
        List<Map<String, Object>> consumeDist = new ArrayList<>();
        typeAmount.forEach((t, a) -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("type", t);
            m.put("amount", a);
            consumeDist.add(m);
        });
        vo.setConsumeTypeDistribution(consumeDist);

        return vo;
    }

    private String calculateRisk(BigDecimal avgGpa, int failCount) {
        if (failCount >= 2 || avgGpa.compareTo(BigDecimal.valueOf(2.0)) < 0) return "HIGH";
        if (failCount == 1) return "MEDIUM";
        return "LOW";
    }

    private String buildRiskReason(BigDecimal avgGpa, int failCount) {
        if (failCount >= 2) return "挂科数≥2，属于高风险";
        if (avgGpa.compareTo(BigDecimal.valueOf(2.0)) < 0) return "平均绩点<2.0，属于高风险";
        if (failCount == 1) return "挂科数=1，属于中风险";
        return "学业状态正常，属于低风险";
    }
}
