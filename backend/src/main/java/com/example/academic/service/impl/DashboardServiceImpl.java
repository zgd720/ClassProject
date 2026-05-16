package com.example.academic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.academic.dto.DashboardVO;
import com.example.academic.entity.*;
import com.example.academic.mapper.*;
import com.example.academic.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final StudentMapper studentMapper;
    private final CourseScoreMapper courseScoreMapper;
    private final LibraryBorrowMapper libraryBorrowMapper;
    private final ConsumptionMapper consumptionMapper;

    @Override
    public DashboardVO getDashboard() {
        DashboardVO vo = new DashboardVO();

        // Risk distribution
        List<Student> students = studentMapper.selectList(new LambdaQueryWrapper<>());
        Map<String, Long> riskDist = new LinkedHashMap<>();
        riskDist.put("HIGH", 0L);
        riskDist.put("MEDIUM", 0L);
        riskDist.put("LOW", 0L);
        for (Student s : students) {
            List<CourseScore> scores = courseScoreMapper.selectList(
                    new LambdaQueryWrapper<CourseScore>().eq(CourseScore::getStudentId, s.getStudentId()));
            BigDecimal avgGpa = scores.stream().map(CourseScore::getGpa).filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (!scores.isEmpty()) avgGpa = avgGpa.divide(BigDecimal.valueOf(scores.size()), 2, RoundingMode.HALF_UP);
            long fail = scores.stream().filter(cs -> cs.getScore() != null && cs.getScore().compareTo(BigDecimal.valueOf(60)) < 0).count();
            if (fail >= 2 || avgGpa.compareTo(BigDecimal.valueOf(2.0)) < 0) riskDist.merge("HIGH", 1L, Long::sum);
            else if (fail == 1) riskDist.merge("MEDIUM", 1L, Long::sum);
            else riskDist.merge("LOW", 1L, Long::sum);
        }
        List<Map<String, Object>> riskList = new ArrayList<>();
        riskDist.forEach((k, v) -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("level", k);
            m.put("count", v);
            riskList.add(m);
        });
        vo.setRiskDistribution(riskList);

        // GPA trend by semester
        vo.setGpaTrend(courseScoreMapper.selectAvgGpaBySemester());

        // Borrow category
        vo.setBorrowCategoryDistribution(libraryBorrowMapper.selectCountByCategory());

        // Consumption hourly
        vo.setConsumptionHourly(consumptionMapper.selectAmountByHour());

        return vo;
    }
}
