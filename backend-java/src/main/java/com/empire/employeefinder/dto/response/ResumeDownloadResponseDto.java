package com.empire.employeefinder.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.core.io.ByteArrayResource;

@Data
@AllArgsConstructor
public class ResumeDownloadResponseDto {

    private String fileName;

    private ByteArrayResource resource;
}
