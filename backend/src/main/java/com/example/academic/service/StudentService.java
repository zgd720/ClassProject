package com.example.academic.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.academic.dto.StudentDetailVO;
import com.example.academic.dto.StudentVO;

public interface StudentService {
    Page<StudentVO> listStudents(Integer page, Integer size, String keyword, String riskLevel, String className);
    StudentDetailVO getStudentDetail(String studentId);
}
