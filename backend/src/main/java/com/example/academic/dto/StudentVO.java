package com.example.academic.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class StudentVO {
    private String studentId;
    private String name;
    private String gender;
    private String major;
    private String className;
    private Integer enrollmentYear;
    private BigDecimal avgGpa;
    private Integer failCount;
    private String riskLevel;
    private String riskReason;
    private Integer borrowCount;
    private BigDecimal avgDailyConsume;
}
