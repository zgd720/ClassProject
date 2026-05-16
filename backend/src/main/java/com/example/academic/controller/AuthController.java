package com.example.academic.controller;

import com.example.academic.dto.ApiResponse;
import com.example.academic.dto.LoginRequest;
import com.example.academic.dto.LoginResponse;
import com.example.academic.service.UserService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户登录认证相关接口")
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(userService.login(request.getUsername(), request.getPassword()));
    }

    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息")
    public ApiResponse<Map<String, Object>> currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return ApiResponse.error(401, "未登录");
        Claims claims = (Claims) auth.getDetails();
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("username", claims.getSubject());
        info.put("role", claims.get("role", String.class));
        info.put("name", claims.get("name", String.class));
        return ApiResponse.success(info);
    }
}
