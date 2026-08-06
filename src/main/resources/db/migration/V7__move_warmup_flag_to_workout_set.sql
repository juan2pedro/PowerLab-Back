-- V7__move_warmup_flag_to_workout_set.sql
--
-- Mueve la condicion de calentamiento del entry a la serie: workout_entry.is_warmup ->
-- workout_set.set_type.
--
-- Motivo: la rampa de aproximacion ocurre DENTRO del mismo ejercicio (60x5, 100x3, 140x1,
-- 165x4, 165x4). Con el flag en el entry hacia falta partir el ejercicio en dos entries
-- —una de warmup y otra de trabajo— y por eso el indice unico de V6 tuvo que hacerse parcial
-- (WHERE is_warmup = false). Ese predicado dejaba ademas sin proteccion de duplicados a las
-- entries de entreno libre, porque en Postgres NULL no colisiona consigo mismo.
--
-- set_type es una etiqueta de intencion declarada por el atleta. El numero de levantamientos
-- (NL) y las zonas de intensidad NO se derivan de aqui: se calculan a partir de la carga
-- contra el 1RM de referencia, sin tocar el esquema.

------------------------------------------------------------
-- 1. WORKOUT_SET.SET_TYPE
------------------------------------------------------------
ALTER TABLE workout_set ADD COLUMN set_type varchar(20);

UPDATE workout_set s
   SET set_type = CASE WHEN we.is_warmup THEN 'WARMUP' ELSE 'WORKING' END
  FROM workout_entry we
 WHERE s.workout_entry_id = we.id;

-- Defensivo: sets huerfanos de la correlacion anterior (no deberia haberlos, la FK es NOT NULL)
UPDATE workout_set SET set_type = 'WORKING' WHERE set_type IS NULL;

ALTER TABLE workout_set ALTER COLUMN set_type SET NOT NULL;
ALTER TABLE workout_set ALTER COLUMN set_type SET DEFAULT 'WORKING';

-- Convencion del proyecto: enums como varchar + CHECK (no tipos ENUM nativos), igual que V5.
ALTER TABLE workout_set ADD CONSTRAINT workout_set_set_type_check
    CHECK (set_type IN ('WARMUP','RAMP','WORKING','BACKOFF'));

------------------------------------------------------------
-- 2. FUSION DE ENTRIES DE CALENTAMIENTO
------------------------------------------------------------
-- Cada entry de warmup se funde con la entry de trabajo del mismo ejercicio en la misma sesion.
-- Las que no tengan gemela de trabajo sobreviven como entry normal: sus series ya quedaron
-- marcadas como WARMUP en el paso 1, asi que no se pierde informacion.
CREATE TEMP TABLE v7_entry_merge AS
SELECT w.id AS warmup_id, t.id AS target_id
  FROM workout_entry w
  JOIN LATERAL (SELECT we.id
                  FROM workout_entry we
                 WHERE we.workout_session_id = w.workout_session_id
                   AND we.exercise_id        = w.exercise_id
                   AND we.is_warmup = false
                 ORDER BY we.id
                 LIMIT 1) t ON true
 WHERE w.is_warmup = true;

UPDATE workout_set s
   SET workout_entry_id = m.target_id
  FROM v7_entry_merge m
 WHERE s.workout_entry_id = m.warmup_id;

DELETE FROM workout_entry we
 USING v7_entry_merge m
 WHERE we.id = m.warmup_id;

DROP TABLE v7_entry_merge;

------------------------------------------------------------
-- 3. DEDUPE PREVIO AL INDICE UNICO TOTAL
------------------------------------------------------------
-- V4 ya deduplico entries de trabajo, pero solo entre ellas. Al quitar el predicado parcial
-- el indice pasa a cubrir tambien las entries sin plantilla, que hasta ahora quedaban fuera.
-- COALESCE(-1) trata los NULL como iguales, que es la semantica que impondra NULLS NOT DISTINCT.
UPDATE workout_set s
   SET workout_entry_id = k.keep_id
  FROM (SELECT id,
               MIN(id) OVER (PARTITION BY workout_session_id, exercise_id,
                                          COALESCE(source_template_id, -1)) AS keep_id
          FROM workout_entry) k
 WHERE s.workout_entry_id = k.id
   AND k.id <> k.keep_id;

DELETE FROM workout_entry we
 WHERE EXISTS (SELECT 1
                 FROM workout_entry other
                WHERE other.workout_session_id = we.workout_session_id
                  AND other.exercise_id        = we.exercise_id
                  AND COALESCE(other.source_template_id, -1) = COALESCE(we.source_template_id, -1)
                  AND other.id < we.id);

------------------------------------------------------------
-- 4. RENUMERADO DE SET_NUMBER
------------------------------------------------------------
-- La fusion mezcla dos numeraciones que empezaban en 1, y hasta ahora nada impedia repetir
-- set_number dentro de un entry. Se renumera todo de forma determinista antes de imponer la
-- restriccion: primero WARMUP, luego RAMP, despues el resto, conservando el orden relativo.
WITH renumbered AS (
    SELECT id,
           ROW_NUMBER() OVER (PARTITION BY workout_entry_id
                              ORDER BY CASE set_type
                                           WHEN 'WARMUP' THEN 0
                                           WHEN 'RAMP'   THEN 1
                                           ELSE 2
                                       END,
                                       set_number,
                                       id) AS rn
      FROM workout_set
)
UPDATE workout_set s
   SET set_number = r.rn
  FROM renumbered r
 WHERE s.id = r.id
   AND s.set_number IS DISTINCT FROM r.rn;

-- DEFERRABLE porque reordenar series dentro de un entry pasa por estados intermedios con
-- numeros repetidos; sin diferir, el UPDATE fallaria a mitad.
ALTER TABLE workout_set ADD CONSTRAINT uq_workout_set_entry_number
    UNIQUE (workout_entry_id, set_number) DEFERRABLE INITIALLY DEFERRED;

------------------------------------------------------------
-- 5. WORKOUT_ENTRY.IS_WARMUP
------------------------------------------------------------
DROP INDEX IF EXISTS uq_workout_entry_session_exercise_template;

ALTER TABLE workout_entry DROP COLUMN is_warmup;

-- Ya no hace falta el predicado parcial: un ejercicio aparece una sola vez por sesion y
-- plantilla. NULLS NOT DISTINCT (PG15+, aqui PG16) cierra el hueco de las entries sin
-- plantilla que documentaba V6.
CREATE UNIQUE INDEX uq_workout_entry_session_exercise_template
    ON workout_entry (workout_session_id, exercise_id, source_template_id)
    NULLS NOT DISTINCT;

------------------------------------------------------------
-- 6. INDICE DE APOYO PARA METRICAS
------------------------------------------------------------
-- Las queries de metricas filtran ahora por set_type directamente sobre workout_set.
CREATE INDEX IF NOT EXISTS idx_workout_set_type ON workout_set (set_type);
