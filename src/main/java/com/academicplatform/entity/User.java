package com.academicplatform.entity;

import com.academicplatform.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa um usuário do sistema acadêmico.
 * Vinculado a uma instituição para isolamento multi-tenant.
 * 
 * @author Felipe Oliveira
 */
@Entity
@Table(name = "users", 
    uniqueConstraints = @UniqueConstraint(name = "uk_user_email", columnNames = "email"),
    indexes = {
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_institution", columnList = "institution_id"),
        @Index(name = "idx_user_active", columnList = "active"),
        @Index(name = "idx_user_role", columnList = "role")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"password", "institution", "uploadedFiles", "favorites", "comments"})
public class User extends BaseEntity {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Email único usado para login e autenticação.
     */
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * Senha criptografada. Nunca deve ser armazenada em texto plano.
     */
    @NotBlank(message = "Senha é obrigatória")
    @Column(nullable = false)
    private String password;

    /**
     * Role determina permissões e funcionalidades disponíveis.
     * Hierarquia: SUPER_ADMIN > ADMIN > TEACHER > STUDENT
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UserRole role = UserRole.STUDENT;

    /**
     * Instituição à qual o usuário pertence. Fundamental para multi-tenant.
     */
    @NotNull(message = "Instituição é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_institution"))
    private Institution institution;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    /**
     * Data do último login bem-sucedido. Atualizada após autenticação.
     */
    private LocalDateTime lastLogin;

    /**
     * Token temporário para recuperação de senha. Gerado ao solicitar reset.
     */
    @Size(max = 255)
    @Column(name = "reset_token", length = 255)
    private String resetToken;

    /**
     * Data de expiração do token de reset. Token válido apenas até esta data.
     */
    @Column(name = "reset_token_expires")
    private LocalDateTime resetTokenExpires;

    @OneToMany(mappedBy = "uploadedBy", fetch = FetchType.LAZY)
    @Builder.Default
    private List<File> uploadedFiles = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Favorite> favorites = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    /**
     * Verifica se o usuário é administrador (ADMIN ou SUPER_ADMIN).
     * Admins têm permissões de gerenciamento e aprovação.
     */
    public boolean isAdmin() {
        return role == UserRole.ADMIN || role == UserRole.SUPER_ADMIN;
    }

    /**
     * Verifica se o usuário é professor.
     * Professores podem fazer upload de arquivos e gerenciar disciplinas.
     */
    public boolean isTeacher() {
        return role == UserRole.TEACHER;
    }

    /**
     * Verifica se o usuário é aluno.
     * Alunos têm permissões de leitura e interação básica.
     */
    public boolean isStudent() {
        return role == UserRole.STUDENT;
    }

    /**
     * Verifica se o token de reset de senha é válido.
     * Valida se o token existe, se expiresAt não é null e se ainda não expirou.
     */
    public boolean hasResetTokenValid() {
        return resetToken != null && 
               resetTokenExpires != null && 
               resetTokenExpires.isAfter(LocalDateTime.now());
    }

    /**
     * Atualiza a data do último login para o momento atual.
     * Chamado após login bem-sucedido para auditoria.
     */
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }
}
