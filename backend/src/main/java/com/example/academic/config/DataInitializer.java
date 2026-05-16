package com.example.academic.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.academic.entity.User;
import com.example.academic.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userMapper.selectCount(new LambdaQueryWrapper<>()) == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            admin.setName("系统管理员");
            userMapper.insert(admin);

            User counselor = new User();
            counselor.setUsername("counselor");
            counselor.setPassword(passwordEncoder.encode("123456"));
            counselor.setRole("COUNSELOR");
            counselor.setName("李辅导员");
            counselor.setClassName("软件工程2101");
            userMapper.insert(counselor);

            User student = new User();
            student.setUsername("student01");
            student.setPassword(passwordEncoder.encode("123456"));
            student.setRole("STUDENT");
            student.setName("张三");
            student.setStudentId("20210101001");
            userMapper.insert(student);
        }
    }
}
