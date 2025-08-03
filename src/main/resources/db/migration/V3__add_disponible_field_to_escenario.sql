-- Añadir campo disponible a la tabla escenario si no existe
ALTER TABLE escenario 
ADD COLUMN IF NOT EXISTS disponible BOOLEAN NOT NULL DEFAULT true;

-- Comentario para documentar el campo
COMMENT ON COLUMN escenario.disponible IS 'Indica si el escenario está disponible para reservas';