package com.example.academic.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.academic.dto.ApiResponse;
import com.example.academic.dto.StudentDetailVO;
import com.example.academic.dto.StudentVO;
import com.example.academic.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "学生画像", description = "学生列表及详情查询")
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    @Operation(summary = "学生列表（分页、搜索、风险筛选）")
    public ApiResponse<Page<StudentVO>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String riskLevel,
            @RequestParam(required = false) String className) {
        return ApiResponse.success(studentService.listStudents(page, size, keyword, riskLevel, className));
    }

    @GetMapping("/{studentId}")
    @Operation(summary = "学生详情")
    public ApiResponse<StudentDetailVO> detail(@PathVariable String studentId) {
        return ApiResponse.success(studentService.getStudentDetail(studentId));
    }
}
