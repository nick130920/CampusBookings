-- Migración V8: Actualizar tabla de roles
-- Fecha: 2025-08-07
-- Descripción: Actualización de la tabla rol para el sistema de gestión de roles y permisos

-- Renombrar la tabla para seguir la convención en inglés
RENAME TABLE rol TO roles;

-- Agregar nuevas columnas a la tabla roles
ALTER TABLE roles 
ADD COLUMN descripcion VARCHAR(255) NOT NULL DEFAULT 'Descripción del rol' AFTER nombre,
ADD COLUMN activo BOOLEAN NOT NULL DEFAULT TRUE AFTER descripcion,
ADD COLUMN created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP AFTER activo,
ADD COLUMN last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_date,
ADD COLUMN created_by VARCHAR(255) AFTER last_modified_date,
ADD COLUMN last_modified_by VARCHAR(255) AFTER created_by;

-- Agregar constraint de unicidad al nombre si no existe
ALTER TABLE roles 
ADD CONSTRAINT uk_roles_nombre UNIQUE (nombre);

-- Actualizar los roles existentes con descripciones apropiadas
UPDATE roles 
SET descripcion = CASE 
    WHEN nombre = 'ADMIN' THEN 'Administrador del sistema con acceso completo'
    WHEN nombre = 'USER' THEN 'Usuario estándar con permisos básicos'
    WHEN nombre = 'USUARIO' THEN 'Usuario estándar con permisos básicos'
    WHEN nombre = 'COORDINATOR' THEN 'Coordinador con permisos de gestión'
    ELSE CONCAT('Rol de ', nombre)
END;

-- Agregar índices para mejorar el rendimiento
CREATE INDEX idx_roles_activo ON roles (activo);
CREATE INDEX idx_roles_nombre ON roles (nombre);

-- Comentarios en la tabla
ALTER TABLE roles COMMENT = 'Tabla que almacena los roles del sistema';

-- Comentarios en las columnas
ALTER TABLE roles 
MODIFY COLUMN nombre VARCHAR(255) NOT NULL COMMENT 'Nombre único del rol',
MODIFY COLUMN descripcion VARCHAR(255) NOT NULL COMMENT 'Descripción detallada del rol',
MODIFY COLUMN activo BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Indica si el rol está activo';
