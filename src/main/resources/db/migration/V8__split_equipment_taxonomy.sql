-- V8__split_equipment_taxonomy.sql
-- El enum `equipment` mezclaba piezas de apoyo acumulables (BELT, SLEEVES, WRAPS, STRAPS, SUIT)
-- con una clasificacion excluyente (RAW, FULL, COMPETITION) en el mismo Set<>, permitiendo
-- combinaciones contradictorias (RAW + FULL a la vez) e impidiendo filtrar por clase.
-- Se separan en dos columnas por fila: gear_class (valor unico) y *_gear (array de piezas),
-- sustituyendo las tablas de union training_set_template_equipment / workout_set_equipment
-- por columnas array nativas de Postgres (catalogo cerrado y pequeno, sin necesidad de join).

------------------------------------------------------------
-- 1. TRAINING_SET_TEMPLATE
------------------------------------------------------------

ALTER TABLE training_set_template ADD COLUMN prescribed_gear_class varchar(20)
    CHECK (prescribed_gear_class IN ('RAW','FULL','COMPETITION'));

ALTER TABLE training_set_template ADD COLUMN prescribed_gear varchar(20)[]
    CHECK (prescribed_gear <@ ARRAY['BELT','SLEEVES','WRAPS','STRAPS','SUIT']::varchar[]);

-- Backfill defensivo (las tablas de union estan vacias en todos los entornos conocidos, pero
-- se escribe para no perder datos si alguno ya tuviera filas).
UPDATE training_set_template t
   SET prescribed_gear = sub.pieces
  FROM (SELECT training_set_template_id, array_agg(equipment) AS pieces
          FROM training_set_template_equipment
         WHERE equipment IN ('BELT','SLEEVES','WRAPS','STRAPS','SUIT')
         GROUP BY training_set_template_id) sub
 WHERE t.id = sub.training_set_template_id;

-- Si una fila vieja tuviera mas de un valor de clase a la vez (el propio bug que se corrige),
-- se resuelve con prioridad COMPETITION > FULL > RAW.
UPDATE training_set_template t
   SET prescribed_gear_class = sub.gear_class
  FROM (SELECT DISTINCT ON (training_set_template_id) training_set_template_id, equipment AS gear_class
          FROM training_set_template_equipment
         WHERE equipment IN ('RAW','FULL','COMPETITION')
         ORDER BY training_set_template_id,
                  CASE equipment WHEN 'COMPETITION' THEN 1 WHEN 'FULL' THEN 2 ELSE 3 END) sub
 WHERE t.id = sub.training_set_template_id;

DROP TABLE training_set_template_equipment;

------------------------------------------------------------
-- 2. WORKOUT_SET
------------------------------------------------------------

ALTER TABLE workout_set ADD COLUMN gear_class varchar(20)
    CHECK (gear_class IN ('RAW','FULL','COMPETITION'));

ALTER TABLE workout_set ADD COLUMN used_gear varchar(20)[]
    CHECK (used_gear <@ ARRAY['BELT','SLEEVES','WRAPS','STRAPS','SUIT']::varchar[]);

UPDATE workout_set w
   SET used_gear = sub.pieces
  FROM (SELECT workout_set_id, array_agg(equipment) AS pieces
          FROM workout_set_equipment
         WHERE equipment IN ('BELT','SLEEVES','WRAPS','STRAPS','SUIT')
         GROUP BY workout_set_id) sub
 WHERE w.id = sub.workout_set_id;

UPDATE workout_set w
   SET gear_class = sub.gear_class
  FROM (SELECT DISTINCT ON (workout_set_id) workout_set_id, equipment AS gear_class
          FROM workout_set_equipment
         WHERE equipment IN ('RAW','FULL','COMPETITION')
         ORDER BY workout_set_id,
                  CASE equipment WHEN 'COMPETITION' THEN 1 WHEN 'FULL' THEN 2 ELSE 3 END) sub
 WHERE w.id = sub.workout_set_id;

DROP TABLE workout_set_equipment;
