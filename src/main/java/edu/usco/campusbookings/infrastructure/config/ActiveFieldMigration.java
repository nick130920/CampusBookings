package edu.usco.campusbookings.infrastructure.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Migración manual para agregar el campo 'active' a scenario_type_permissions
 * con valores por defecto antes de aplicar restricción NOT NULL
 */
@Component
@RequiredArgsConstructor
@Slf4j
// Ejecutar siempre, pero de forma segura
//@ConditionalOnProperty(name = "spring.jpa.hibernate.ddl-auto", havingValue = "update")
public class ActiveFieldMigration {

    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    @Transactional
    public void migrate() {
        try {
            log.info("Verificando si necesita migración de campo 'active' en scenario_type_permissions...");
            
            // Verificar si la tabla existe
            boolean tableExists = checkTableExists();
            if (!tableExists) {
                log.info("Tabla scenario_type_permissions no existe aún. Saltando migración.");
                return;
            }
            
            // Verificar si el campo ya existe
            boolean columnExists = checkColumnExists();
            if (columnExists) {
                log.info("Campo 'active' ya existe en scenario_type_permissions. Verificando datos...");
                updateNullValues();
                return;
            }
            
            log.info("Agregando campo 'active' con valores por defecto...");
            
            // 1. Agregar columna como nullable inicialmente
            jdbcTemplate.execute(
                "ALTER TABLE scenario_type_permissions ADD COLUMN IF NOT EXISTS active BOOLEAN DEFAULT TRUE"
            );
            
            // 2. Actualizar todos los registros existentes
            int updated = jdbcTemplate.update(
                "UPDATE scenario_type_permissions SET active = TRUE WHERE active IS NULL"
            );
            log.info("Actualizados {} registros con active = TRUE", updated);
            
            // 3. NO aplicar restricción NOT NULL por ahora para evitar errores
            // jdbcTemplate.execute(
            //     "ALTER TABLE scenario_type_permissions ALTER COLUMN active SET NOT NULL"
            // );
            log.info("Campo 'active' agregado como nullable para compatibilidad");
            
            log.info("Migración de campo 'active' completada exitosamente");
            
        } catch (Exception e) {
            log.error("Error durante migración de campo 'active': {}", e.getMessage(), e);
            // No relanzar la excepción para evitar que falle el inicio de la aplicación
        }
    }
    
    private boolean checkTableExists() {
        try {
            String sql = """
                SELECT COUNT(*) FROM information_schema.tables 
                WHERE table_name = 'scenario_type_permissions' 
                AND table_schema = CURRENT_SCHEMA()
                """;
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
            return count != null && count > 0;
        } catch (Exception e) {
            log.debug("Error verificando existencia de tabla: {}", e.getMessage());
            return false;
        }
    }
    
    private boolean checkColumnExists() {
        try {
            String sql = """
                SELECT COUNT(*) FROM information_schema.columns 
                WHERE table_name = 'scenario_type_permissions' 
                AND column_name = 'active'
                AND table_schema = CURRENT_SCHEMA()
                """;
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
            return count != null && count > 0;
        } catch (Exception e) {
            log.debug("Error verificando existencia de columna: {}", e.getMessage());
            return false;
        }
    }
    
    private void updateNullValues() {
        try {
            int updated = jdbcTemplate.update(
                "UPDATE scenario_type_permissions SET active = TRUE WHERE active IS NULL"
            );
            if (updated > 0) {
                log.info("Actualizados {} registros con valores NULL en campo 'active'", updated);
            }
        } catch (Exception e) {
            log.warn("Error actualizando valores NULL: {}", e.getMessage());
        }
    }
}
