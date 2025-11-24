package com.academicplatform.dto.response;

import com.academicplatform.enums.FileStatus;
import com.academicplatform.enums.FileType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de resposta para File.
 * 
 * @author Felipe Oliveira
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileResponse {

    private Long id;
    private String title;
    private String fileName;
    private FileType fileType;
    private Long fileSize;
    private String filePath;
    private String description;
    private Long disciplineId;
    private String disciplineName;
    private Long institutionId;
    private String institutionName;
    private Long uploadedById;
    private String uploadedByName;
    private FileStatus status;
    private Integer downloadCount;
    private LocalDateTime approvedAt;
    private String version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long favoritesCount;
    private Long commentsCount;
}

