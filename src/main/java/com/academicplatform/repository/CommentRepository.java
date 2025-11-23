package com.academicplatform.repository;

import com.academicplatform.entity.Comment;
import com.academicplatform.entity.File;
import com.academicplatform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório para operações de persistência da entidade Comment.
 * 
 * @author Felipe Oliveira
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Busca comentários de um arquivo.
     */
    List<Comment> findByFile(File file);

    /**
     * Busca comentários ativos de um arquivo (não soft deleted).
     */
    List<Comment> findByFileAndActiveTrue(File file);

    /**
     * Busca comentários de um usuário.
     */
    List<Comment> findByUser(User user);

    /**
     * Busca comentários ativos de um usuário.
     */
    List<Comment> findByUserAndActiveTrue(User user);

    /**
     * Busca comentários ativos de um arquivo ordenados por data (mais recentes primeiro).
     */
    @Query("SELECT c FROM Comment c WHERE c.file = :file AND c.active = true ORDER BY c.createdAt DESC")
    List<Comment> findActiveCommentsByFileOrderByCreatedAtDesc(@Param("file") File file);

    /**
     * Busca comentários ativos de um arquivo ordenados por data (mais antigos primeiro).
     */
    @Query("SELECT c FROM Comment c WHERE c.file = :file AND c.active = true ORDER BY c.createdAt ASC")
    List<Comment> findActiveCommentsByFileOrderByCreatedAtAsc(@Param("file") File file);

    /**
     * Conta comentários ativos de um arquivo.
     */
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.file = :file AND c.active = true")
    long countActiveCommentsByFile(@Param("file") File file);

    /**
     * Conta comentários de um usuário.
     */
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.user = :user")
    long countByUser(@Param("user") User user);
}

