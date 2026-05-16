package com.example.academic.controller;

import com.example.academic.dto.AiSuggestionRequest;
import com.example.academic.dto.ApiResponse;
import com.example.academic.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI建议", description = "调用大模型生成个性化学习建议")
public class AiController {

    private final AiService aiService;

    @PostMapping("/suggestion")
    @Operation(summary = "生成AI学习建议")
    public ApiResponse<Map<String, String>> generateSuggestion(@Valid @RequestBody AiSuggestionRequest request) {
        String suggestion = aiService.generateSuggestion(request.getStudentId());
        return ApiResponse.success(Map.of("suggestion", suggestion));
    }
}
