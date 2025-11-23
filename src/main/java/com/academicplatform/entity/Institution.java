package com.academicplatform.entity;

import com.academicplatform.enums.PlanType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa uma instituição de ensino (multi-tenant).
 * Cada instituição possui usuários, disciplinas e arquivos isolados.
 * 
 * @author Felipe Oliveira
 */
@Entity
@Table(name = "institutions", 
    uniqueConstraints = @UniqueConstraint(name = "uk_institution_name", columnNames = "name"),
    indexes = {
        @Index(name = "idx_institution_code", columnList = "code"),
        @Index(name = "idx_institution_active", columnList = "active"),
        @Index(name = "idx_institution_plan", columnList = "plan")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"users", "disciplines"})
public class Institution extends BaseEntity {

    private static final int DEFAULT_MAX_USERS = 10;
    private static final int DEFAULT_MAX_STORAGE_GB = 5;

    @NotBlank(message = "Nome da instituição é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Size(max = 50, message = "Código deve ter no máximo 50 caracteres")
    @Column(length = 50)
    private String code;

    @Size(max = 200, message = "Descrição deve ter no máximo 200 caracteres")
    @Column(length = 200)
    private String description;

    /**
     * Tipo de plano determina limites e funcionalidades disponíveis.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PlanType plan = PlanType.FREE;

    /**
     * Limite máximo de usuários permitidos. Baseado no plano contratado.
     */
    @NotNull(message = "Limite de usuários é obrigatório")
    @Positive(message = "Limite de usuários deve ser positivo")
    @Column(nullable = false)
    @Builder.Default
    private Integer maxUsers = DEFAULT_MAX_USERS;

    /**
     * Limite máximo de armazenamento em GB. Baseado no plano contratado.
     */
    @NotNull(message = "Limite de armazenamento é obrigatório")
    @Positive(message = "Limite de armazenamento deve ser positivo")
    @Column(nullable = false)
    @Builder.Default
    private Integer maxStorageGB = DEFAULT_MAX_STORAGE_GB;

    /**
     * Data de expiração do plano. Se null, plano sem expiração.
     */
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @OneToMany(mappedBy = "institution", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Builder.Default
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "institution", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Discipline> disciplines = new ArrayList<>();

    /**
     * Verifica se o plano expirou comparando expiresAt com a data atual.
     * Retorna false se expiresAt for null (plano sem expiração).
     */
    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }

    /**
     * Verifica se o número de usuários atingiu o limite máximo.
     * Compara o tamanho da lista de usuários com maxUsers.
     */
    public boolean hasReachedUserLimit() {
        return users.size() >= maxUsers;
    }

    /**
     * Calcula o armazenamento total usado pela instituição em GB.
     * Percorre todas as disciplinas, seus arquivos e soma os tamanhos.
     * Converte bytes para GB dividindo por (1024 * 1024 * 1024).
     */
    public long getTotalStorageUsed() {
        return disciplines.stream()
                .flatMap(d -> d.getFiles().stream())
                .mapToLong(File::getFileSize)
                .sum() / (1024L * 1024L * 1024L);
    }
}
