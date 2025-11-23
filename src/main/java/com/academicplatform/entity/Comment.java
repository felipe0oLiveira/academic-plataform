package com.academicplatform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Representa um comentário feito por um usuário em um arquivo.
 * Utiliza soft delete (campo active) para moderação sem perder histórico.
 * 
 * @author Felipe Oliveira
 */
@Entity
@Table(name = "comments",
    indexes = {
        @Index(name = "idx_comment_user", columnList = "user_id"),
        @Index(name = "idx_comment_file", columnList = "file_id"),
        @Index(name = "idx_comment_active", columnList = "active"),
        @Index(name = "idx_comment_created", columnList = "created_at")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"user", "file"})
public class Comment extends BaseEntity {

    @NotBlank(message = "Conteúdo do comentário é obrigatório")
    @Size(max = 2000, message = "Comentário deve ter no máximo 2000 caracteres")
    @Column(nullable = false, length = 2000)
    private String content;

    /**
     * Usuário autor do comentário.
     */
    @NotNull(message = "Usuário é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_comment_user"))
    private User user;

    /**
     * Arquivo ao qual o comentário se refere.
     */
    @NotNull(message = "Arquivo é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false, foreignKey = @ForeignKey(name = "fk_comment_file"))
    private File file;

    /**
     * Indica se o comentário está ativo. false = soft delete (oculto).
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    /**
     * Desativa o comentário (soft delete).
     * O comentário permanece no banco mas não é exibido.
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * Reativa um comentário previamente desativado.
     * Torna o comentário visível novamente.
     */
    public void activate() {
        this.active = true;
    }
}
