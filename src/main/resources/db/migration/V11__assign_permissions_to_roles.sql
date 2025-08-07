-- Migración V11: Asignar permisos a roles existentes
-- Fecha: 2025-08-07
-- Descripción: Asignación de permisos por defecto a los roles existentes

-- Asignar TODOS los permisos al rol ADMIN
INSERT INTO rol_permissions (rol_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.nombre = 'ADMIN';

-- Asignar permisos básicos al rol USER/USUARIO
INSERT INTO rol_permissions (rol_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.nombre IN ('USER', 'USUARIO')
AND p.name IN (
    'RESERVAS_CREATE',
    'RESERVAS_READ',
    'RESERVAS_UPDATE',
    'ESCENARIOS_READ',
    'CONFIGURACION_READ'
);

-- Asignar permisos intermedios al rol COORDINATOR (si existe)
INSERT INTO rol_permissions (rol_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.nombre = 'COORDINATOR'
AND p.name IN (
    'RESERVAS_CREATE',
    'RESERVAS_READ',
    'RESERVAS_UPDATE',
    'RESERVAS_DELETE',
    'RESERVAS_MANAGE',
    'ESCENARIOS_READ',
    'ESCENARIOS_UPDATE',
    'USUARIOS_READ',
    'CONFIGURACION_READ',
    'REPORTES_READ',
    'REPORTES_CREATE'
);

-- Verificar las asignaciones
SELECT 
    r.nombre as rol_nombre,
    COUNT(rp.permission_id) as permisos_asignados
FROM roles r
LEFT JOIN rol_permissions rp ON r.id = rp.rol_id
GROUP BY r.id, r.nombre
ORDER BY r.nombre;
