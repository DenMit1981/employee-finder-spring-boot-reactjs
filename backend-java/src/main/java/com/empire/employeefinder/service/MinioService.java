package com.empire.employeefinder.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface MinioService {

    String upload(MultipartFile file);

    void upload(String fileName, InputStream inputStream);

    InputStream download(String fileName);

    void delete(String path);
}
