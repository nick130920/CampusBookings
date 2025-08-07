-- Migración V7: Crear tabla de permisos
-- Fecha: 2025-08-07
-- Descripción: Creación de la tabla permissions para el sistema de gestión de permisos

CREATE TABLE permissions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL,
    resource VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),
    
    -- Índices para mejorar el rendimiento
    INDEX idx_permissions_resource (resource),
    INDEX idx_permissions_action (action),
    INDEX idx_permissions_resource_action (resource, action),
    INDEX idx_permissions_name (name)
);

-- Comentarios en la tabla
ALTER TABLE permissions COMMENT = 'Tabla que almacena los permisos del sistema';

-- Comentarios en las columnas
ALTER TABLE permissions 
MODIFY COLUMN name VARCHAR(100) NOT NULL UNIQUE COMMENT 'Nombre único del permiso',
MODIFY COLUMN description VARCHAR(255) NOT NULL COMMENT 'Descripción detallada del permiso',
MODIFY COLUMN resource VARCHAR(50) NOT NULL COMMENT 'Recurso sobre el que aplica el permiso (ej: USUARIOS, RESERVAS)',
MODIFY COLUMN action VARCHAR(50) NOT NULL COMMENT 'Acción que permite el permiso (ej: CREATE, READ, UPDATE, DELETE)';
