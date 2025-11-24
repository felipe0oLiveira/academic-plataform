package com.academicplatform.exception;

/**
 * Exception lançada quando tenta-se criar uma entidade duplicada.
 * 
 * @author Felipe Oliveira
 */
public class DuplicateEntityException extends RuntimeException {

    public DuplicateEntityException(String message) {
        super(message);
    }
}

