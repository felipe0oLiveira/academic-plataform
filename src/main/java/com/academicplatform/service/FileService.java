package com.academicplatform.service;

import com.academicplatform.dto.request.FileRequest;
import com.academicplatform.dto.response.FileResponse;
import com.academicplatform.entity.Discipline;
import com.academicplatform.entity.File;
import com.academicplatform.entity.User;
import com.academicplatform.enums.FileStatus;
import com.academicplatform.repository.CommentRepository;
import com.academicplatform.repository.FavoriteRepository;
import com.academicplatform.repository.FileRepository;
import com.academicplatform.util.ServiceHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para operações de negócio relacionadas a File.
 * 
 * @author Felipe Oliveira
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FileService {

    private final FileRepository fileRepository;
    private final FavoriteRepository favoriteRepository;
    private final CommentRepository commentRepository;
    private final ServiceHelper serviceHelper;

    /**
     * Cria um novo arquivo.
     * O arquivo é criado com status PENDING e precisa ser aprovado.
     */
    public FileResponse create(FileRequest request, Long uploadedById) {
        Discipline discipline = serviceHelper.findDisciplineOrThrow(request.getDisciplineId());
        User uploadedBy = serviceHelper.findUserOrThrow(uploadedById);

        File file = File.builder()
                .title(request.getTitle())
                .fileName(request.getFileName())
                .fileType(request.getFileType())
                .fileSize(request.getFileSize())
                .filePath(request.getFilePath())
                .description(request.getDescription())
                .discipline(discipline)
                .institution(discipline.getInstitution())
                .uploadedBy(uploadedBy)
                .status(FileStatus.PENDING)
                .version(request.getVersion())
                .build();

        File saved = fileRepository.save(file);
        log.info("Arquivo criado: {}", saved.getId());

        return toResponse(saved);
    }

    /**
     * Busca arquivo por ID.
     */
    @Transactional(readOnly = true)
    public FileResponse findById(Long id) {
        File file = serviceHelper.findFileOrThrow(id);
        return toResponse(file);
    }

    /**
     * Lista arquivos aprovados de uma disciplina (visíveis para alunos).
     */
    @Transactional(readOnly = true)
    public List<FileResponse> findApprovedByDiscipline(Long disciplineId) {
        Discipline discipline = serviceHelper.findDisciplineOrThrow(disciplineId);

        return fileRepository.findApprovedFilesByDiscipline(discipline).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lista arquivos pendentes de aprovação de uma instituição (para admins).
     */
    @Transactional(readOnly = true)
    public List<FileResponse> findPendingByInstitution(Long institutionId) {
        return fileRepository.findByStatusAndInstitution(FileStatus.PENDING, 
                serviceHelper.findInstitutionOrThrow(institutionId)).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Aprova um arquivo, mudando seu status para APPROVED.
     */
    public FileResponse approve(Long id) {
        File file = serviceHelper.findFileOrThrow(id);
        file.setStatus(FileStatus.APPROVED);
        file.setApprovedAt(LocalDateTime.now());

        File saved = fileRepository.save(file);
        log.info("Arquivo aprovado: {}", saved.getId());

        return toResponse(saved);
    }

    /**
     * Rejeita um arquivo, mudando seu status para REJECTED.
     */
    public FileResponse reject(Long id) {
        File file = serviceHelper.findFileOrThrow(id);
        file.setStatus(FileStatus.REJECTED);

        File saved = fileRepository.save(file);
        log.info("Arquivo rejeitado: {}", saved.getId());

        return toResponse(saved);
    }

    /**
     * Incrementa o contador de downloads do arquivo.
     */
    public void incrementDownloadCount(Long id) {
        File file = serviceHelper.findFileOrThrow(id);

        file.setDownloadCount(file.getDownloadCount() + 1);
        fileRepository.save(file);
        log.debug("Download contado para arquivo: {}", id);
    }

    /**
     * Atualiza informações de um arquivo.
     */
    public FileResponse update(Long id, FileRequest request) {
        File file = serviceHelper.findFileOrThrow(id);
        Discipline discipline = serviceHelper.findDisciplineOrThrow(request.getDisciplineId());

        updateFileFields(file, request, discipline);

        File saved = fileRepository.save(file);
        log.info("Arquivo atualizado: {}", saved.getId());

        return toResponse(saved);
    }

    /**
     * Converte File para FileResponse.
     * Calcula contadores de favoritos e comentários.
     */
    private FileResponse toResponse(File file) {
        long favoritesCount = favoriteRepository.countByFile(file);
        long commentsCount = commentRepository.countActiveCommentsByFile(file);

        return FileResponse.builder()
                .id(file.getId())
                .title(file.getTitle())
                .fileName(file.getFileName())
                .fileType(file.getFileType())
                .fileSize(file.getFileSize())
                .filePath(file.getFilePath())
                .description(file.getDescription())
                .disciplineId(file.getDiscipline().getId())
                .disciplineName(file.getDiscipline().getName())
                .institutionId(file.getInstitution().getId())
                .institutionName(file.getInstitution().getName())
                .uploadedById(file.getUploadedBy().getId())
                .uploadedByName(file.getUploadedBy().getName())
                .status(file.getStatus())
                .downloadCount(file.getDownloadCount())
                .approvedAt(file.getApprovedAt())
                .version(file.getVersion())
                .createdAt(file.getCreatedAt())
                .updatedAt(file.getUpdatedAt())
                .favoritesCount(favoritesCount)
                .commentsCount(commentsCount)
                .build();
    }

    /**
     * Atualiza os campos do arquivo com os valores do request.
     */
    private void updateFileFields(File file, FileRequest request, Discipline discipline) {
        file.setTitle(request.getTitle());
        file.setFileName(request.getFileName());
        file.setFileType(request.getFileType());
        file.setFileSize(request.getFileSize());
        file.setFilePath(request.getFilePath());
        file.setDescription(request.getDescription());
        file.setDiscipline(discipline);
        file.setInstitution(discipline.getInstitution());
        file.setVersion(request.getVersion());
    }
}

