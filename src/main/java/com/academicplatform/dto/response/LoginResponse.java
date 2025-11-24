package com.academicplatform.dto.response;

import com.academicplatform.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de resposta para login bem-sucedido.
 * Contém o token JWT e informações básicas do usuário.
 * 
 * @author Felipe Oliveira
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String token;
    @Builder.Default
    private String type = "Bearer";
    private Long userId;
    private String userName;
    private String email;
    private UserRole role;
    private Long institutionId;
    private String institutionName;
}

