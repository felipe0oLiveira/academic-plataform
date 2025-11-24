package com.academicplatform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criação de Comment.
 * 
 * @author Felipe Oliveira
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequest {

    @NotBlank(message = "Conteúdo do comentário é obrigatório")
    @Size(max = 2000, message = "Comentário deve ter no máximo 2000 caracteres")
    private String content;

    @NotNull(message = "ID do arquivo é obrigatório")
    private Long fileId;
}

