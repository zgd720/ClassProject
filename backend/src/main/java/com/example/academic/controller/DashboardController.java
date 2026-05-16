package com.example.academic.controller;

import com.example.academic.dto.ApiResponse;
import com.example.academic.dto.DashboardVO;
import com.example.academic.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "可视化看板", description = "班级数据统计可视化")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @Operation(summary = "获取看板数据")
    public ApiResponse<DashboardVO> getDashboard() {
        return ApiResponse.success(dashboardService.getDashboard());
    }
}
