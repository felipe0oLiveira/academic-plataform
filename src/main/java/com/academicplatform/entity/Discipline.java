package com.academicplatform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa uma disciplina/matéria oferecida pela instituição.
 * Organiza arquivos acadêmicos por matéria.
 * 
 * @author Felipe Oliveira
 */
@Entity
@Table(name = "disciplines",
    indexes = {
        @Index(name = "idx_discipline_code", columnList = "code"),
        @Index(name = "idx_discipline_institution", columnList = "institution_id"),
        @Index(name = "idx_discipline_active", columnList = "active")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"institution", "files"})
public class Discipline extends BaseEntity {

    @NotBlank(message = "Nome da disciplina é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Código único da disciplina para identificação rápida (ex: "ANAT01").
     */
    @Size(max = 20, message = "Código deve ter no máximo 20 caracteres")
    @Column(length = 20)
    private String code;

    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    @Column(length = 500)
    private String description;

    /**
     * Instituição à qual a disciplina pertence. Fundamental para multi-tenant.
     */
    @NotNull(message = "Instituição é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false, foreignKey = @ForeignKey(name = "fk_discipline_institution"))
    private Institution institution;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @OneToMany(mappedBy = "discipline", fetch = FetchType.LAZY)
    @Builder.Default
    private List<File> files = new ArrayList<>();

    /**
     * Retorna a quantidade total de arquivos associados à disciplina.
     * Simplesmente retorna o tamanho da lista de arquivos.
     */
    public long getTotalFilesCount() {
        return files.size();
    }

    /**
     * Calcula o armazenamento total usado pelos arquivos da disciplina em bytes.
     * Soma o fileSize de todos os arquivos usando stream.
     */
    public long getTotalStorageUsed() {
        return files.stream()
                .mapToLong(File::getFileSize)
                .sum();
    }
}
