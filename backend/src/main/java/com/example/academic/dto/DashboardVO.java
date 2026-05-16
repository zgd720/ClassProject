package com.example.academic.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class DashboardVO {
    private List<Map<String, Object>> riskDistribution;
    private List<Map<String, Object>> gpaTrend;
    private List<Map<String, Object>> borrowCategoryDistribution;
    private List<Map<String, Object>> consumptionHourly;
}
