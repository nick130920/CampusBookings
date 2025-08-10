-- Migración para crear la tabla de alertas de reservas
-- Autor: Sistema CampusBookings
-- Fecha: 2025-01-09

CREATE TABLE alertas_reservas (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    
    -- Relación con la reserva
    reserva_id BIGINT NOT NULL,
    
    -- Información de la alerta
    tipo VARCHAR(50) NOT NULL,
    fecha_envio DATETIME NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PROGRAMADO',
    mensaje TEXT,
    canal_envio VARCHAR(100) DEFAULT 'EMAIL,WEBSOCKET',
    
    -- Información de envío
    fecha_enviado DATETIME NULL,
    detalles_envio TEXT NULL,
    intentos_envio INT DEFAULT 0,
    motivo_fallo TEXT NULL,
    
    -- Auditoría
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    
    -- Constraints
    CONSTRAINT fk_alerta_reserva_reserva FOREIGN KEY (reserva_id) REFERENCES reservas(id) ON DELETE CASCADE,
    
    -- Indices para mejor rendimiento
    INDEX idx_alertas_reserva_id (reserva_id),
    INDEX idx_alertas_estado (estado),
    INDEX idx_alertas_tipo (tipo),
    INDEX idx_alertas_fecha_envio (fecha_envio),
    INDEX idx_alertas_estado_fecha (estado, fecha_envio)
);

-- Comentarios para documentación
ALTER TABLE alertas_reservas COMMENT = 'Tabla para gestionar alertas y recordatorios de reservas';

-- Valores válidos para los enums (solo para documentación)
-- TipoAlerta: RECORDATORIO_24H, RECORDATORIO_2H, RECORDATORIO_30MIN, CONFIRMACION_LLEGADA, EXPIRACION_RESERVA, CAMBIO_ESTADO, CANCELACION_AUTOMATICA
-- EstadoAlerta: PENDIENTE, ENVIADO, FALLIDO, CANCELADO, PROGRAMADO
