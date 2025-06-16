package com.empire.employeefinder.service.impl;

import com.empire.employeefinder.dto.response.ResumeDownloadResponseDto;
import com.empire.employeefinder.exception.ResumeNotFoundException;
import com.empire.employeefinder.model.Employee;
import com.empire.employeefinder.model.File;
import com.empire.employeefinder.model.Resume;
import com.empire.employeefinder.repository.FileRepository;
import com.empire.employeefinder.repository.ResumeRepository;
import com.empire.employeefinder.service.MinioService;
import com.empire.employeefinder.service.ResumeService;
import com.empire.employeefinder.service.ValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final FileRepository fileRepository;
    private final MinioService minioService;
    private final ValidationService validationService;

    @Override
    public ResumeDownloadResponseDto download(Long employeeId) {
        File file = fileRepository.findByResume_EmployeeId(employeeId)
                .orElseThrow(ResumeNotFoundException::new);

        try (InputStream inputStream = minioService.download(file.getName())) {
            byte[] bytes = inputStream.readAllBytes();
            return new ResumeDownloadResponseDto(file.getOriginalFileName(), new ByteArrayResource(bytes));
        } catch (IOException e) {
            throw new RuntimeException("Error downloading file", e);
        }
    }

    @Override
    @Transactional
    public void deleteResume(Long employeeId) {
        Resume resume = resumeRepository.findByEmployeeId(employeeId).orElseThrow(ResumeNotFoundException::new);

        File file = resume.getFile();

        if (file != null) {
            minioService.delete(file.getPathFile());

            file.setResume(null);
            resume.setFile(null);

            fileRepository.delete(file);
        }

        Employee employee = resume.getEmployee();
        if (employee != null) {
            employee.setResume(null);
        }

        resume.setEmployee(null);
        resumeRepository.delete(resume);
    }

    @Override
    public void uploadResumeAndAttach(Employee employee, MultipartFile resumeFile) throws IOException {
        validationService.validateUploadFile(resumeFile);

        File file = createAndUploadFile(resumeFile);
        Resume resume = new Resume();
        resume.setEmployee(employee);
        resume.setFile(file);
        file.setResume(resume);
        employee.setResume(resume);

        resumeRepository.save(resume);
        fileRepository.save(file);
    }

    @Override
    public void replaceResume(Employee employee, MultipartFile resumeFile) throws IOException {
        validationService.validateUploadFile(resumeFile);

        Resume resume = employee.getResume();

        if (resume == null) {
            resume = new Resume();
            resume.setEmployee(employee);
            employee.setResume(resume);
            resumeRepository.save(resume);
        }

        Optional.ofNullable(resume.getFile()).ifPresent(oldFile -> {
            minioService.delete(oldFile.getPathFile());
            fileRepository.delete(oldFile);
        });

        File newFile = createAndUploadFile(resumeFile);
        newFile.setResume(resume);
        resume.setFile(newFile);

        fileRepository.save(newFile);
    }

    @Override
    public MediaType getMediaType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

        return switch (extension) {
            case "pdf" -> MediaType.APPLICATION_PDF;
            case "doc" -> MediaType.valueOf("application/msword");
            case "docx" -> MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }

    private File createAndUploadFile(MultipartFile resumeFile) throws IOException {
        String originalFilename = resumeFile.getOriginalFilename();
        String uniqueFileName = UUID.randomUUID() + "_" + originalFilename;

        minioService.upload(uniqueFileName, resumeFile.getInputStream());

        File file = new File();
        file.setName(uniqueFileName);
        file.setOriginalFileName(originalFilename);
        file.setPathFile(uniqueFileName);
        file.setFileSize(resumeFile.getSize());

        return file;
    }
}
