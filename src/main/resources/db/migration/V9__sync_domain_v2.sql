-- V9__sync_domain_v2.sql
-- Cierra el drift restante entre las entidades JPA y el esquema tras la refactorizacion
-- "bbdd v2 + metrics domain". Produccion arranca con ddl-auto=validate, asi que ambas
-- desviaciones impiden el arranque. Los tests no las detectan porque usan H2 con
-- create-drop y flyway deshabilitado, generando el esquema desde las entidades.

------------------------------------------------------------
-- 1. TRAINING_SET_TEMPLATE.REFERENCE_SLOT_CODE
------------------------------------------------------------
-- TrainingSetTemplate.referenceSlotCode existe en la entidad y en los tres DTOs
-- (request/response/update) desde la v2, pero ninguna migracion creaba la columna.
-- Identifica el slot cuya carga sirve de referencia para calcular la intensidad de este set.

ALTER TABLE training_set_template ADD COLUMN reference_slot_code varchar(10);

ALTER TABLE training_set_template ADD CONSTRAINT training_set_template_reference_slot_code_check
    CHECK (reference_slot_code IN ('SQ1','SQ2','SQ3','BP1','BP2','BP3','DL1','DL2','DL3','A1','A2','A3'));

------------------------------------------------------------
-- 2. TRAINING_SET_TEMPLATE.TARGET_INTENSITY_TYPE
------------------------------------------------------------
-- El check de V4 solo admitia ('RIR','RPE','PERCENTAGE'). El enum IntensityType sustituyo
-- el generico PERCENTAGE por cuatro variantes que declaran la referencia del porcentaje,
-- por lo que cualquier escritura con un valor nuevo violaba el check.

-- Migracion defensiva: no hay filas con 'PERCENTAGE' en ningun entorno conocido (V3 solo
-- siembra 'RIR'), pero el generico equivale al porcentaje sobre el 1RM.

ALTER TABLE training_set_template DROP CONSTRAINT IF EXISTS training_set_template_target_intensity_type_check;

UPDATE training_set_template
   SET target_intensity_type = 'PERCENTAGE_1RM'
 WHERE target_intensity_type = 'PERCENTAGE';


ALTER TABLE training_set_template ADD CONSTRAINT training_set_template_target_intensity_type_check
    CHECK (target_intensity_type IN ('RIR','RPE','PERCENTAGE_1RM','PERCENTAGE_ERM',
                                   'PERCENTAGE_DIALY_TOP'));
