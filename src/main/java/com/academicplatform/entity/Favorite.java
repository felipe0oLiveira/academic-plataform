package com.academicplatform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Representa a relação de favorito entre usuário e arquivo.
 * Constraint única garante que um usuário só pode favoritar um arquivo uma vez.
 * 
 * @author Felipe Oliveira
 */
@Entity
@Table(name = "favorites",
    uniqueConstraints = @UniqueConstraint(name = "uk_favorite_user_file", columnNames = {"user_id", "file_id"}),
    indexes = {
        @Index(name = "idx_favorite_user", columnList = "user_id"),
        @Index(name = "idx_favorite_file", columnList = "file_id")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false, of = {"user", "file"})
@ToString(exclude = {"user", "file"})
public class Favorite extends BaseEntity {

    /**
     * Usuário que favoritou o arquivo.
     */
    @NotNull(message = "Usuário é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_favorite_user"))
    private User user;

    /**
     * Arquivo favoritado pelo usuário.
     */
    @NotNull(message = "Arquivo é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false, foreignKey = @ForeignKey(name = "fk_favorite_file"))
    private File file;
}
