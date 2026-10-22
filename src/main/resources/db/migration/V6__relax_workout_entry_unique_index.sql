-- V6__relax_workout_entry_unique_index.sql
-- Permite varias entries del mismo ejercicio en una sesión si provienen de plantillas distintas.
-- Caso de uso: el coach prescribe el mismo ejercicio en tres slots con cargas distintas
-- (1×1 @9 de apertura, 1×4 @147,5 y 2×4 @141,5 — patrón habitual en el método conjugado).
--
-- Nota: en Postgres NULL no colisiona consigo mismo en índices únicos, por lo que entries
-- sin plantilla (entreno libre) quedan sin protección de duplicados entre sí.
-- Es el comportamiento que ya tenían antes de V4 y es aceptable para ese caso de uso.

DROP INDEX uq_workout_entry_session_exercise;

CREATE UNIQUE INDEX uq_workout_entry_session_exercise_template
    ON workout_entry (workout_session_id, exercise_id, source_template_id)
 WHERE is_warmup = false;
