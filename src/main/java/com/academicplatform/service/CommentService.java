package com.academicplatform.service;

import com.academicplatform.dto.request.CommentRequest;
import com.academicplatform.dto.response.CommentResponse;
import com.academicplatform.entity.Comment;
import com.academicplatform.entity.File;
import com.academicplatform.entity.User;
import com.academicplatform.repository.CommentRepository;
import com.academicplatform.util.ServiceHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para operações de negócio relacionadas a Comment.
 * 
 * @author Felipe Oliveira
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final ServiceHelper serviceHelper;

    /**
     * Cria um novo comentário em um arquivo.
     */
    public CommentResponse create(CommentRequest request, Long userId) {
        File file = serviceHelper.findFileOrThrow(request.getFileId());
        User user = serviceHelper.findUserOrThrow(userId);

        Comment comment = Comment.builder()
                .content(request.getContent())
                .file(file)
                .user(user)
                .active(true)
                .build();

        Comment saved = commentRepository.save(comment);
        log.info("Comentário criado: {}", saved.getId());

        return toResponse(saved);
    }

    /**
     * Busca comentário por ID.
     */
    @Transactional(readOnly = true)
    public CommentResponse findById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentário não encontrado com ID: " + id));
        return toResponse(comment);
    }

    /**
     * Lista comentários de um arquivo, ordenados por data de criação (mais antigos primeiro).
     */
    @Transactional(readOnly = true)
    public List<CommentResponse> findByFile(Long fileId) {
        File file = serviceHelper.findFileOrThrow(fileId);
        return commentRepository.findActiveCommentsByFileOrderByCreatedAtAsc(file).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Desativa um comentário (soft delete).
     */
    public void delete(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentário não encontrado"));

        comment.setActive(false);
        commentRepository.save(comment);
        log.info("Comentário desativado: {}", id);
    }

    /**
     * Atualiza conteúdo de um comentário.
     */
    public CommentResponse update(Long id, String content) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentário não encontrado"));

        comment.setContent(content);
        Comment saved = commentRepository.save(comment);
        log.info("Comentário atualizado: {}", saved.getId());

        return toResponse(saved);
    }

    /**
     * Converte Comment para CommentResponse.
     */
    private CommentResponse toResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .userId(comment.getUser().getId())
                .userName(comment.getUser().getName())
                .fileId(comment.getFile().getId())
                .fileName(comment.getFile().getTitle())
                .active(comment.getActive())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}

