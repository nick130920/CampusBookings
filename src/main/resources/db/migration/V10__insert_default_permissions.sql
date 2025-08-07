-- Migración V10: Insertar permisos por defecto
-- Fecha: 2025-08-07
-- Descripción: Inserción de permisos básicos del sistema

-- Permisos para USUARIOS
INSERT INTO permissions (name, description, resource, action) VALUES
('USUARIOS_CREATE', 'Crear nuevos usuarios en el sistema', 'USUARIOS', 'CREATE'),
('USUARIOS_READ', 'Ver información de usuarios', 'USUARIOS', 'READ'),
('USUARIOS_UPDATE', 'Actualizar información de usuarios', 'USUARIOS', 'UPDATE'),
('USUARIOS_DELETE', 'Eliminar usuarios del sistema', 'USUARIOS', 'DELETE'),
('USUARIOS_MANAGE', 'Gestión completa de usuarios', 'USUARIOS', 'MANAGE');

-- Permisos para ROLES
INSERT INTO permissions (name, description, resource, action) VALUES
('ROLES_CREATE', 'Crear nuevos roles en el sistema', 'ROLES', 'CREATE'),
('ROLES_READ', 'Ver información de roles', 'ROLES', 'READ'),
('ROLES_UPDATE', 'Actualizar información de roles', 'ROLES', 'UPDATE'),
('ROLES_DELETE', 'Eliminar roles del sistema', 'ROLES', 'DELETE'),
('ROLES_MANAGE', 'Gestión completa de roles', 'ROLES', 'MANAGE');

-- Permisos para PERMISOS
INSERT INTO permissions (name, description, resource, action) VALUES
('PERMISOS_CREATE', 'Crear nuevos permisos en el sistema', 'PERMISOS', 'CREATE'),
('PERMISOS_READ', 'Ver información de permisos', 'PERMISOS', 'READ'),
('PERMISOS_UPDATE', 'Actualizar información de permisos', 'PERMISOS', 'UPDATE'),
('PERMISOS_DELETE', 'Eliminar permisos del sistema', 'PERMISOS', 'DELETE'),
('PERMISOS_MANAGE', 'Gestión completa de permisos', 'PERMISOS', 'MANAGE');

-- Permisos para RESERVAS
INSERT INTO permissions (name, description, resource, action) VALUES
('RESERVAS_CREATE', 'Crear nuevas reservas', 'RESERVAS', 'CREATE'),
('RESERVAS_READ', 'Ver información de reservas', 'RESERVAS', 'READ'),
('RESERVAS_UPDATE', 'Actualizar reservas existentes', 'RESERVAS', 'UPDATE'),
('RESERVAS_DELETE', 'Eliminar reservas', 'RESERVAS', 'DELETE'),
('RESERVAS_MANAGE', 'Gestión completa de reservas', 'RESERVAS', 'MANAGE');

-- Permisos para ESCENARIOS
INSERT INTO permissions (name, description, resource, action) VALUES
('ESCENARIOS_CREATE', 'Crear nuevos escenarios', 'ESCENARIOS', 'CREATE'),
('ESCENARIOS_READ', 'Ver información de escenarios', 'ESCENARIOS', 'READ'),
('ESCENARIOS_UPDATE', 'Actualizar escenarios existentes', 'ESCENARIOS', 'UPDATE'),
('ESCENARIOS_DELETE', 'Eliminar escenarios', 'ESCENARIOS', 'DELETE'),
('ESCENARIOS_MANAGE', 'Gestión completa de escenarios', 'ESCENARIOS', 'MANAGE');

-- Permisos para CONFIGURACION
INSERT INTO permissions (name, description, resource, action) VALUES
('CONFIGURACION_READ', 'Ver configuración del sistema', 'CONFIGURACION', 'READ'),
('CONFIGURACION_UPDATE', 'Actualizar configuración del sistema', 'CONFIGURACION', 'UPDATE'),
('CONFIGURACION_MANAGE', 'Gestión completa de configuración', 'CONFIGURACION', 'MANAGE');

-- Permisos para REPORTES
INSERT INTO permissions (name, description, resource, action) VALUES
('REPORTES_READ', 'Ver reportes del sistema', 'REPORTES', 'READ'),
('REPORTES_CREATE', 'Generar nuevos reportes', 'REPORTES', 'CREATE'),
('REPORTES_MANAGE', 'Gestión completa de reportes', 'REPORTES', 'MANAGE');

-- Permisos para AUDITORIA
INSERT INTO permissions (name, description, resource, action) VALUES
('AUDITORIA_READ', 'Ver logs de auditoría', 'AUDITORIA', 'READ'),
('AUDITORIA_MANAGE', 'Gestión completa de auditoría', 'AUDITORIA', 'MANAGE');
