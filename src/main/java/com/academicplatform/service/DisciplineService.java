package com.academicplatform.service;

import com.academicplatform.dto.request.DisciplineRequest;
import com.academicplatform.dto.response.DisciplineResponse;
import com.academicplatform.entity.Discipline;
import com.academicplatform.entity.Institution;
import com.academicplatform.exception.DuplicateEntityException;
import com.academicplatform.repository.DisciplineRepository;
import com.academicplatform.util.ServiceHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para operações de negócio relacionadas a Discipline.
 * 
 * @author Felipe Oliveira
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DisciplineService {

    private final DisciplineRepository disciplineRepository;
    private final ServiceHelper serviceHelper;

    /**
     * Cria uma nova disciplina.
     * Valida se o código já existe na instituição antes de criar.
     */
    public DisciplineResponse create(DisciplineRequest request) {
        Institution institution = serviceHelper.findInstitutionOrThrow(request.getInstitutionId());
        validateCodeUniqueness(request.getCode(), institution);

        Discipline discipline = buildDiscipline(request, institution);
        Discipline saved = disciplineRepository.save(discipline);
        log.info("Disciplina criada: {}", saved.getId());

        return toResponse(saved);
    }

    /**
     * Busca disciplina por ID.
     */
    @Transactional(readOnly = true)
    public DisciplineResponse findById(Long id) {
        Discipline discipline = serviceHelper.findDisciplineOrThrow(id);
        return toResponse(discipline);
    }

    /**
     * Lista disciplinas ativas de uma instituição.
     */
    @Transactional(readOnly = true)
    public List<DisciplineResponse> findByInstitution(Long institutionId) {
        Institution institution = serviceHelper.findInstitutionOrThrow(institutionId);
        return disciplineRepository.findByInstitutionAndActiveTrue(institution).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Atualiza uma disciplina existente.
     */
    public DisciplineResponse update(Long id, DisciplineRequest request) {
        Discipline discipline = serviceHelper.findDisciplineOrThrow(id);
        Institution institution = serviceHelper.findInstitutionOrThrow(request.getInstitutionId());

        validateCodeUniquenessForUpdate(discipline, request.getCode(), institution);
        updateDisciplineFields(discipline, request, institution);

        Discipline saved = disciplineRepository.save(discipline);
        log.info("Disciplina atualizada: {}", saved.getId());

        return toResponse(saved);
    }

    /**
     * Converte Discipline para DisciplineResponse.
     */
    private DisciplineResponse toResponse(Discipline discipline) {
        return DisciplineResponse.builder()
                .id(discipline.getId())
                .name(discipline.getName())
                .code(discipline.getCode())
                .description(discipline.getDescription())
                .institutionId(discipline.getInstitution().getId())
                .institutionName(discipline.getInstitution().getName())
                .active(discipline.getActive())
                .createdAt(discipline.getCreatedAt())
                .updatedAt(discipline.getUpdatedAt())
                .totalFiles(discipline.getTotalFilesCount())
                .totalStorageUsedBytes(discipline.getTotalStorageUsed())
                .build();
    }

    /**
     * Constrói uma nova Discipline a partir do request.
     */
    private Discipline buildDiscipline(DisciplineRequest request, Institution institution) {
        return Discipline.builder()
                .name(request.getName())
                .code(request.getCode())
                .description(request.getDescription())
                .institution(institution)
                .active(request.getActive() != null ? request.getActive() : true)
                .build();
    }

    /**
     * Valida se o código é único na instituição.
     */
    private void validateCodeUniqueness(String code, Institution institution) {
        if (code != null && !code.isEmpty() && 
            disciplineRepository.existsByCodeAndInstitution(code, institution)) {
            throw new DuplicateEntityException("Disciplina com código '" + code + "' já existe nesta instituição");
        }
    }

    /**
     * Valida se o código é único para atualização, considerando que pode ser o mesmo código da disciplina.
     */
    private void validateCodeUniquenessForUpdate(Discipline discipline, String newCode, Institution institution) {
        if (newCode != null && !newCode.isEmpty() && 
            !newCode.equals(discipline.getCode()) &&
            disciplineRepository.existsByCodeAndInstitution(newCode, institution)) {
            throw new DuplicateEntityException("Disciplina com código '" + newCode + "' já existe nesta instituição");
        }
    }

    /**
     * Atualiza os campos da disciplina com os valores do request.
     */
    private void updateDisciplineFields(Discipline discipline, DisciplineRequest request, Institution institution) {
        discipline.setName(request.getName());
        discipline.setCode(request.getCode());
        discipline.setDescription(request.getDescription());
        discipline.setInstitution(institution);
        
        if (request.getActive() != null) {
            discipline.setActive(request.getActive());
        }
    }
}

