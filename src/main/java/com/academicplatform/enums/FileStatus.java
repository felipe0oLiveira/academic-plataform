package com.academicplatform.enums;

/**
 * Status do arquivo no processo de aprovação.
 * Fluxo: PENDING → APPROVED (aprovado) ou REJECTED (rejeitado).
 * Apenas arquivos APPROVED são visíveis para alunos.
 * 
 * @author Felipe Oliveira
 */
public enum FileStatus {
    PENDING, APPROVED, REJECTED
}
