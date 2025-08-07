-- Creación de la tabla feedback
CREATE TABLE feedback (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    escenario_id BIGINT NOT NULL,
    calificacion INT NOT NULL,
    comentario TEXT,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    modified_by VARCHAR(255),
    
    -- Constraints
    CONSTRAINT chk_feedback_calificacion CHECK (calificacion BETWEEN 1 AND 5),
    CONSTRAINT uq_feedback_usuario_escenario UNIQUE (usuario_id, escenario_id),
    
    -- Foreign Keys
    CONSTRAINT fk_feedback_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE,
    CONSTRAINT fk_feedback_escenario FOREIGN KEY (escenario_id) REFERENCES escenario (id) ON DELETE CASCADE
);

-- Índices para mejorar el rendimiento
CREATE INDEX idx_feedback_usuario_id ON feedback (usuario_id);
CREATE INDEX idx_feedback_escenario_id ON feedback (escenario_id);
CREATE INDEX idx_feedback_calificacion ON feedback (calificacion);
CREATE INDEX idx_feedback_activo ON feedback (activo);
CREATE INDEX idx_feedback_created_date ON feedback (created_date);

-- Comentarios para documentación
ALTER TABLE feedback COMMENT = 'Tabla para almacenar feedback de usuarios sobre escenarios';
ALTER TABLE feedback MODIFY COLUMN usuario_id BIGINT NOT NULL COMMENT 'ID del usuario que deja el feedback';
ALTER TABLE feedback MODIFY COLUMN escenario_id BIGINT NOT NULL COMMENT 'ID del escenario evaluado';
ALTER TABLE feedback MODIFY COLUMN calificacion INT NOT NULL COMMENT 'Calificación del 1 al 5';
ALTER TABLE feedback MODIFY COLUMN comentario TEXT COMMENT 'Comentario opcional del usuario';
ALTER TABLE feedback MODIFY COLUMN activo BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Indica si el feedback está activo';
