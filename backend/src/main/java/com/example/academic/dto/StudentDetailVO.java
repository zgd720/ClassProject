package com.example.academic.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class StudentDetailVO {
    private String studentId;
    private String name;
    private String gender;
    private String major;
    private String className;
    private Integer enrollmentYear;
    private BigDecimal avgGpa;
    private Integer failCount;
    private Integer totalBorrowCount;
    private Integer recentBorrowCount;
    private BigDecimal avgDailyConsume;
    private String riskLevel;
    private String riskReason;
    private List<Map<String, Object>> gpaTrend;
    private List<Map<String, Object>> consumeTypeDistribution;
    private List<Map<String, Object>> borrowCategoryDistribution;
}
