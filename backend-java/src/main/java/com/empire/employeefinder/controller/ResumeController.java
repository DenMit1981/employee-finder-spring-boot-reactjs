package com.empire.employeefinder.controller;

import com.empire.employeefinder.dto.response.ResumeDownloadResponseDto;
import com.empire.employeefinder.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/resumes")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Resume controller")
public class ResumeController {

    private final ResumeService resumeService;

    @GetMapping("/download/{employeeId}")
    @Operation(summary = "Download resume for employee (pdf/doc/docx)")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ByteArrayResource> download(@PathVariable Long employeeId) {
        ResumeDownloadResponseDto resumeDownloadDto = resumeService.download(employeeId);
        MediaType mediaType = resumeService.getMediaType(resumeDownloadDto.getFileName());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resumeDownloadDto.getFileName() + "\"")
                .contentType(mediaType)
                .body(resumeDownloadDto.getResource());
    }

    @DeleteMapping("remove/{employeeId}")
    @Operation(summary = "Delete resume by employee ID")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteResume(@PathVariable Long employeeId) {
        resumeService.deleteResume(employeeId);
    }
}
