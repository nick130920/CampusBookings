-- ==============================================
-- Migración Manual: Agregar campo 'active' a scenario_type_permissions
-- Ejecutar en PostgreSQL de producción
-- ==============================================

-- 1. Verificar si la tabla existe
DO $$
BEGIN
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'scenario_type_permissions') THEN
        RAISE NOTICE 'Tabla scenario_type_permissions existe. Procediendo con migración...';
        
        -- 2. Verificar si el campo ya existe
        IF NOT EXISTS (SELECT FROM information_schema.columns 
                      WHERE table_name = 'scenario_type_permissions' 
                      AND column_name = 'active') THEN
            
            RAISE NOTICE 'Campo active no existe. Agregando...';
            
            -- 3. Agregar campo como nullable con valor por defecto
            ALTER TABLE scenario_type_permissions 
            ADD COLUMN active BOOLEAN DEFAULT TRUE;
            
            -- 4. Actualizar registros existentes
            UPDATE scenario_type_permissions 
            SET active = TRUE 
            WHERE active IS NULL;
            
            -- 5. Obtener estadísticas
            RAISE NOTICE 'Campo active agregado exitosamente.';
            
        ELSE
            RAISE NOTICE 'Campo active ya existe. Verificando valores NULL...';
            
            -- Solo actualizar valores NULL si existen
            UPDATE scenario_type_permissions 
            SET active = TRUE 
            WHERE active IS NULL;
            
        END IF;
        
        -- 6. Mostrar estadísticas finales
        RAISE NOTICE 'Total de registros en scenario_type_permissions: %', 
                     (SELECT COUNT(*) FROM scenario_type_permissions);
        RAISE NOTICE 'Registros con active = TRUE: %', 
                     (SELECT COUNT(*) FROM scenario_type_permissions WHERE active = TRUE);
        RAISE NOTICE 'Registros con active IS NULL: %', 
                     (SELECT COUNT(*) FROM scenario_type_permissions WHERE active IS NULL);
        
    ELSE
        RAISE NOTICE 'Tabla scenario_type_permissions no existe. Migración no necesaria.';
    END IF;
END
$$;

-- ==============================================
-- Verificación final
-- ==============================================
SELECT 
    table_name,
    column_name,
    data_type,
    is_nullable,
    column_default
FROM information_schema.columns 
WHERE table_name = 'scenario_type_permissions' 
AND column_name = 'active';

-- Mostrar datos actuales
SELECT COUNT(*) as total_permissions FROM scenario_type_permissions;
SELECT active, COUNT(*) as count 
FROM scenario_type_permissions 
GROUP BY active 
ORDER BY active;
