package com.academicplatform.service;

import com.academicplatform.dto.request.UserRequest;
import com.academicplatform.dto.response.UserResponse;
import com.academicplatform.entity.Institution;
import com.academicplatform.entity.User;
import com.academicplatform.exception.BusinessException;
import com.academicplatform.exception.DuplicateEntityException;
import com.academicplatform.repository.UserRepository;
import com.academicplatform.util.ServiceHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para operações de negócio relacionadas a User.
 * 
 * @author Felipe Oliveira
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ServiceHelper serviceHelper;

    /**
     * Cria um novo usuário.
     * Criptografa a senha antes de persistir.
     * Valida se o email já existe e se a instituição não atingiu o limite de usuários.
     */
    public UserResponse create(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEntityException("Usuário com email '" + request.getEmail() + "' já existe");
        }

        Institution institution = serviceHelper.findInstitutionOrThrow(request.getInstitutionId());
        validateUserLimit(institution);

        User user = buildUser(request, institution);
        User saved = userRepository.save(user);
        log.info("Usuário criado: {}", saved.getId());

        return toResponse(saved);
    }

    /**
     * Busca usuário por ID.
     */
    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        User user = serviceHelper.findUserOrThrow(id);
        return toResponse(user);
    }

    /**
     * Busca usuário por email.
     */
    @Transactional(readOnly = true)
    public UserResponse findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com email: " + email));
        return toResponse(user);
    }

    /**
     * Lista usuários de uma instituição.
     */
    @Transactional(readOnly = true)
    public List<UserResponse> findByInstitution(Long institutionId) {
        Institution institution = serviceHelper.findInstitutionOrThrow(institutionId);
        return userRepository.findByInstitution(institution).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Atualiza um usuário existente.
     * Se a senha for informada, criptografa antes de atualizar.
     */
    public UserResponse update(Long id, UserRequest request) {
        User user = serviceHelper.findUserOrThrow(id);
        validateEmailUniqueness(user, request.getEmail());

        Institution institution = serviceHelper.findInstitutionOrThrow(request.getInstitutionId());
        updateUserFields(user, request, institution);

        User saved = userRepository.save(user);
        log.info("Usuário atualizado: {}", saved.getId());

        return toResponse(saved);
    }

    /**
     * Converte User para UserResponse.
     */
    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .institutionId(user.getInstitution().getId())
                .institutionName(user.getInstitution().getName())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLogin(user.getLastLogin())
                .build();
    }

    /**
     * Constrói um novo User a partir do request.
     */
    private User buildUser(UserRequest request, Institution institution) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .institution(institution)
                .active(request.getActive() != null ? request.getActive() : true)
                .build();
    }

    /**
     * Valida se a instituição ainda pode aceitar novos usuários.
     */
    private void validateUserLimit(Institution institution) {
        if (userRepository.countActiveUsersByInstitution(institution) >= institution.getMaxUsers()) {
            throw new BusinessException("Instituição atingiu o limite máximo de usuários");
        }
    }

    /**
     * Valida se o email é único, considerando que pode ser o mesmo email do próprio usuário.
     */
    private void validateEmailUniqueness(User user, String newEmail) {
        if (!user.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
            throw new DuplicateEntityException("Usuário com email '" + newEmail + "' já existe");
        }
    }

    /**
     * Atualiza os campos do usuário com os valores do request.
     */
    private void updateUserFields(User user, UserRequest request, Institution institution) {
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setInstitution(institution);
        
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }
    }
}

