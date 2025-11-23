package com.academicplatform.repository;

import com.academicplatform.entity.Institution;
import com.academicplatform.entity.User;
import com.academicplatform.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações de persistência da entidade User.
 * 
 * @author Felipe Oliveira
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca usuário por email. Email é único no sistema.
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica se existe usuário com o email informado.
     */
    boolean existsByEmail(String email);

    /**
     * Busca usuários por instituição.
     */
    List<User> findByInstitution(Institution institution);

    /**
     * Busca usuários ativos de uma instituição.
     */
    List<User> findByInstitutionAndActiveTrue(Institution institution);

    /**
     * Busca usuários por role.
     */
    List<User> findByRole(UserRole role);

    /**
     * Busca usuários por role e instituição.
     */
    List<User> findByRoleAndInstitution(UserRole role, Institution institution);

    /**
     * Busca usuários ativos por role e instituição.
     */
    List<User> findByRoleAndInstitutionAndActiveTrue(UserRole role, Institution institution);

    /**
     * Busca usuário por token de reset de senha.
     * Usado para validar token na recuperação de senha.
     */
    Optional<User> findByResetToken(String resetToken);

    /**
     * Conta usuários ativos de uma instituição.
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.institution = :institution AND u.active = true")
    long countActiveUsersByInstitution(@Param("institution") Institution institution);

    /**
     * Verifica se a instituição atingiu o limite de usuários.
     * Compara o número de usuários ativos com o limite da instituição.
     */
    @Query("SELECT COUNT(u) >= i.maxUsers FROM User u JOIN u.institution i WHERE i = :institution AND u.active = true")
    boolean hasReachedUserLimit(@Param("institution") Institution institution);
}

