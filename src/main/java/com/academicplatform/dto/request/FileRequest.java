package com.academicplatform.dto.request;

import com.academicplatform.enums.FileType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criação e atualização de File.
 * 
 * @author Felipe Oliveira
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileRequest {

    @NotBlank(message = "Título é obrigatório")
    @Size(max = 200, message = "Título deve ter no máximo 200 caracteres")
    private String title;

    @NotBlank(message = "Nome do arquivo é obrigatório")
    @Size(max = 500, message = "Nome do arquivo deve ter no máximo 500 caracteres")
    private String fileName;

    @NotNull(message = "Tipo do arquivo é obrigatório")
    private FileType fileType;

    @NotNull(message = "Tamanho do arquivo é obrigatório")
    @Positive(message = "Tamanho do arquivo deve ser positivo")
    private Long fileSize;

    @Size(max = 500, message = "Caminho do arquivo deve ter no máximo 500 caracteres")
    private String filePath;

    @Size(max = 1000, message = "Descrição deve ter no máximo 1000 caracteres")
    private String description;

    @NotNull(message = "ID da disciplina é obrigatório")
    private Long disciplineId;

    @Size(max = 20, message = "Versão deve ter no máximo 20 caracteres")
    private String version;
}

