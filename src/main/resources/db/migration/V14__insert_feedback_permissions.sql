-- Insertar permisos para el recurso FEEDBACK
INSERT INTO permissions (name, description, resource, action, created_date, created_by) VALUES
('FEEDBACK_CREATE', 'Crear feedback', 'FEEDBACK', 'CREATE', NOW(), 'SYSTEM'),
('FEEDBACK_READ', 'Leer feedback', 'FEEDBACK', 'READ', NOW(), 'SYSTEM'),
('FEEDBACK_UPDATE', 'Actualizar feedback', 'FEEDBACK', 'UPDATE', NOW(), 'SYSTEM'),
('FEEDBACK_DELETE', 'Eliminar feedback', 'FEEDBACK', 'DELETE', NOW(), 'SYSTEM');

-- Asignar permisos de feedback a los roles
-- Los usuarios pueden crear, leer, actualizar y eliminar sus propios feedbacks
INSERT INTO rol_permissions (rol_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.nombre = 'USUARIO' 
AND p.resource = 'FEEDBACK' 
AND p.action IN ('CREATE', 'READ', 'UPDATE', 'DELETE');

-- Los administradores tienen todos los permisos sobre feedback
INSERT INTO rol_permissions (rol_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.nombre = 'ADMIN' 
AND p.resource = 'FEEDBACK';

-- Los coordinadores tienen permisos de lectura sobre feedback
INSERT INTO rol_permissions (rol_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.nombre = 'COORDINADOR' 
AND p.resource = 'FEEDBACK' 
AND p.action = 'READ';
