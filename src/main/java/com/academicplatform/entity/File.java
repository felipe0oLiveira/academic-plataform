package com.academicplatform.entity;

import com.academicplatform.enums.FileStatus;
import com.academicplatform.enums.FileType;
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
 * Representa um arquivo acadêmico no sistema.
 * Passa por processo de aprovação antes de ficar disponível.
 * 
 * @author Felipe Oliveira
 */
@Entity
@Table(name = "files",
    indexes = {
        @Index(name = "idx_file_discipline", columnList = "discipline_id"),
        @Index(name = "idx_file_institution", columnList = "institution_id"),
        @Index(name = "idx_file_uploaded_by", columnList = "uploaded_by"),
        @Index(name = "idx_file_status", columnList = "status"),
        @Index(name = "idx_file_type", columnList = "file_type"),
        @Index(name = "idx_file_created", columnList = "created_at")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"discipline", "institution", "uploadedBy", "favorites", "comments"})
public class File extends BaseEntity {

    private static final int ZERO_DOWNLOADS = 0;

    @NotBlank(message = "Título é obrigatório")
    @Size(max = 200, message = "Título deve ter no máximo 200 caracteres")
    @Column(nullable = false, length = 200)
    private String title;

    /**
     * Nome original do arquivo enviado, incluindo extensão.
     */
    @NotBlank(message = "Nome do arquivo é obrigatório")
    @Size(max = 500, message = "Nome do arquivo deve ter no máximo 500 caracteres")
    @Column(name = "file_name", nullable = false, length = 500)
    private String fileName;

    /**
     * Tipo do arquivo determina o visualizador usado e ícone exibido.
     */
    @NotNull(message = "Tipo do arquivo é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false, length = 50)
    private FileType fileType;

    /**
     * Tamanho do arquivo em bytes. Usado para controle de armazenamento.
     */
    @NotNull(message = "Tamanho do arquivo é obrigatório")
    @Positive(message = "Tamanho do arquivo deve ser positivo")
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    /**
     * Caminho completo do arquivo no storage (local ou remoto como S3).
     */
    @Size(max = 500, message = "Caminho do arquivo deve ter no máximo 500 caracteres")
    @Column(name = "file_path", length = 500)
    private String filePath;

    @Size(max = 1000, message = "Descrição deve ter no máximo 1000 caracteres")
    @Column(length = 1000)
    private String description;

    /**
     * Disciplina à qual o arquivo pertence. Organiza arquivos por matéria.
     */
    @NotNull(message = "Disciplina é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discipline_id", nullable = false, foreignKey = @ForeignKey(name = "fk_file_discipline"))
    private Discipline discipline;

    /**
     * Instituição à qual o arquivo pertence. Fundamental para multi-tenant.
     */
    @NotNull(message = "Instituição é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false, foreignKey = @ForeignKey(name = "fk_file_institution"))
    private Institution institution;

    /**
     * Usuário que fez upload do arquivo. Registrado para auditoria.
     */
    @NotNull(message = "Usuário que fez upload é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false, foreignKey = @ForeignKey(name = "fk_file_user"))
    private User uploadedBy;

    /**
     * Status do arquivo no processo de aprovação.
     * PENDING: aguardando aprovação, não visível para alunos.
     * APPROVED: aprovado e disponível.
     * REJECTED: rejeitado e não disponível.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private FileStatus status = FileStatus.PENDING;

    /**
     * Contador de downloads. Incrementado a cada download bem-sucedido.
     */
    @Column(name = "download_count", nullable = false)
    @Builder.Default
    private Integer downloadCount = ZERO_DOWNLOADS;

    /**
     * Data de aprovação. Definida quando status muda para APPROVED.
     */
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    /**
     * Versão do arquivo para controle de versões (ex: "1.0", "2.1").
     */
    @Size(max = 20, message = "Versão deve ter no máximo 20 caracteres")
    @Column(length = 20)
    private String version;

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Favorite> favorites = new ArrayList<>();

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    /**
     * Verifica se o arquivo está aprovado (status == APPROVED).
     * Apenas arquivos aprovados são visíveis para alunos.
     */
    public boolean isApproved() {
        return status == FileStatus.APPROVED;
    }

    /**
     * Verifica se o arquivo está pendente (status == PENDING).
     * Arquivos pendentes aguardam revisão de um admin.
     */
    public boolean isPending() {
        return status == FileStatus.PENDING;
    }

    /**
     * Verifica se o arquivo foi rejeitado (status == REJECTED).
     * Arquivos rejeitados não são exibidos.
     */
    public boolean isRejected() {
        return status == FileStatus.REJECTED;
    }

    /**
     * Aprova o arquivo alterando status para APPROVED.
     * Registra a data de aprovação no momento da chamada.
     */
    public void approve() {
        this.status = FileStatus.APPROVED;
        this.approvedAt = LocalDateTime.now();
    }

    /**
     * Rejeita o arquivo alterando status para REJECTED.
     * Não atualiza approvedAt, pois não foi aprovado.
     */
    public void reject() {
        this.status = FileStatus.REJECTED;
    }

    /**
     * Incrementa o contador de downloads em 1.
     * Chamado a cada download bem-sucedido do arquivo.
     */
    public void incrementDownloadCount() {
        this.downloadCount++;
    }

    /**
     * Extrai a extensão do arquivo do fileName.
     * Busca o último ponto e retorna o texto após ele em minúsculas.
     * Se não encontrar ponto, retorna o nome do fileType em minúsculas.
     */
    public String getFileExtension() {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        }
        return fileType != null ? fileType.name().toLowerCase() : "";
    }
}
