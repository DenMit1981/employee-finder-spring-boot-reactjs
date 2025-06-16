package com.empire.employeefinder.mapper;

import com.empire.employeefinder.dto.response.ResumeResponseDto;
import com.empire.employeefinder.model.Resume;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.text.NumberFormat;
import java.util.Locale;

@Mapper(componentModel = "spring")
public interface ResumeMapper {

    @Mapping(source = "resume.file.originalFileName", target = "fileName")
    @Mapping(source = "resume.file.fileSize", target = "fileSize", qualifiedByName = "getSizeInByte")
    ResumeResponseDto toDto(Resume resume);

    @Named("getSizeInByte")
    default String getSizeInByte(Long size) {
        if (size == null) {
            return "";
        }

        String[] units = new String[]{"B", "KB", "MB"};
        int unitIndex = (int) (Math.log10(size) / 3);
        double sizeInUnit = size / Math.pow(1024, unitIndex);
        NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
        nf.setMaximumFractionDigits(1);

        return String.format("%s %s", nf.format(sizeInUnit), units[unitIndex]);
    }
}
