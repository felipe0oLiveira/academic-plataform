package com.academicplatform.repository;

import com.academicplatform.entity.Discipline;
import com.academicplatform.entity.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações de persistência da entidade Discipline.
 * 
 * @author Felipe Oliveira
 */
@Repository
public interface DisciplineRepository extends JpaRepository<Discipline, Long> {

    /**
     * Busca disciplinas por instituição.
     */
    List<Discipline> findByInstitution(Institution institution);

    /**
     * Busca disciplinas ativas de uma instituição.
     */
    List<Discipline> findByInstitutionAndActiveTrue(Institution institution);

    /**
     * Busca disciplina por código único dentro de uma instituição.
     */
    Optional<Discipline> findByCodeAndInstitution(String code, Institution institution);

    /**
     * Busca disciplinas por nome (case-insensitive, parcial).
     */
    @Query("SELECT d FROM Discipline d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')) AND d.institution = :institution")
    List<Discipline> findByNameContainingIgnoreCaseAndInstitution(@Param("name") String name, @Param("institution") Institution institution);

    /**
     * Verifica se existe disciplina com o código na instituição.
     */
    boolean existsByCodeAndInstitution(String code, Institution institution);

    /**
     * Conta disciplinas ativas de uma instituição.
     */
    @Query("SELECT COUNT(d) FROM Discipline d WHERE d.institution = :institution AND d.active = true")
    long countActiveDisciplinesByInstitution(@Param("institution") Institution institution);
}

