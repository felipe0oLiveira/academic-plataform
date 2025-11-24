package com.academicplatform.service;

import com.academicplatform.dto.response.FileResponse;
import com.academicplatform.entity.Favorite;
import com.academicplatform.entity.File;
import com.academicplatform.entity.User;
import com.academicplatform.repository.FavoriteRepository;
import com.academicplatform.util.ServiceHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para operações de negócio relacionadas a Favorite.
 * 
 * @author Felipe Oliveira
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final FileService fileService;
    private final ServiceHelper serviceHelper;

    /**
     * Adiciona um arquivo aos favoritos do usuário.
     * Se já for favorito, não faz nada.
     */
    public void addFavorite(Long fileId, Long userId) {
        File file = serviceHelper.findFileOrThrow(fileId);
        User user = serviceHelper.findUserOrThrow(userId);

        if (favoriteRepository.existsByUserAndFile(user, file)) {
            log.debug("Arquivo {} já está nos favoritos do usuário {}", fileId, userId);
            return;
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .file(file)
                .build();

        favoriteRepository.save(favorite);
        log.info("Favorito adicionado: arquivo {} para usuário {}", fileId, userId);
    }

    /**
     * Remove um arquivo dos favoritos do usuário.
     */
    public void removeFavorite(Long fileId, Long userId) {
        File file = serviceHelper.findFileOrThrow(fileId);
        User user = serviceHelper.findUserOrThrow(userId);

        Favorite favorite = favoriteRepository.findByUserAndFile(user, file)
                .orElseThrow(() -> new RuntimeException("Favorito não encontrado"));

        favoriteRepository.delete(favorite);
        log.info("Favorito removido: arquivo {} do usuário {}", fileId, userId);
    }

    /**
     * Lista arquivos favoritos de um usuário.
     */
    @Transactional(readOnly = true)
    public List<FileResponse> findByUser(Long userId) {
        User user = serviceHelper.findUserOrThrow(userId);

        return favoriteRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(Favorite::getFile)
                .map(file -> fileService.findById(file.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Verifica se um arquivo está nos favoritos do usuário.
     */
    @Transactional(readOnly = true)
    public boolean isFavorite(Long fileId, Long userId) {
        File file = serviceHelper.findFileOrThrow(fileId);
        User user = serviceHelper.findUserOrThrow(userId);

        return favoriteRepository.existsByUserAndFile(user, file);
    }
}

