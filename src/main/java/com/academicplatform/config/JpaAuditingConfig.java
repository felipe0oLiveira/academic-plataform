package com.academicplatform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Habilita auditoria JPA para preenchimento automático de campos de auditoria.
 * 
 * @author Felipe Oliveira
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
