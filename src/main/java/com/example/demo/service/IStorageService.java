package com.example.demo.service;

import org.springframework.web.multipart.MultipartFile;

public interface IStorageService {
    String storeFile(MultipartFile file);
    byte[] readFileContent(String fileName);
    void deleteFile(String fileName);
}