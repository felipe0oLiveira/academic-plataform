package com.academicplatform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de resposta para Comment.
 * 
 * @author Felipe Oliveira
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {

    private Long id;
    private String content;
    private Long userId;
    private String userName;
    private Long fileId;
    private String fileName;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

