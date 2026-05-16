package com.example.academic.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiSuggestionRequest {
    @NotBlank(message = "学号不能为空")
    private String studentId;
}
