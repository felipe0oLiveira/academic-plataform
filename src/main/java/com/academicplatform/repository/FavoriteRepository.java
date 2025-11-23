package com.academicplatform.repository;

import com.academicplatform.entity.Favorite;
import com.academicplatform.entity.File;
import com.academicplatform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações de persistência da entidade Favorite.
 * 
 * @author Felipe Oliveira
 */
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    /**
     * Busca favoritos de um usuário.
     */
    List<Favorite> findByUser(User user);

    /**
     * Busca favoritos de um arquivo.
     */
    List<Favorite> findByFile(File file);

    /**
     * Verifica se o usuário já favoritou o arquivo.
     * Usa constraint única user_id + file_id.
     */
    Optional<Favorite> findByUserAndFile(User user, File file);

    /**
     * Verifica se existe favorito para o usuário e arquivo.
     */
    boolean existsByUserAndFile(User user, File file);

    /**
     * Conta quantos usuários favoritaram um arquivo.
     */
    @Query("SELECT COUNT(f) FROM Favorite f WHERE f.file = :file")
    long countByFile(@Param("file") File file);

    /**
     * Conta quantos arquivos um usuário favoritou.
     */
    @Query("SELECT COUNT(f) FROM Favorite f WHERE f.user = :user")
    long countByUser(@Param("user") User user);

    /**
     * Busca favoritos de um usuário ordenados por data de criação (mais recentes primeiro).
     */
    @Query("SELECT f FROM Favorite f WHERE f.user = :user ORDER BY f.createdAt DESC")
    List<Favorite> findByUserOrderByCreatedAtDesc(@Param("user") User user);
}

