package com.academicplatform.util;

import com.academicplatform.entity.*;
import com.academicplatform.exception.EntityNotFoundException;
import com.academicplatform.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Classe helper com métodos comuns para evitar repetição de código nos services.
 * 
 * @author Felipe Oliveira
 */
@Component
@RequiredArgsConstructor
public class ServiceHelper {

    private final InstitutionRepository institutionRepository;
    private final UserRepository userRepository;
    private final DisciplineRepository disciplineRepository;
    private final FileRepository fileRepository;

    /**
     * Busca instituição por ID ou lança exceção se não encontrar.
     */
    public Institution findInstitutionOrThrow(Long id) {
        return institutionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Instituição", id));
    }

    /**
     * Busca usuário por ID ou lança exceção se não encontrar.
     */
    public User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário", id));
    }

    /**
     * Busca disciplina por ID ou lança exceção se não encontrar.
     */
    public Discipline findDisciplineOrThrow(Long id) {
        return disciplineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Disciplina", id));
    }

    /**
     * Busca arquivo por ID ou lança exceção se não encontrar.
     */
    public File findFileOrThrow(Long id) {
        return fileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Arquivo", id));
    }
}

