package com.empire.employeefinder.service.impl;

import com.empire.employeefinder.exception.FileSizeExceededException;
import com.empire.employeefinder.exception.InvalidFileTypeException;
import com.empire.employeefinder.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@Slf4j
public class ValidationServiceImpl implements ValidationService {

    private static final String EMPTY_VALUE = "";
    private static final String DOWNLOADABLE_FILE_FORMAT = "pdf|doc|docx";
    private static final String DOWNLOADABLE_FILE_FORMAT_ERROR_MESSAGE = "The selected file type is not allowed. Please select a file of " +
            "one of the following types: pdf, doc, docx";
    private static final Double ALLOWED_MAXIMUM_SIZE = 5.0;
    private static final String ALLOWED_MAXIMUM_SIZE_ERROR_MESSAGE = "The size of the attached file should not be greater than 5 Mb. " +
            "Please select another file.";

    @Override
    public void validateUploadFile(MultipartFile file) {
        if (file != null) {
            if (!getFileExtension(file).matches(DOWNLOADABLE_FILE_FORMAT)) {
                log.error(DOWNLOADABLE_FILE_FORMAT_ERROR_MESSAGE);
                throw new InvalidFileTypeException();
            }

            if (getFileSizeMegaBytes(file) > ALLOWED_MAXIMUM_SIZE) {
                log.error(ALLOWED_MAXIMUM_SIZE_ERROR_MESSAGE);
                throw new FileSizeExceededException();
            }
        }
    }

    private String getFileExtension(MultipartFile file) {
        return Optional.ofNullable(file.getOriginalFilename())
                .filter(name -> name.contains("."))
                .map(name -> name.substring(name.lastIndexOf('.') + 1))
                .orElse(EMPTY_VALUE);
    }

    private double getFileSizeMegaBytes(MultipartFile file) {
        return file.getSize() / (1024.0 * 1024.0);
    }
}
