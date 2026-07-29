-- V4__update_entities_schema.sql
-- Alinea el esquema con el rediseño del modelo de dominio (rama FLYWAY).
--
-- Resumen:
--   exercise                  → accommodating_resistance (antes loading_method), height_value (antes
--                               height_length, ahora double), unilateral, measurement_type, load_mode;
--                               se eliminan columnas muertas. La magnitud de banda/cadena NO vive aquí:
--                               se prescribe por set en training_set_template (target_resistance_top/bottom_pct,
--                               % del RM del reference_exercise arriba/abajo del ROM; negativos = reverse bands).
--   training_session_template → day_index (antes day_in_week).
--   training_set_template     → slot_code, tempo, amrap, target_reps_min/max (antes target_reps),
--                               target_intensity_min/max (antes target_intensity; min 0 para RIR 0),
--                               target_resistance_top_pct/target_resistance_bottom_pct,
--                               reference_exercise_id, tabla de equipamiento prescrito.
--   workout_session           → status; dedupe + unique(session_date) (el dominio trabaja con "el día").
--   workout_entry             → display_order, source_template_id (FK ON DELETE SET NULL, backfill
--                               determinista), is_warmup NOT NULL, dedupe + unique parcial
--                               (workout_session_id, exercise_id) para entries de trabajo;
--                               se eliminan los actual_* (redundantes con workout_set).
--   workout_set               → rpe_value (antes intensity_value/intensity_type; guarda que aborta si
--                               existen datos PERCENTAGE inconvertibles), tabla de equipamiento usado.
--   Se migran los datos seed de V2/V3 a la nueva taxonomía y se indexan las FKs.

------------------------------------------------------------
-- 1. EXERCISE
------------------------------------------------------------

-- 1.1 Renombrados y cambio de tipo
ALTER TABLE exercise RENAME COLUMN loading_method TO accommodating_resistance;
ALTER TABLE exercise RENAME COLUMN height_length TO height_value;
ALTER TABLE exercise ALTER COLUMN height_value TYPE double precision;

-- 1.2 Columnas nuevas
ALTER TABLE exercise ADD COLUMN unilateral boolean NOT NULL DEFAULT false;
ALTER TABLE exercise ADD COLUMN measurement_type varchar(30);
ALTER TABLE exercise ADD COLUMN load_mode varchar(30);

-- 1.3 Checks antiguos que bloquean la migración de datos
ALTER TABLE exercise DROP CONSTRAINT IF EXISTS exercise_setup_check;
ALTER TABLE exercise DROP CONSTRAINT IF EXISTS exercise_stance_check;
ALTER TABLE exercise DROP CONSTRAINT IF EXISTS exercise_implement_check;
ALTER TABLE exercise DROP CONSTRAINT IF EXISTS exercise_loading_method_check;

-- 1.4 Migración de datos seed a la nueva taxonomía
UPDATE exercise SET implement = 'BODY_WEIGHT' WHERE implement = 'BODYWEIGHT';

-- Los setups que describían implemento/agarre/postura se reparten en sus campos propios
UPDATE exercise SET stance = 'SPLIT' WHERE setup = 'SPLIT';
UPDATE exercise SET unilateral = true WHERE setup IN ('ONE_ARM', 'SPLIT');
UPDATE exercise SET setup = 'STANDARD'
 WHERE setup IN ('SSB', 'CAMBERED', 'SNATCH_GRIP', 'CLOSE_GRIP', 'SPLIT', 'ONE_ARM');
UPDATE exercise SET unilateral = true
 WHERE base_name IN ('Zancadas Caminando', 'Step Up');

UPDATE exercise
   SET measurement_type = CASE WHEN base_name = 'Plancha Frontal' THEN 'TIME' ELSE 'REPS' END
 WHERE measurement_type IS NULL;

UPDATE exercise
   SET load_mode = CASE
        WHEN implement IN ('BODY_WEIGHT', 'PULLUP_BAR', 'RINGS') THEN 'BODYWEIGHT_PLUS'
        ELSE 'EXTERNAL'
   END
 WHERE load_mode IS NULL;

-- 1.5 Columnas muertas (tempo/pausa pasan a prescribirse por set en training_set_template.tempo)
ALTER TABLE exercise DROP COLUMN IF EXISTS loading_unit;
ALTER TABLE exercise DROP COLUMN IF EXISTS pause_seconds;
ALTER TABLE exercise DROP COLUMN IF EXISTS tempo;
ALTER TABLE exercise DROP COLUMN IF EXISTS bar;
ALTER TABLE exercise DROP COLUMN IF EXISTS resistance;
ALTER TABLE exercise DROP COLUMN IF EXISTS style;
ALTER TABLE exercise DROP COLUMN IF EXISTS equipment;

-- 1.6 Checks nuevos
ALTER TABLE exercise ADD CONSTRAINT exercise_setup_check
    CHECK (setup IN ('STANDARD','LOWBAR','HIGHBAR','FRONT','ZERCHER','BOX'));
ALTER TABLE exercise ADD CONSTRAINT exercise_stance_check
    CHECK (stance IN ('NARROW','MEDIUM','WIDE','SUMO','CONVENTIONAL','NONE','LARSEN','PARALYMPIC','SPLIT','REVERSE_LUNGE'));
ALTER TABLE exercise ADD CONSTRAINT exercise_implement_check
    CHECK (implement IN ('BARBELL','DUMBBELL','KETTLEBELL','TRAP_BAR','SAFETY_BAR','CAMBERED_BAR','SWISS_BAR','PLATES',
                         'CABLE','MACHINE','SMITH','RINGS','BODY_WEIGHT','LANDMINE','PULLUP_BAR','BAND',
                         'LEVERAGE_MACHINE','WEIGHTED','EX_BARBELL','OTHER'));
ALTER TABLE exercise ADD CONSTRAINT exercise_accommodating_resistance_check
    CHECK (accommodating_resistance IN ('NONE','BANDS','CHAINS','REVERSE_BANDS','BOTH'));
ALTER TABLE exercise ADD CONSTRAINT exercise_measurement_type_check
    CHECK (measurement_type IN ('REPS','TIME','DISTANCE','STEPS'));
ALTER TABLE exercise ADD CONSTRAINT exercise_load_mode_check
    CHECK (load_mode IN ('EXTERNAL','BODYWEIGHT','BODYWEIGHT_PLUS','BODYWEIGHT_MINUS','NONE'));

------------------------------------------------------------
-- 2. TRAINING_SESSION_TEMPLATE
------------------------------------------------------------

ALTER TABLE training_session_template RENAME COLUMN day_in_week TO day_index;

------------------------------------------------------------
-- 3. TRAINING_SET_TEMPLATE
------------------------------------------------------------

-- 3.1 Columnas nuevas
ALTER TABLE training_set_template ADD COLUMN slot_code varchar(10);
ALTER TABLE training_set_template ADD COLUMN tempo varchar(20);
ALTER TABLE training_set_template ADD COLUMN amrap boolean NOT NULL DEFAULT false;
ALTER TABLE training_set_template ADD COLUMN target_reps_min integer;
ALTER TABLE training_set_template ADD COLUMN target_reps_max integer;
ALTER TABLE training_set_template ADD COLUMN target_intensity_min double precision;
ALTER TABLE training_set_template ADD COLUMN target_intensity_max double precision;
ALTER TABLE training_set_template ADD COLUMN target_resistance_top_pct integer;
ALTER TABLE training_set_template ADD COLUMN target_resistance_bottom_pct integer;
ALTER TABLE training_set_template ADD COLUMN reference_exercise_id bigint;

ALTER TABLE training_set_template ADD CONSTRAINT fk_set_template_reference_exercise
    FOREIGN KEY (reference_exercise_id) REFERENCES exercise;

-- 3.2 Migración: reps e intensidad puntuales pasan a rango [min, max] con min = max
UPDATE training_set_template
   SET target_reps_min = target_reps,
       target_reps_max = target_reps
 WHERE target_reps IS NOT NULL;

UPDATE training_set_template
   SET target_intensity_min = target_intensity,
       target_intensity_max = target_intensity
 WHERE target_intensity IS NOT NULL;

ALTER TABLE training_set_template DROP COLUMN target_reps;
ALTER TABLE training_set_template DROP COLUMN target_intensity;

-- 3.3 Checks: límites nuevos y tipo de intensidad ampliado
ALTER TABLE training_set_template DROP CONSTRAINT IF EXISTS training_set_template_target_sets_check;
ALTER TABLE training_set_template DROP CONSTRAINT IF EXISTS training_set_template_display_order_check;
ALTER TABLE training_set_template DROP CONSTRAINT IF EXISTS training_set_template_target_intensity_type_check;

ALTER TABLE training_set_template ADD CONSTRAINT training_set_template_target_sets_check
    CHECK (target_sets >= 1 AND target_sets <= 20);
ALTER TABLE training_set_template ADD CONSTRAINT training_set_template_display_order_check
    CHECK (display_order >= 1 AND display_order <= 20);
ALTER TABLE training_set_template ADD CONSTRAINT training_set_template_target_intensity_type_check
    CHECK (target_intensity_type IN ('RIR','RPE','PERCENTAGE'));
ALTER TABLE training_set_template ADD CONSTRAINT training_set_template_slot_code_check
    CHECK (slot_code IN ('SQ1','SQ2','SQ3','BP1','BP2','BP3','DL1','DL2','DL3'));
ALTER TABLE training_set_template ADD CONSTRAINT training_set_template_target_reps_check
    CHECK (target_reps_min >= 1 AND target_reps_max >= 1);
-- Min 0: RIR 0 (al fallo) es prescripción válida y habitual
ALTER TABLE training_set_template ADD CONSTRAINT training_set_template_target_intensity_check
    CHECK (target_intensity_min >= 0 AND target_intensity_min <= 100
       AND target_intensity_max >= 0 AND target_intensity_max <= 100);
-- Perfil de tensión de banda/cadena arriba/abajo del ROM, en % del RM del reference_exercise.
-- Negativos = descarga (reverse bands).
ALTER TABLE training_set_template ADD CONSTRAINT training_set_template_target_resistance_top_pct_check
    CHECK (target_resistance_top_pct >= -100 AND target_resistance_top_pct <= 100);
ALTER TABLE training_set_template ADD CONSTRAINT training_set_template_target_resistance_bottom_pct_check
    CHECK (target_resistance_bottom_pct >= -100 AND target_resistance_bottom_pct <= 100);

-- 3.4 Equipamiento prescrito por set (ElementCollection)
CREATE TABLE training_set_template_equipment (
    training_set_template_id bigint      NOT NULL REFERENCES training_set_template,
    equipment                varchar(20) NOT NULL
        CHECK (equipment IN ('BELT','RAW','SLEEVES','WRAPS','SUIT','STRAPS','FULL','COMPETITION')),
    PRIMARY KEY (training_set_template_id, equipment)
);

------------------------------------------------------------
-- 4. WORKOUT_SESSION
------------------------------------------------------------

ALTER TABLE workout_session ADD COLUMN status varchar(20)
    CHECK (status IN ('COMPLETED','SKIPPED'));

-- Sesiones pasadas con trabajo registrado quedan como completadas; las futuras quedan NULL (planificadas)
UPDATE workout_session ws
   SET status = 'COMPLETED'
 WHERE ws.session_date < CURRENT_DATE
   AND EXISTS (SELECT 1 FROM workout_entry we WHERE we.workout_session_id = ws.id);

-- El dominio permite un único workout por fecha. Antes de imponer el unique, se fusionan
-- deterministamente las fechas duplicadas que pudiera haber creado la API (se conserva la sesión
-- de menor id y hereda los entries del resto; las duplicadas quedan vacías y se borran).
UPDATE workout_entry we
   SET workout_session_id = k.keep_id
  FROM (SELECT id, MIN(id) OVER (PARTITION BY session_date) AS keep_id
          FROM workout_session) k
 WHERE we.workout_session_id = k.id
   AND k.id <> k.keep_id;

DELETE FROM workout_session ws
 WHERE EXISTS (SELECT 1
                 FROM workout_session other
                WHERE other.session_date = ws.session_date
                  AND other.id < ws.id);

ALTER TABLE workout_session ADD CONSTRAINT uq_workout_session_date UNIQUE (session_date);

------------------------------------------------------------
-- 5. WORKOUT_ENTRY
------------------------------------------------------------

ALTER TABLE workout_entry ADD COLUMN display_order integer;
ALTER TABLE workout_entry ADD COLUMN source_template_id bigint;

-- ON DELETE SET NULL: editar/borrar un set de plantilla no puede romper el historial de workouts
-- (el entry conserva sus datos y solo pierde el vínculo con la pauta).
ALTER TABLE workout_entry ADD CONSTRAINT fk_entry_source_template
    FOREIGN KEY (source_template_id) REFERENCES training_set_template ON DELETE SET NULL;

-- is_warmup era nullable: el dominio solo tiene dos estados y las métricas filtran por él,
-- así que NULL provocaba que esos entries desaparecieran silenciosamente de los cálculos.
UPDATE workout_entry SET is_warmup = false WHERE is_warmup IS NULL;
ALTER TABLE workout_entry ALTER COLUMN is_warmup SET NOT NULL;
ALTER TABLE workout_entry ALTER COLUMN is_warmup SET DEFAULT false;

-- Backfill: orden por id dentro de cada sesión
UPDATE workout_entry we
   SET display_order = sub.rn
  FROM (SELECT id, ROW_NUMBER() OVER (PARTITION BY workout_session_id ORDER BY id) AS rn
          FROM workout_entry) sub
 WHERE we.id = sub.id;

-- Backfill: correlación entry → set pautado por (plantilla de la sesión, ejercicio).
-- DISTINCT ON garantiza elección determinista si la plantilla repite ejercicio
-- (se elige el set pautado de menor display_order).
UPDATE workout_entry we
   SET source_template_id = pick.template_id
  FROM (SELECT DISTINCT ON (we2.id) we2.id AS entry_id, tst.id AS template_id
          FROM workout_entry we2
          JOIN workout_session ws ON ws.id = we2.workout_session_id
          JOIN training_set_template tst
            ON tst.session_template_id = ws.training_session_template_id
           AND tst.exercise_id = we2.exercise_id
         ORDER BY we2.id, tst.display_order NULLS LAST, tst.id) pick
 WHERE we.id = pick.entry_id
   AND we.source_template_id IS NULL;

-- Los agregados actual_* eran redundantes con workout_set
ALTER TABLE workout_entry DROP COLUMN IF EXISTS actual_sets;
ALTER TABLE workout_entry DROP COLUMN IF EXISTS actual_reps;
ALTER TABLE workout_entry DROP COLUMN IF EXISTS actual_plate_weight;
ALTER TABLE workout_entry DROP COLUMN IF EXISTS actual_rir_or_rpe;

-- Dedupe de entries de trabajo duplicados (mismo ejercicio en la misma sesión), posibles por la
-- carrera del find-or-create de la API o por la fusión de sesiones anterior: se conserva el de
-- menor id, hereda los sets del resto y los duplicados se borran.
UPDATE workout_set s
   SET workout_entry_id = k.keep_id
  FROM (SELECT id, MIN(id) OVER (PARTITION BY workout_session_id, exercise_id) AS keep_id
          FROM workout_entry
         WHERE is_warmup = false) k
 WHERE s.workout_entry_id = k.id
   AND k.id <> k.keep_id;

DELETE FROM workout_entry we
 WHERE we.is_warmup = false
   AND EXISTS (SELECT 1
                 FROM workout_entry other
                WHERE other.workout_session_id = we.workout_session_id
                  AND other.exercise_id = we.exercise_id
                  AND other.is_warmup = false
                  AND other.id < we.id);

-- Cierra a nivel de BD la carrera del find-or-create (WorkoutServiceImpl.addSetByExercise).
-- Parcial: un mismo ejercicio puede repetirse como entry de calentamiento + entry de trabajo.
CREATE UNIQUE INDEX uq_workout_entry_session_exercise
    ON workout_entry (workout_session_id, exercise_id)
 WHERE is_warmup = false;

------------------------------------------------------------
-- 6. WORKOUT_SET
------------------------------------------------------------

ALTER TABLE workout_set ADD COLUMN rpe_value double precision
    CHECK (rpe_value >= 0 AND rpe_value <= 11);

-- Guarda: PERCENTAGE no es convertible a RPE sin un RM de referencia. Si existen sets así,
-- la migración aborta explícitamente (rollback completo) en lugar de perder el dato en silencio.
DO $$
BEGIN
    IF EXISTS (SELECT 1
                 FROM workout_set
                WHERE intensity_type = 'PERCENTAGE'
                  AND intensity_value IS NOT NULL) THEN
        RAISE EXCEPTION
            'V4: existen workout_set con intensity_type=PERCENTAGE; decidir su conversion antes de migrar';
    END IF;
END $$;

-- Migración de intensidad: RPE se copia tal cual; RIR se convierte (RPE = 10 - RIR)
UPDATE workout_set
   SET rpe_value = CASE intensity_type
        WHEN 'RPE' THEN intensity_value
        WHEN 'RIR' THEN GREATEST(0, 10 - intensity_value)
   END
 WHERE intensity_value IS NOT NULL;

ALTER TABLE workout_set DROP COLUMN intensity_value;
ALTER TABLE workout_set DROP COLUMN intensity_type;

-- Equipamiento usado por serie (ElementCollection)
CREATE TABLE workout_set_equipment (
    workout_set_id bigint      NOT NULL REFERENCES workout_set,
    equipment      varchar(20) NOT NULL
        CHECK (equipment IN ('BELT','RAW','SLEEVES','WRAPS','SUIT','STRAPS','FULL','COMPETITION')),
    PRIMARY KEY (workout_set_id, equipment)
);

------------------------------------------------------------
-- 7. ÍNDICES EN FKs (Postgres no los crea automáticamente)
------------------------------------------------------------

CREATE INDEX IF NOT EXISTS idx_workout_entry_session          ON workout_entry (workout_session_id);
CREATE INDEX IF NOT EXISTS idx_workout_entry_exercise         ON workout_entry (exercise_id);
CREATE INDEX IF NOT EXISTS idx_workout_entry_source_template  ON workout_entry (source_template_id);
CREATE INDEX IF NOT EXISTS idx_workout_set_entry              ON workout_set (workout_entry_id);
CREATE INDEX IF NOT EXISTS idx_set_template_session           ON training_set_template (session_template_id);
CREATE INDEX IF NOT EXISTS idx_set_template_exercise          ON training_set_template (exercise_id);
CREATE INDEX IF NOT EXISTS idx_set_template_reference         ON training_set_template (reference_exercise_id);
CREATE INDEX IF NOT EXISTS idx_workout_session_template       ON workout_session (training_session_template_id);
