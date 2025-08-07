-- Migración V12: Actualizar referencias en tabla usuarios
-- Fecha: 2025-08-07
-- Descripción: Actualizar la tabla usuarios para referenciar correctamente la nueva tabla roles

-- Primero verificar si hay una clave foránea existente y eliminarla si es necesario
SET FOREIGN_KEY_CHECKS = 0;

-- Actualizar la referencia de la tabla usuarios para apuntar a 'roles' en lugar de 'rol'
-- Solo si la columna rol_id ya existe y apunta a la tabla 'rol'
ALTER TABLE usuarios 
DROP FOREIGN KEY IF EXISTS usuarios_ibfk_1,
DROP FOREIGN KEY IF EXISTS fk_usuarios_rol;

-- Agregar la nueva clave foránea que apunta a la tabla 'roles'
ALTER TABLE usuarios 
ADD CONSTRAINT fk_usuarios_rol 
FOREIGN KEY (rol_id) REFERENCES roles(id) 
ON DELETE SET NULL ON UPDATE CASCADE;

SET FOREIGN_KEY_CHECKS = 1;

-- Agregar índice para mejorar el rendimiento de las consultas
CREATE INDEX idx_usuarios_rol_id ON usuarios (rol_id);

-- Comentario en la tabla
ALTER TABLE usuarios 
MODIFY COLUMN rol_id BIGINT COMMENT 'ID del rol asignado al usuario';
