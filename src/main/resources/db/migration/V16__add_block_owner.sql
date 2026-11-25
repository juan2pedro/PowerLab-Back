-- ============================================================
-- V16: Dueno en block (aislamiento multi-usuario, mismo patron que V15).
--
--   Hasta ahora los mesociclos (block) no tenian dueno: cualquier
--   usuario autenticado podia leer/editar el bloque de otro por su id.
--   El dueno de un training_session_template se deriva via block.owner.
--
--   El usuario dueno ya existe desde V15 (juanpemt), aqui no se inserta;
--   solo se backfillea por email. Produccion se reseedea limpio aparte.
--
--   ORDEN: columna -> backfill -> NOT NULL + FK.
-- ============================================================

-- 1. Columna owner_id (nullable de momento para poder backfillear)
ALTER TABLE block ADD COLUMN owner_id BIGINT;

-- 2. Backfill: todos los bloques existentes pasan al usuario (por email)
UPDATE block
   SET owner_id = (SELECT id FROM users WHERE email = 'juanpemt@gmail.com')
 WHERE owner_id IS NULL;

-- 3. Ya con datos: NOT NULL + FK
ALTER TABLE block ALTER COLUMN owner_id SET NOT NULL;
ALTER TABLE block
    ADD CONSTRAINT fk_block_owner FOREIGN KEY (owner_id) REFERENCES users;
