package com.academicplatform.repository;

import com.academicplatform.entity.Discipline;
import com.academicplatform.entity.File;
import com.academicplatform.entity.Institution;
import com.academicplatform.entity.User;
import com.academicplatform.enums.FileStatus;
import com.academicplatform.enums.FileType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório para operações de persistência da entidade File.
 * 
 * @author Felipe Oliveira
 */
@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    /**
     * Busca arquivos por disciplina.
     */
    List<File> findByDiscipline(Discipline discipline);

    /**
     * Busca arquivos aprovados de uma disciplina.
     */
    List<File> findByDisciplineAndStatus(Discipline discipline, FileStatus status);

    /**
     * Busca arquivos por instituição.
     */
    List<File> findByInstitution(Institution institution);

    /**
     * Busca arquivos por status.
     */
    List<File> findByStatus(FileStatus status);

    /**
     * Busca arquivos por status e instituição.
     */
    List<File> findByStatusAndInstitution(FileStatus status, Institution institution);

    /**
     * Busca arquivos pendentes de aprovação de uma instituição.
     */
    @Query("SELECT f FROM File f WHERE f.institution = :institution AND f.status = 'PENDING' ORDER BY f.createdAt DESC")
    List<File> findPendingFilesByInstitution(@Param("institution") Institution institution);

    /**
     * Busca arquivos aprovados de uma disciplina (visíveis para alunos).
     */
    @Query("SELECT f FROM File f WHERE f.discipline = :discipline AND f.status = 'APPROVED' ORDER BY f.createdAt DESC")
    List<File> findApprovedFilesByDiscipline(@Param("discipline") Discipline discipline);

    /**
     * Busca arquivos por usuário que fez upload.
     */
    List<File> findByUploadedBy(User user);

    /**
     * Busca arquivos por tipo.
     */
    List<File> findByFileType(FileType fileType);

    /**
     * Busca arquivos por tipo e disciplina.
     */
    List<File> findByFileTypeAndDiscipline(FileType fileType, Discipline discipline);

    /**
     * Busca arquivos por título (case-insensitive, parcial).
     */
    @Query("SELECT f FROM File f WHERE LOWER(f.title) LIKE LOWER(CONCAT('%', :title, '%')) AND f.institution = :institution")
    List<File> findByTitleContainingIgnoreCaseAndInstitution(@Param("title") String title, @Param("institution") Institution institution);

    /**
     * Busca arquivos com paginação para uma disciplina.
     */
    Page<File> findByDisciplineAndStatus(Discipline discipline, FileStatus status, Pageable pageable);

    /**
     * Conta arquivos por status e instituição.
     */
    @Query("SELECT COUNT(f) FROM File f WHERE f.institution = :institution AND f.status = :status")
    long countByStatusAndInstitution(@Param("status") FileStatus status, @Param("institution") Institution institution);

    /**
     * Busca arquivos mais baixados de uma instituição.
     * Ordena por downloadCount descendente.
     */
    @Query("SELECT f FROM File f WHERE f.institution = :institution AND f.status = 'APPROVED' ORDER BY f.downloadCount DESC")
    List<File> findMostDownloadedFilesByInstitution(@Param("institution") Institution institution, Pageable pageable);
}

