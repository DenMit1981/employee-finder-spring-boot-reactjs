package com.empire.employeefinder.service;

import com.empire.employeefinder.dto.response.ResumeDownloadResponseDto;
import com.empire.employeefinder.model.Employee;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ResumeService {

    ResumeDownloadResponseDto download(Long employeeId);

    void deleteResume(Long employeeId);

    void uploadResumeAndAttach(Employee employee, MultipartFile resumeFile) throws IOException;

    void replaceResume(Employee employee, MultipartFile resumeFile) throws IOException;

    MediaType getMediaType(String fileName);
}
