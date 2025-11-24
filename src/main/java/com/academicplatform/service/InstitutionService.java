package com.academicplatform.service;

import com.academicplatform.dto.request.InstitutionRequest;
import com.academicplatform.dto.response.InstitutionResponse;
import com.academicplatform.entity.Institution;
import com.academicplatform.exception.DuplicateEntityException;
import com.academicplatform.repository.InstitutionRepository;
import com.academicplatform.repository.UserRepository;
import com.academicplatform.util.ServiceHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para operações de negócio relacionadas a Institution.
 * 
 * @author Felipe Oliveira
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InstitutionService {

    private final InstitutionRepository institutionRepository;
    private final UserRepository userRepository;
    private final ServiceHelper serviceHelper;

    /**
     * Cria uma nova instituição.
     * Valida se o nome já existe antes de criar.
     */
    public InstitutionResponse create(InstitutionRequest request) {
        if (institutionRepository.existsByName(request.getName())) {
            throw new DuplicateEntityException("Instituição com nome '" + request.getName() + "' já existe");
        }

        Institution institution = buildInstitution(request);
        Institution saved = institutionRepository.save(institution);
        log.info("Instituição criada: {}", saved.getId());

        return toResponse(saved);
    }

    /**
     * Busca instituição por ID.
     * Lança exceção se não encontrar.
     */
    @Transactional(readOnly = true)
    public InstitutionResponse findById(Long id) {
        Institution institution = serviceHelper.findInstitutionOrThrow(id);
        return toResponse(institution);
    }

    /**
     * Lista todas as instituições ativas.
     */
    @Transactional(readOnly = true)
    public List<InstitutionResponse> findAllActive() {
        return institutionRepository.findByActiveTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Atualiza uma instituição existente.
     * Valida se o nome já existe em outra instituição.
     */
    public InstitutionResponse update(Long id, InstitutionRequest request) {
        Institution institution = serviceHelper.findInstitutionOrThrow(id);

        validateNameUniqueness(institution, request.getName());

        updateInstitutionFields(institution, request);
        Institution saved = institutionRepository.save(institution);
        log.info("Instituição atualizada: {}", saved.getId());

        return toResponse(saved);
    }

    /**
     * Converte Institution para InstitutionResponse.
     * Calcula totais de usuários e armazenamento usado.
     */
    private InstitutionResponse toResponse(Institution institution) {
        long totalUsers = userRepository.countActiveUsersByInstitution(institution);
        long totalStorageUsedGB = institution.getTotalStorageUsed();

        return InstitutionResponse.builder()
                .id(institution.getId())
                .name(institution.getName())
                .code(institution.getCode())
                .description(institution.getDescription())
                .plan(institution.getPlan())
                .maxUsers(institution.getMaxUsers())
                .maxStorageGB(institution.getMaxStorageGB())
                .expiresAt(institution.getExpiresAt())
                .active(institution.getActive())
                .createdAt(institution.getCreatedAt())
                .updatedAt(institution.getUpdatedAt())
                .totalUsers(totalUsers)
                .totalStorageUsedGB(totalStorageUsedGB)
                .build();
    }

    /**
     * Constrói uma nova Institution a partir do request.
     */
    private Institution buildInstitution(InstitutionRequest request) {
        return Institution.builder()
                .name(request.getName())
                .code(request.getCode())
                .description(request.getDescription())
                .plan(request.getPlan())
                .maxUsers(request.getMaxUsers())
                .maxStorageGB(request.getMaxStorageGB())
                .expiresAt(request.getExpiresAt())
                .active(request.getActive())
                .build();
    }

    /**
     * Valida se o nome é único, considerando que pode ser o mesmo nome da própria instituição.
     */
    private void validateNameUniqueness(Institution institution, String newName) {
        if (!institution.getName().equals(newName) && institutionRepository.existsByName(newName)) {
            throw new DuplicateEntityException("Instituição com nome '" + newName + "' já existe");
        }
    }

    /**
     * Atualiza os campos da instituição com os valores do request.
     */
    private void updateInstitutionFields(Institution institution, InstitutionRequest request) {
        institution.setName(request.getName());
        institution.setCode(request.getCode());
        institution.setDescription(request.getDescription());
        institution.setPlan(request.getPlan());
        institution.setMaxUsers(request.getMaxUsers());
        institution.setMaxStorageGB(request.getMaxStorageGB());
        institution.setExpiresAt(request.getExpiresAt());
        institution.setActive(request.getActive());
    }
}

