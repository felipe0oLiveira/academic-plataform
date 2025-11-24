package com.academicplatform.exception;

/**
 * Exception lançada quando uma entidade não é encontrada no banco de dados.
 * 
 * @author Felipe Oliveira
 */
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String entityName, Long id) {
        super(String.format("%s não encontrado(a) com ID: %d", entityName, id));
    }
}

