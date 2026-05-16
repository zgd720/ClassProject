package com.example.academic.controller;

import com.example.academic.dto.ApiResponse;
import com.example.academic.service.DataImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
@Tag(name = "数据管理", description = "CSV数据导入、预览、清空")
public class DataImportController {

    private final DataImportService dataImportService;

    @PostMapping("/import")
    @Operation(summary = "导入CSV文件")
    public ApiResponse<Map<String, Integer>> importCsv(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type) {
        return ApiResponse.success(dataImportService.importCsv(file, type));
    }

    @GetMapping("/preview")
    @Operation(summary = "预览导入数据")
    public ApiResponse<List<Map<String, Object>>> preview(
            @RequestParam("type") String type,
            @RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.success(dataImportService.preview(type, limit));
    }

    @DeleteMapping("/clear")
    @Operation(summary = "清空所有业务数据")
    public ApiResponse<Void> clearAll() {
        dataImportService.clearAll();
        return ApiResponse.success("数据已清空", null);
    }
}
