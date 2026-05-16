package com.example.academic.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface DataImportService {
    Map<String, Integer> importCsv(MultipartFile file, String type);
    List<Map<String, Object>> preview(String type, int limit);
    void clearAll();
}
