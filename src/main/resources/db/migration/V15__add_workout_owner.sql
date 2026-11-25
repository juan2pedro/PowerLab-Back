-- ============================================================
-- V15: Dueno en workout_session (cierra IDOR + multi-usuario).
--
--   Hasta ahora workout_session no tenia dueno: cualquier usuario
--   autenticado podia leer/editar sesiones de otro por su id, y el
--   UNIQUE global de session_date impedia que dos atletas entrenaran
--   el mismo dia. Aqui cada sesion pasa a pertenecer a un usuario.
--
--   ORDEN OBLIGATORIO (o falla el NOT NULL / la FK):
--     1. usuario dueno -> 2. columna -> 3. backfill -> 4. NOT NULL + FK -> 5. unique.
--
--   Nota: en local el usuario ya existe (ON CONFLICT DO NOTHING no hace
--   nada) y el backfill por email le asigna todo el historial del seed V3.
--   Produccion se reseedea limpio aparte, con su propia password.
-- ============================================================

-- 1. Usuario dueno del historial (idempotente). Password solo placeholder
--    para local; en prod se reseedea con credenciales reales.
INSERT INTO users (email, password_hash, username, sex, coach, default_pr_visibility, active)
VALUES ('juanpemt@gmail.com',
        '$2a$10$REEMPLAZAR_POR_HASH_BCRYPT_ADMIN1234',
        'juanpe', 'MALE', true, 'PRIVATE', true)
ON CONFLICT (email) DO NOTHING;

-- 2. Columna owner_id (nullable de momento para poder hacer el backfill)
ALTER TABLE workout_session ADD COLUMN owner_id BIGINT;

-- 3. Backfill: todas las sesiones existentes pasan al usuario (por email, no por id)
UPDATE workout_session
   SET owner_id = (SELECT id FROM users WHERE email = 'juanpemt@gmail.com')
 WHERE owner_id IS NULL;

-- 4. Ya con datos: NOT NULL + FK
ALTER TABLE workout_session ALTER COLUMN owner_id SET NOT NULL;
ALTER TABLE workout_session
    ADD CONSTRAINT fk_workout_session_owner FOREIGN KEY (owner_id) REFERENCES users;

-- 5. Unique por dueno+fecha en lugar del global de V4 (uq_workout_session_date)
ALTER TABLE workout_session DROP CONSTRAINT uq_workout_session_date;
ALTER TABLE workout_session
    ADD CONSTRAINT uq_workout_session_owner_date UNIQUE (owner_id, session_date);
