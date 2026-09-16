-- V9__sync_domain_v2.sql
-- Cierra el drift restante entre las entidades JPA y el esquema tras la refactorizacion
-- "bbdd v2 + metrics domain". Produccion arranca con ddl-auto=validate, asi que cualquier
-- desviacion impide el arranque.

------------------------------------------------------------
-- 1. TRAINING_SET_TEMPLATE.REFERENCE_SLOT_CODE
------------------------------------------------------------
-- De QUE slot se toma el 100% para calcular la intensidad de este set.
-- Nullable: null significa "el slot del propio set".

ALTER TABLE training_set_template ADD COLUMN reference_slot_code varchar(10);

ALTER TABLE training_set_template ADD CONSTRAINT training_set_template_reference_slot_code_check
    CHECK (reference_slot_code IN ('SQ1','SQ2','SQ3','BP1','BP2','BP3','DL1','DL2','DL3','A1','A2','A3'));

------------------------------------------------------------
-- 2. TRAINING_SET_TEMPLATE.REFERENCE_SOURCE
------------------------------------------------------------
-- QUE VERSION de ese levantamiento: el ejercicio que ocupa el slot (SELF) o el
-- levantamiento de competicion de ese patron (COMPETITION).
-- Nullable: el resolver interpreta null como SELF.

ALTER TABLE training_set_template ADD COLUMN reference_source varchar(20);

ALTER TABLE training_set_template ADD CONSTRAINT training_set_template_reference_source_check
    CHECK (reference_source IN ('SELF','COMPETITION'));

------------------------------------------------------------
-- 3. TRAINING_SET_TEMPLATE.TARGET_INTENSITY_TYPE
------------------------------------------------------------
-- El check de V4 solo admitia ('RIR','RPE','PERCENTAGE'). El enum IntensityType sustituyo
-- el generico PERCENTAGE por tres variantes que declaran la referencia del porcentaje.
--
-- El DROP va ANTES del UPDATE: escribir 'PERCENTAGE_1RM' con el check viejo todavia vivo
-- lo violaria y abortaria la migracion.

ALTER TABLE training_set_template DROP CONSTRAINT IF EXISTS training_set_template_target_intensity_type_check;

UPDATE training_set_template
SET target_intensity_type = 'PERCENTAGE_1RM'
WHERE target_intensity_type = 'PERCENTAGE';

ALTER TABLE training_set_template ADD CONSTRAINT training_set_template_target_intensity_type_check
    CHECK (target_intensity_type IN ('RIR','RPE','PERCENTAGE_1RM','PERCENTAGE_ERM',
                                     'PERCENTAGE_DIALY_TOP'));