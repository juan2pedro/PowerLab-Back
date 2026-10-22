-- V5__fix_enum_checks.sql
-- Corrige el drift entre los enums Java y los CHECK constraints de Postgres, detectado al
-- construir el modulo de metricas. Ningun enum cambia de nombre; solo se realinean los CHECK
-- (algunos arrastrados sin tocar desde V1) con los valores que el dominio ya admite en Java.

------------------------------------------------------------
-- 1. EXERCISE.MAIN_PATTERN
------------------------------------------------------------
-- V1 solo contemplaba 'PRESS'; el dominio lo desglosa en VERTICAL_PRESS / HORIZONTAL_PRESS
-- y anade HORIZONTAL_PULL / VERTICAL_PULL. Las 6 filas seed de press vertical (Overhead, Push,
-- Z, Seated DB Shoulder, Landmine, Arnold Press) se reclasifican antes de recrear el check.
ALTER TABLE exercise DROP CONSTRAINT IF EXISTS exercise_main_pattern_check;

UPDATE exercise SET main_pattern = 'VERTICAL_PRESS' WHERE main_pattern = 'PRESS';

ALTER TABLE exercise ADD CONSTRAINT exercise_main_pattern_check
    CHECK (main_pattern IN ('NONE','SQUAT','BENCH','DEADLIFT','VERTICAL_PRESS',
                            'HORIZONTAL_PRESS','HORIZONTAL_PULL','VERTICAL_PULL','OLY','ACCESSORY'));

------------------------------------------------------------
-- 2. EXERCISE.GRIP
------------------------------------------------------------
-- Falta HOOK (agarre en gancho, habitual en peso muerto).
ALTER TABLE exercise DROP CONSTRAINT IF EXISTS exercise_grip_check;

ALTER TABLE exercise ADD CONSTRAINT exercise_grip_check
    CHECK (grip IN ('CLOSE','MEDIUM','WIDE','SNATCH','CLEAN','NEUTRAL',
                    'PRONATED','SUPINATED','MIXED','NONE','HOOK'));

------------------------------------------------------------
-- 3. EXERCISE.ACCOMMODATING_RESISTANCE
------------------------------------------------------------
-- V4 renombro loading_method -> accommodating_resistance pero conservo el valor 'BOTH' del
-- check original; el enum Java usa BANDS_AND_CHAINS. Hoy no hay filas afectadas (0 registros
-- seed usan 'BOTH'), pero se corrige el dato por si una BD ya migrada lo tuviera.
ALTER TABLE exercise DROP CONSTRAINT IF EXISTS exercise_accommodating_resistance_check;

UPDATE exercise SET accommodating_resistance = 'BANDS_AND_CHAINS'
 WHERE accommodating_resistance = 'BOTH';

ALTER TABLE exercise ADD CONSTRAINT exercise_accommodating_resistance_check
    CHECK (accommodating_resistance IN ('NONE','BANDS','CHAINS','REVERSE_BANDS','BANDS_AND_CHAINS'));

------------------------------------------------------------
-- 4. TRAINING_SET_TEMPLATE.SLOT_CODE
------------------------------------------------------------
-- Faltaban los slots de accesorio (A1-A3); solo estaban los de SQ/BP/DL.
ALTER TABLE training_set_template DROP CONSTRAINT IF EXISTS training_set_template_slot_code_check;

ALTER TABLE training_set_template ADD CONSTRAINT training_set_template_slot_code_check
    CHECK (slot_code IN ('SQ1','SQ2','SQ3','BP1','BP2','BP3','DL1','DL2','DL3','A1','A2','A3'));

------------------------------------------------------------
-- 5. WORKOUT_SESSION.STATUS
------------------------------------------------------------
-- El enum Java ya tenia IN_PROGRESS (el check de V4 solo admitia COMPLETED/SKIPPED) y se anade
-- PARTIAL: sesion donde se completo el trabajo principal pero se salto parte de los accesorios.
ALTER TABLE workout_session DROP CONSTRAINT IF EXISTS workout_session_status_check;

ALTER TABLE workout_session ADD CONSTRAINT workout_session_status_check
    CHECK (status IN ('COMPLETED','SKIPPED','IN_PROGRESS','PARTIAL'));

------------------------------------------------------------
-- 6. TRAINING_SESSION_TEMPLATE.CONJUGATE_DAY_TYPE
------------------------------------------------------------
-- Falta NONE (dias sin metodo conjugado asignado), presente en el enum Java desde V1.
ALTER TABLE training_session_template DROP CONSTRAINT IF EXISTS training_session_template_conjugate_day_type_check;

ALTER TABLE training_session_template ADD CONSTRAINT training_session_template_conjugate_day_type_check
    CHECK (conjugate_day_type IN ('ME_UPPER','ME_LOWER','DE_UPPER','DE_LOWER','RE_UPPER','RE_LOWER','NONE'));
