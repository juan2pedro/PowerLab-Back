-- V12__widen_tempo_column.sql
-- El tempo pasa de una unica tupla (e-p-c-p) a una SECUENCIA de tuplas por rep, separadas por '|',
-- para modelar complexes de pausas sobre el mismo movimiento (p. ej. "1-5-X-0|1-3-X-0": rep1 pausa 5", rep2 pausa 3").
-- Una sola tupla sigue siendo valida (series homogeneas). Solo ampliamos la columna; no hay CHECK de formato en BD
-- (la validacion del patron vive en Bean Validation sobre la entidad y los DTOs de request).

ALTER TABLE training_set_template
    ALTER COLUMN tempo TYPE varchar(60);
