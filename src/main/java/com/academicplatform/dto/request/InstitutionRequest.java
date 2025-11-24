package com.academicplatform.dto.request;

import com.academicplatform.enums.PlanType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para criação e atualização de Institution.
 * 
 * @author Felipe Oliveira
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstitutionRequest {

    @NotBlank(message = "Nome da instituição é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String name;

    @Size(max = 50, message = "Código deve ter no máximo 50 caracteres")
    private String code;

    @Size(max = 200, message = "Descrição deve ter no máximo 200 caracteres")
    private String description;

    @NotNull(message = "Tipo de plano é obrigatório")
    private PlanType plan;

    @NotNull(message = "Limite de usuários é obrigatório")
    @Positive(message = "Limite de usuários deve ser positivo")
    private Integer maxUsers;

    @NotNull(message = "Limite de armazenamento é obrigatório")
    @Positive(message = "Limite de armazenamento deve ser positivo")
    private Integer maxStorageGB;

    private LocalDateTime expiresAt;

    @NotNull(message = "Status ativo é obrigatório")
    private Boolean active;
}

