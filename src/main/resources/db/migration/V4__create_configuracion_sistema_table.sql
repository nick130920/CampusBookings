-- Crear tabla para configuración del sistema
CREATE TABLE configuracion_sistema (
    id BIGSERIAL PRIMARY KEY,
    dias_minimos_anticipacion INTEGER NOT NULL CHECK (dias_minimos_anticipacion >= 0 AND dias_minimos_anticipacion <= 7),
    dias_maximos_anticipacion INTEGER NOT NULL CHECK (dias_maximos_anticipacion >= 7 AND dias_maximos_anticipacion <= 365),
    tipo_configuracion VARCHAR(50) NOT NULL DEFAULT 'RESERVAS',
    descripcion TEXT,
    created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) DEFAULT 'SYSTEM',
    modified_by VARCHAR(255) DEFAULT 'SYSTEM',
    
    -- Validación: días mínimos deben ser menores que días máximos
    CONSTRAINT check_dias_validos CHECK (dias_minimos_anticipacion < dias_maximos_anticipacion),
    
    -- Único por tipo de configuración
    CONSTRAINT uk_configuracion_tipo UNIQUE (tipo_configuracion)
);

-- Crear índices
CREATE INDEX idx_configuracion_tipo ON configuracion_sistema(tipo_configuracion);
CREATE INDEX idx_configuracion_modified ON configuracion_sistema(modified_date);

-- Insertar configuración por defecto
INSERT INTO configuracion_sistema (
    dias_minimos_anticipacion, 
    dias_maximos_anticipacion, 
    tipo_configuracion, 
    descripcion,
    created_by,
    modified_by
) VALUES (
    2, 
    90, 
    'RESERVAS', 
    'Configuración por defecto del sistema de reservas - Los usuarios deben hacer reservas con mínimo 2 días de anticipación y máximo 90 días',
    'SYSTEM',
    'SYSTEM'
);

-- Comentarios para documentación
COMMENT ON TABLE configuracion_sistema IS 'Configuración global del sistema CampusBookings';
COMMENT ON COLUMN configuracion_sistema.dias_minimos_anticipacion IS 'Días mínimos de anticipación requeridos para hacer una reserva (0-7)';
COMMENT ON COLUMN configuracion_sistema.dias_maximos_anticipacion IS 'Días máximos de anticipación permitidos para hacer una reserva (7-365)';
COMMENT ON COLUMN configuracion_sistema.tipo_configuracion IS 'Tipo de configuración: RESERVAS, NOTIFICACIONES, GENERAL';
COMMENT ON COLUMN configuracion_sistema.descripcion IS 'Descripción detallada de la configuración';