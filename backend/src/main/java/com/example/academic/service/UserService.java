package com.example.academic.service;

import com.example.academic.dto.LoginResponse;

public interface UserService {
    LoginResponse login(String username, String password);
}
