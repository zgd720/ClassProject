package com.example.academic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.academic.entity.*;
import com.example.academic.mapper.*;
import com.example.academic.service.DataImportService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DataImportServiceImpl implements DataImportService {

    private final StudentMapper studentMapper;
    private final CourseScoreMapper courseScoreMapper;
    private final LibraryBorrowMapper libraryBorrowMapper;
    private final ConsumptionMapper consumptionMapper;

    @Override
    @Transactional
    public Map<String, Integer> importCsv(MultipartFile file, String type) {
        int count = 0;
        try (CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader()
                .parse(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            switch (type) {
                case "student" -> {
                    List<Student> list = new ArrayList<>();
                    for (CSVRecord r : parser) {
                        Student s = new Student();
                        s.setStudentId(r.get("student_id"));
                        s.setName(r.get("name"));
                        s.setGender(r.get("gender"));
                        s.setMajor(r.get("major"));
                        s.setClassName(r.get("class_name"));
                        s.setEnrollmentYear(Integer.parseInt(r.get("enrollment_year")));
                        list.add(s);
                    }
                    for (Student s : list) studentMapper.insert(s);
                    count = list.size();
                }
                case "course_score" -> {
                    List<CourseScore> list = new ArrayList<>();
                    for (CSVRecord r : parser) {
                        CourseScore cs = new CourseScore();
                        cs.setStudentId(r.get("student_id"));
                        cs.setSemester(r.get("semester"));
                        cs.setCourseName(r.get("course_name"));
                        cs.setScore(new BigDecimal(r.get("score")));
                        cs.setGpa(new BigDecimal(r.get("gpa")));
                        list.add(cs);
                    }
                    for (CourseScore cs : list) courseScoreMapper.insert(cs);
                    count = list.size();
                }
                case "library_borrow" -> {
                    List<LibraryBorrow> list = new ArrayList<>();
                    for (CSVRecord r : parser) {
                        LibraryBorrow lb = new LibraryBorrow();
                        lb.setStudentId(r.get("student_id"));
                        lb.setBorrowDate(LocalDate.parse(r.get("borrow_date")));
                        lb.setBookName(r.get("book_name"));
                        lb.setCategory(r.get("category"));
                        list.add(lb);
                    }
                    for (LibraryBorrow lb : list) libraryBorrowMapper.insert(lb);
                    count = list.size();
                }
                case "consumption" -> {
                    List<Consumption> list = new ArrayList<>();
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    for (CSVRecord r : parser) {
                        Consumption c = new Consumption();
                        c.setStudentId(r.get("student_id"));
                        c.setConsumeTime(LocalDateTime.parse(r.get("consume_time"), fmt));
                        c.setAmount(new BigDecimal(r.get("amount")));
                        c.setType(r.get("type"));
                        list.add(c);
                    }
                    for (Consumption c : list) consumptionMapper.insert(c);
                    count = list.size();
                }
                default -> throw new RuntimeException("未知的数据类型: " + type);
            }
        } catch (Exception e) {
            throw new RuntimeException("CSV解析失败: " + e.getMessage());
        }
        return Map.of("imported", count);
    }

    @Override
    public List<Map<String, Object>> preview(String type, int limit) {
        // Return last N records for preview
        return switch (type) {
            case "student" -> mapStudents(studentMapper.selectList(
                    new LambdaQueryWrapper<Student>().last("LIMIT " + limit)));
            case "course_score" -> mapScores(courseScoreMapper.selectList(
                    new LambdaQueryWrapper<CourseScore>().last("LIMIT " + limit)));
            case "library_borrow" -> mapBorrows(libraryBorrowMapper.selectList(
                    new LambdaQueryWrapper<LibraryBorrow>().last("LIMIT " + limit)));
            case "consumption" -> mapConsumptions(consumptionMapper.selectList(
                    new LambdaQueryWrapper<Consumption>().last("LIMIT " + limit)));
            default -> List.of();
        };
    }

    @Override
    @Transactional
    public void clearAll() {
        consumptionMapper.delete(new LambdaQueryWrapper<>());
        libraryBorrowMapper.delete(new LambdaQueryWrapper<>());
        courseScoreMapper.delete(new LambdaQueryWrapper<>());
        riskLogMapper.delete(new LambdaQueryWrapper<>());
        studentMapper.delete(new LambdaQueryWrapper<>());
    }

    private final RiskLogMapper riskLogMapper;

    private List<Map<String, Object>> mapStudents(List<Student> list) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Student s : list) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("student_id", s.getStudentId());
            m.put("name", s.getName());
            m.put("gender", s.getGender());
            m.put("major", s.getMajor());
            m.put("class_name", s.getClassName());
            m.put("enrollment_year", s.getEnrollmentYear());
            result.add(m);
        }
        return result;
    }

    private List<Map<String, Object>> mapScores(List<CourseScore> list) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (CourseScore cs : list) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("student_id", cs.getStudentId());
            m.put("semester", cs.getSemester());
            m.put("course_name", cs.getCourseName());
            m.put("score", cs.getScore());
            m.put("gpa", cs.getGpa());
            result.add(m);
        }
        return result;
    }

    private List<Map<String, Object>> mapBorrows(List<LibraryBorrow> list) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (LibraryBorrow lb : list) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("student_id", lb.getStudentId());
            m.put("borrow_date", lb.getBorrowDate());
            m.put("book_name", lb.getBookName());
            m.put("category", lb.getCategory());
            result.add(m);
        }
        return result;
    }

    private List<Map<String, Object>> mapConsumptions(List<Consumption> list) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Consumption c : list) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("student_id", c.getStudentId());
            m.put("consume_time", c.getConsumeTime());
            m.put("amount", c.getAmount());
            m.put("type", c.getType());
            result.add(m);
        }
        return result;
    }
}
