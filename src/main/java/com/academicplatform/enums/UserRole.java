package com.academicplatform.enums;

/**
 * Roles/perfis de usuários no sistema.
 * Hierarquia de permissões: SUPER_ADMIN > ADMIN > TEACHER > STUDENT.
 * Determina funcionalidades e acesso disponíveis.
 * 
 * @author Felipe Oliveira
 */
public enum UserRole {
    SUPER_ADMIN, ADMIN, TEACHER, STUDENT
}
