package com.academicplatform.dto.response;

import com.academicplatform.enums.PlanType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de resposta para Institution.
 * 
 * @author Felipe Oliveira
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstitutionResponse {

    private Long id;
    private String name;
    private String code;
    private String description;
    private PlanType plan;
    private Integer maxUsers;
    private Integer maxStorageGB;
    private LocalDateTime expiresAt;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long totalUsers;
    private Long totalStorageUsedGB;
}

