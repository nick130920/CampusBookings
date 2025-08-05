-- Crear tabla para tokens de recuperación de contraseña
CREATE TABLE password_reset_tokens (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    code VARCHAR(6) NOT NULL,
    token VARCHAR(255) UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    attempts INTEGER NOT NULL DEFAULT 0
);

-- Índices para optimizar consultas
CREATE INDEX idx_password_reset_email ON password_reset_tokens(email);
CREATE INDEX idx_password_reset_email_code ON password_reset_tokens(email, code);
CREATE INDEX idx_password_reset_token ON password_reset_tokens(token);
CREATE INDEX idx_password_reset_expires_at ON password_reset_tokens(expires_at);
CREATE INDEX idx_password_reset_email_used_expires ON password_reset_tokens(email, used, expires_at);

-- Comentarios para documentar la tabla
COMMENT ON TABLE password_reset_tokens IS 'Almacena tokens temporales para recuperación de contraseñas';
COMMENT ON COLUMN password_reset_tokens.email IS 'Email del usuario que solicita el reset';
COMMENT ON COLUMN password_reset_tokens.code IS 'Código de 6 dígitos enviado por email';
COMMENT ON COLUMN password_reset_tokens.token IS 'Token UUID generado después de verificar el código';
COMMENT ON COLUMN password_reset_tokens.created_at IS 'Fecha y hora de creación del token';
COMMENT ON COLUMN password_reset_tokens.expires_at IS 'Fecha y hora de expiración del token';
COMMENT ON COLUMN password_reset_tokens.used IS 'Indica si el token ya fue utilizado';
COMMENT ON COLUMN password_reset_tokens.attempts IS 'Número de intentos de verificación del código';