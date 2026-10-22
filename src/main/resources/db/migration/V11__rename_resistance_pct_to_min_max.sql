-- V11__rename_resistance_pct_to_min_max.sql
-- La resistencia acomodada (bandas/cadenas) se pauta como un RANGO de % ("12-15% en banda"),
-- no como top/bottom del ROM: la tension abajo no se mide, solo que la banda este tensa.
-- Renombramos top/bottom -> min/max para alinear con targetRepsMin/Max y targetIntensityMin/Max.
-- Siguen firmados (negativos = reverse bands).

ALTER TABLE training_set_template
    DROP CONSTRAINT IF EXISTS training_set_template_target_resistance_top_pct_check;
ALTER TABLE training_set_template
    DROP CONSTRAINT IF EXISTS training_set_template_target_resistance_bottom_pct_check;

ALTER TABLE training_set_template
    RENAME COLUMN target_resistance_top_pct TO target_resistance_min_pct;
ALTER TABLE training_set_template
    RENAME COLUMN target_resistance_bottom_pct TO target_resistance_max_pct;

ALTER TABLE training_set_template ADD CONSTRAINT training_set_template_target_resistance_min_pct_check
    CHECK (target_resistance_min_pct >= -100 AND target_resistance_min_pct <= 100);
ALTER TABLE training_set_template ADD CONSTRAINT training_set_template_target_resistance_max_pct_check
    CHECK (target_resistance_max_pct >= -100 AND target_resistance_max_pct <= 100);
