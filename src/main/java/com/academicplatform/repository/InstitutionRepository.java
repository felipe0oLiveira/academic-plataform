package com.academicplatform.repository;

import com.academicplatform.entity.Institution;
import com.academicplatform.enums.PlanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações de persistência da entidade Institution.
 * 
 * @author Felipe Oliveira
 */
@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Long> {

    /**
     * Busca instituição por nome exato.
     */
    Optional<Institution> findByName(String name);

    /**
     * Busca instituição por código único.
     */
    Optional<Institution> findByCode(String code);

    /**
     * Busca todas as instituições ativas.
     */
    List<Institution> findByActiveTrue();

    /**
     * Busca instituições por tipo de plano.
     */
    List<Institution> findByPlan(PlanType plan);

    /**
     * Verifica se existe instituição com o nome informado.
     */
    boolean existsByName(String name);

    /**
     * Verifica se existe instituição com o código informado.
     */
    boolean existsByCode(String code);

    /**
     * Busca instituições que têm plano expirado.
     * Compara expiresAt com a data atual.
     */
    @Query("SELECT i FROM Institution i WHERE i.expiresAt IS NOT NULL AND i.expiresAt < CURRENT_TIMESTAMP")
    List<Institution> findExpiredInstitutions();

    /**
     * Conta o número de instituições ativas.
     */
    @Query("SELECT COUNT(i) FROM Institution i WHERE i.active = true")
    long countActiveInstitutions();
}
