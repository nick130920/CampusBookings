-- Migración V9: Crear tabla de relación roles-permisos
-- Fecha: 2025-08-07
-- Descripción: Creación de la tabla de relación many-to-many entre roles y permisos

CREATE TABLE rol_permissions (
    rol_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    
    -- Clave primaria compuesta
    PRIMARY KEY (rol_id, permission_id),
    
    -- Claves foráneas
    CONSTRAINT fk_rol_permissions_rol 
        FOREIGN KEY (rol_id) REFERENCES roles(id) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    
    CONSTRAINT fk_rol_permissions_permission 
        FOREIGN KEY (permission_id) REFERENCES permissions(id) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    
    -- Índices para mejorar el rendimiento
    INDEX idx_rol_permissions_rol (rol_id),
    INDEX idx_rol_permissions_permission (permission_id)
);

-- Comentarios en la tabla
ALTER TABLE rol_permissions COMMENT = 'Tabla de relación many-to-many entre roles y permisos';

-- Comentarios en las columnas
ALTER TABLE rol_permissions 
MODIFY COLUMN rol_id BIGINT NOT NULL COMMENT 'ID del rol',
MODIFY COLUMN permission_id BIGINT NOT NULL COMMENT 'ID del permiso';
