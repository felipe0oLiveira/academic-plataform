package com.academicplatform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de resposta para Discipline.
 * 
 * @author Felipe Oliveira
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisciplineResponse {

    private Long id;
    private String name;
    private String code;
    private String description;
    private Long institutionId;
    private String institutionName;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long totalFiles;
    private Long totalStorageUsedBytes;
}

