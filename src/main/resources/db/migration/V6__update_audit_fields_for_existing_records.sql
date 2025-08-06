-- Migración para actualizar campos de auditoría existentes
-- y manejar valores null en created_by y modified_by

-- Actualizar valores 'SYSTEM' a NULL para que Spring Data los maneje automáticamente
-- Solo para registros donde el auditor no se pudo determinar originalmente

-- Para la tabla de usuarios
UPDATE usuarios 
SET created_by = NULL, modified_by = NULL 
WHERE created_by = 'SYSTEM' OR modified_by = 'SYSTEM';

-- Para la tabla de escenarios  
UPDATE escenarios 
SET created_by = NULL, modified_by = NULL 
WHERE created_by = 'SYSTEM' OR modified_by = 'SYSTEM';

-- Para la tabla de reservas
UPDATE reservas 
SET created_by = NULL, modified_by = NULL 
WHERE created_by = 'SYSTEM' OR modified_by = 'SYSTEM';

-- Para la tabla de configuración del sistema
UPDATE configuracion_sistema 
SET created_by = NULL, modified_by = NULL 
WHERE created_by = 'SYSTEM' OR modified_by = 'SYSTEM';

-- Para la tabla de tokens de reset de contraseña
UPDATE password_reset_tokens 
SET created_by = NULL, modified_by = NULL 
WHERE created_by = 'SYSTEM' OR modified_by = 'SYSTEM';

-- Los registros con NULL en created_by/modified_by se manejarán automáticamente
-- por el AuditorAware cuando se actualicen en el futuro