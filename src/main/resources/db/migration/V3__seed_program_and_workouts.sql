-- V3__seed_program_and_workouts.sql
-- Seed: Block A (Metodo Conjugado) — plantillas + historial de entrenos + sesiones futuras.
-- Usa CURRENT_DATE para que los datos sean siempre "actuales" independientemente
-- de cuando se ejecute la migracion.
-- Las fechas pasadas representan sesiones completadas con datos reales.
-- Las fechas futuras representan sesiones planificadas (sin entradas ni series aun).

------------------------------------------------------------
-- TRAINING SESSION TEMPLATES: Block A (4 tipos de dia)
------------------------------------------------------------

INSERT INTO training_session_template (block_name, week_number, day_in_week, conjugate_day_type, notes) VALUES
  ('Block A', 1, 1, 'ME_LOWER', 'Fuerza maxima tren inferior: sentadilla competicion + cadena posterior + accesorios'),
  ('Block A', 1, 3, 'ME_UPPER', 'Fuerza maxima tren superior: press banca competicion + espalda + accesorios'),
  ('Block A', 1, 5, 'DE_LOWER', 'Esfuerzo dinamico tren inferior: velocidad ~60% 1RM + core'),
  ('Block A', 1, 6, 'RE_UPPER', 'Esfuerzo repeticion tren superior: volumen pecho + espalda + brazos');

------------------------------------------------------------
-- TRAINING SET TEMPLATES (5 ejercicios por tipo de dia)
------------------------------------------------------------

-- ME_LOWER
INSERT INTO training_set_template
  (session_template_id, exercise_id, display_order, target_sets, target_reps, target_weight, target_intensity, target_intensity_type, rest, notes)
VALUES
  ((SELECT id FROM training_session_template WHERE block_name='Block A' AND conjugate_day_type='ME_LOWER'), 1,  1, 5, 3, 100, 1, 'RIR', '3-5 min', 'Sentadilla low bar de competicion'),
  ((SELECT id FROM training_session_template WHERE block_name='Block A' AND conjugate_day_type='ME_LOWER'), 33, 2, 3, 8, 80,  2, 'RIR', '2 min',   'RDL cadena posterior'),
  ((SELECT id FROM training_session_template WHERE block_name='Block A' AND conjugate_day_type='ME_LOWER'), 80, 3, 3, 10, 150, 2, 'RIR', '2 min',   'Prensa cuadriceps accesorio'),
  ((SELECT id FROM training_session_template WHERE block_name='Block A' AND conjugate_day_type='ME_LOWER'), 82, 4, 3, 12, 40,  2, 'RIR', '90 sec',  'Curl femoral isquiotibiales'),
  ((SELECT id FROM training_session_template WHERE block_name='Block A' AND conjugate_day_type='ME_LOWER'), 89, 5, 3, 45, NULL, NULL, NULL, '60 sec', '45 segundos por serie, antiextension core');

-- ME_UPPER
INSERT INTO training_set_template
  (session_template_id, exercise_id, display_order, target_sets, target_reps, target_weight, target_intensity, target_intensity_type, rest, notes)
VALUES
  ((SELECT id FROM training_session_template WHERE block_name='Block A' AND conjugate_day_type='ME_UPPER'), 15, 1, 5, 3, 80,  1, 'RIR', '3-5 min', 'Press banca de competicion'),
  ((SELECT id FROM training_session_template WHERE block_name='Block A' AND conjugate_day_type='ME_UPPER'), 59, 2, 4, 6, 70,  2, 'RIR', '2 min',   'Remo con barra espalda gruesa'),
  ((SELECT id FROM training_session_template WHERE block_name='Block A' AND conjugate_day_type='ME_UPPER'), 45, 3, 3, 8, 50,  2, 'RIR', '2 min',   'Press militar hombro'),
  ((SELECT id FROM training_session_template WHERE block_name='Block A' AND conjugate_day_type='ME_UPPER'), 77, 4, 3, 12, 20, 1, 'RIR', '90 sec',  'Extension triceps en polea'),
  ((SELECT id FROM training_session_template WHERE block_name='Block A' AND conjugate_day_type='ME_UPPER'), 64, 5, 3, 15, 15, 1, 'RIR', '60 sec',  'Salud manguito rotador');

-- DE_LOWER
INSERT INTO training_set_template
  (session_template_id, exercise_id, display_order, target_sets, target_reps, target_weight, target_intensity, target_intensity_type, rest, notes)
VALUES
  ((SELECT id FROM training_session_template WHERE block_name='Block A' AND conjugate_day_type='DE_LOWER'), 6,  1, 5, 2, 70,  3, 'RIR', '90 sec',  'Box squat velocidad ~60% 1RM'),
  ((SELECT id FROM training_session_template WHERE block_name='Block A' AND conjugate_day_type='DE_LOWER'), 30, 2, 5, 2, 90,  3, 'RIR', '90 sec',  'Pull convencional velocidad ~65% 1RM'),
  ((SELECT id FROM training_session_template WHERE block_name='Block A' AND conjugate_day_type='DE_LOWER'), 36, 3, 3, 10, 40,  2, 'RIR', '2 min',   'Good morning cadena posterior'),
  ((SELECT id FROM training_session_template WHERE block_name='Block A' AND conjugate_day_type='DE_LOWER'), 81, 4, 3, 15, 30,  2, 'RIR', '90 sec',  'Extension cuadriceps aislamiento'),
  ((SELECT id FROM training_session_template WHERE block_name='Block A' AND conjugate_day_type='DE_LOWER'), 90, 5, 3, 10, NULL, NULL, NULL, '60 sec', 'Rueda abdominal antiextension');

-- RE_UPPER
INSERT INTO training_set_template
  (session_template_id, exercise_id, display_order, target_sets, target_reps, target_weight, target_intensity, target_intensity_type, rest, notes)
VALUES
  ((SELECT id FROM training_session_template WHERE block_name='Block A' AND conjugate_day_type='RE_UPPER'), 23, 1, 3, 12, 22,   2, 'RIR', '2 min',   'Incline press volumen pecho'),
  ((SELECT id FROM training_session_template WHERE block_name='Block A' AND conjugate_day_type='RE_UPPER'), 56, 2, 4, 8,  NULL,  2, 'RIR', '2 min',   'Dominadas pronadas peso corporal'),
  ((SELECT id FROM training_session_template WHERE block_name='Block A' AND conjugate_day_type='RE_UPPER'), 61, 3, 3, 12, 24,   2, 'RIR', '90 sec',  'Remo mancuerna 1 brazo'),
  ((SELECT id FROM training_session_template WHERE block_name='Block A' AND conjugate_day_type='RE_UPPER'), 66, 4, 3, 15, 8,    1, 'RIR', '60 sec',  'Elevaciones laterales hombro'),
  ((SELECT id FROM training_session_template WHERE block_name='Block A' AND conjugate_day_type='RE_UPPER'), 74, 5, 3, 12, 12,   1, 'RIR', '60 sec',  'Curl biceps mancuerna');

------------------------------------------------------------
-- WORKOUT SESSIONS: pasadas (con datos) + futuras (planificadas)
-- DO block necesario para capturar IDs autogenerados y usarlos
-- en workout_entry y workout_set de forma encadenada.
------------------------------------------------------------

DO $$
DECLARE
  tpl_me_lower BIGINT;
  tpl_me_upper BIGINT;
  tpl_de_lower BIGINT;
  tpl_re_upper BIGINT;

  sess_id  BIGINT;
  e1 BIGINT;
  e2 BIGINT;
  e3 BIGINT;
  e4 BIGINT;
  e5 BIGINT;

  ts TIMESTAMPTZ;
BEGIN
  SELECT id INTO tpl_me_lower FROM training_session_template WHERE block_name='Block A' AND conjugate_day_type='ME_LOWER';
  SELECT id INTO tpl_me_upper FROM training_session_template WHERE block_name='Block A' AND conjugate_day_type='ME_UPPER';
  SELECT id INTO tpl_de_lower FROM training_session_template WHERE block_name='Block A' AND conjugate_day_type='DE_LOWER';
  SELECT id INTO tpl_re_upper FROM training_session_template WHERE block_name='Block A' AND conjugate_day_type='RE_UPPER';

  -- ================================================================
  -- SEMANA -4 (hace 28-25 dias) — carga media
  -- ================================================================

  -- ME_LOWER dia -28
  ts := NOW() - INTERVAL '28 days';
  INSERT INTO workout_session (session_date, training_session_template_id) VALUES (CURRENT_DATE - 28, tpl_me_lower) RETURNING id INTO sess_id;

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 1, false, 5, 3, 95, '2 RIR') RETURNING id INTO e1;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e1, 1, 3, 95, 'RIR', 3, ts, ts),
    (e1, 2, 3, 95, 'RIR', 2, ts, ts),
    (e1, 3, 3, 95, 'RIR', 2, ts, ts),
    (e1, 4, 3, 95, 'RIR', 2, ts, ts),
    (e1, 5, 2, 95, 'RIR', 1, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 33, false, 3, 8, 80, '2 RIR') RETURNING id INTO e2;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e2, 1, 8, 80, 'RIR', 3, ts, ts),
    (e2, 2, 8, 80, 'RIR', 2, ts, ts),
    (e2, 3, 8, 80, 'RIR', 2, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 80, false, 3, 10, 150, '2 RIR') RETURNING id INTO e3;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e3, 1, 10, 150, 'RIR', 2, ts, ts),
    (e3, 2, 10, 150, 'RIR', 2, ts, ts),
    (e3, 3, 10, 150, 'RIR', 2, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 82, false, 3, 12, 40, '1 RIR') RETURNING id INTO e4;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e4, 1, 12, 40, 'RIR', 2, ts, ts),
    (e4, 2, 12, 40, 'RIR', 1, ts, ts),
    (e4, 3, 12, 40, 'RIR', 1, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 89, false, 3, 45, NULL, NULL) RETURNING id INTO e5;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e5, 1, 45, NULL, NULL, NULL, ts, ts),
    (e5, 2, 45, NULL, NULL, NULL, ts, ts),
    (e5, 3, 40, NULL, NULL, NULL, ts, ts);

  -- ME_UPPER dia -26
  ts := NOW() - INTERVAL '26 days';
  INSERT INTO workout_session (session_date, training_session_template_id) VALUES (CURRENT_DATE - 26, tpl_me_upper) RETURNING id INTO sess_id;

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 15, false, 5, 3, 77.5, '2 RIR') RETURNING id INTO e1;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e1, 1, 3, 77.5, 'RIR', 3, ts, ts),
    (e1, 2, 3, 77.5, 'RIR', 2, ts, ts),
    (e1, 3, 3, 77.5, 'RIR', 2, ts, ts),
    (e1, 4, 3, 77.5, 'RIR', 2, ts, ts),
    (e1, 5, 2, 77.5, 'RIR', 1, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 59, false, 4, 6, 70, '2 RIR') RETURNING id INTO e2;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e2, 1, 6, 70, 'RIR', 3, ts, ts),
    (e2, 2, 6, 70, 'RIR', 2, ts, ts),
    (e2, 3, 6, 70, 'RIR', 2, ts, ts),
    (e2, 4, 6, 70, 'RIR', 2, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 45, false, 3, 8, 50, '2 RIR') RETURNING id INTO e3;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e3, 1, 8, 50, 'RIR', 3, ts, ts),
    (e3, 2, 8, 50, 'RIR', 2, ts, ts),
    (e3, 3, 8, 50, 'RIR', 2, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 77, false, 3, 12, 20, '1 RIR') RETURNING id INTO e4;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e4, 1, 12, 20, 'RIR', 2, ts, ts),
    (e4, 2, 12, 20, 'RIR', 1, ts, ts),
    (e4, 3, 12, 20, 'RIR', 1, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 64, false, 3, 15, 15, '1 RIR') RETURNING id INTO e5;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e5, 1, 15, 15, 'RIR', 2, ts, ts),
    (e5, 2, 15, 15, 'RIR', 1, ts, ts),
    (e5, 3, 15, 15, 'RIR', 1, ts, ts);

  -- DE_LOWER dia -24
  ts := NOW() - INTERVAL '24 days';
  INSERT INTO workout_session (session_date, training_session_template_id) VALUES (CURRENT_DATE - 24, tpl_de_lower) RETURNING id INTO sess_id;

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 6, false, 5, 2, 70, '3 RIR') RETURNING id INTO e1;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e1, 1, 2, 70, 'RIR', 4, ts, ts),
    (e1, 2, 2, 70, 'RIR', 4, ts, ts),
    (e1, 3, 2, 70, 'RIR', 3, ts, ts),
    (e1, 4, 2, 70, 'RIR', 3, ts, ts),
    (e1, 5, 2, 70, 'RIR', 3, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 30, false, 5, 2, 90, '3 RIR') RETURNING id INTO e2;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e2, 1, 2, 90, 'RIR', 4, ts, ts),
    (e2, 2, 2, 90, 'RIR', 4, ts, ts),
    (e2, 3, 2, 90, 'RIR', 3, ts, ts),
    (e2, 4, 2, 90, 'RIR', 3, ts, ts),
    (e2, 5, 2, 90, 'RIR', 3, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 36, false, 3, 10, 40, '2 RIR') RETURNING id INTO e3;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e3, 1, 10, 40, 'RIR', 3, ts, ts),
    (e3, 2, 10, 40, 'RIR', 2, ts, ts),
    (e3, 3, 10, 40, 'RIR', 2, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 81, false, 3, 15, 30, '2 RIR') RETURNING id INTO e4;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e4, 1, 15, 30, 'RIR', 2, ts, ts),
    (e4, 2, 15, 30, 'RIR', 2, ts, ts),
    (e4, 3, 15, 30, 'RIR', 2, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 90, false, 3, 10, NULL, NULL) RETURNING id INTO e5;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e5, 1, 10, NULL, NULL, NULL, ts, ts),
    (e5, 2, 10, NULL, NULL, NULL, ts, ts),
    (e5, 3, 8,  NULL, NULL, NULL, ts, ts);

  -- RE_UPPER dia -23
  ts := NOW() - INTERVAL '23 days';
  INSERT INTO workout_session (session_date, training_session_template_id) VALUES (CURRENT_DATE - 23, tpl_re_upper) RETURNING id INTO sess_id;

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 23, false, 3, 12, 22, '2 RIR') RETURNING id INTO e1;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e1, 1, 12, 22, 'RIR', 3, ts, ts),
    (e1, 2, 12, 22, 'RIR', 2, ts, ts),
    (e1, 3, 11, 22, 'RIR', 1, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 56, false, 4, 8, NULL, '2 RIR') RETURNING id INTO e2;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e2, 1, 8, NULL, 'RIR', 3, ts, ts),
    (e2, 2, 8, NULL, 'RIR', 2, ts, ts),
    (e2, 3, 7, NULL, 'RIR', 2, ts, ts),
    (e2, 4, 7, NULL, 'RIR', 1, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 61, false, 3, 12, 24, '2 RIR') RETURNING id INTO e3;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e3, 1, 12, 24, 'RIR', 2, ts, ts),
    (e3, 2, 12, 24, 'RIR', 2, ts, ts),
    (e3, 3, 12, 24, 'RIR', 2, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 66, false, 3, 15, 8, '1 RIR') RETURNING id INTO e4;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e4, 1, 15, 8, 'RIR', 2, ts, ts),
    (e4, 2, 15, 8, 'RIR', 1, ts, ts),
    (e4, 3, 14, 8, 'RIR', 1, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 74, false, 3, 12, 12, '1 RIR') RETURNING id INTO e5;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e5, 1, 12, 12, 'RIR', 2, ts, ts),
    (e5, 2, 12, 12, 'RIR', 1, ts, ts),
    (e5, 3, 11, 12, 'RIR', 1, ts, ts);

  -- ================================================================
  -- SEMANA -3 (hace 21-18 dias) — DESCARGA (deload)
  -- ================================================================

  -- ME_LOWER dia -21 (descarga: -10% peso, menos series)
  ts := NOW() - INTERVAL '21 days';
  INSERT INTO workout_session (session_date, training_session_template_id) VALUES (CURRENT_DATE - 21, tpl_me_lower) RETURNING id INTO sess_id;

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 1, false, 3, 3, 85, '4 RIR') RETURNING id INTO e1;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e1, 1, 3, 85, 'RIR', 5, ts, ts),
    (e1, 2, 3, 85, 'RIR', 4, ts, ts),
    (e1, 3, 3, 85, 'RIR', 4, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 33, false, 2, 8, 70, '3 RIR') RETURNING id INTO e2;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e2, 1, 8, 70, 'RIR', 4, ts, ts),
    (e2, 2, 8, 70, 'RIR', 3, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 80, false, 2, 10, 130, '3 RIR') RETURNING id INTO e3;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e3, 1, 10, 130, 'RIR', 4, ts, ts),
    (e3, 2, 10, 130, 'RIR', 3, ts, ts);

  -- ME_UPPER dia -19 (descarga)
  ts := NOW() - INTERVAL '19 days';
  INSERT INTO workout_session (session_date, training_session_template_id) VALUES (CURRENT_DATE - 19, tpl_me_upper) RETURNING id INTO sess_id;

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 15, false, 3, 3, 70, '4 RIR') RETURNING id INTO e1;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e1, 1, 3, 70, 'RIR', 5, ts, ts),
    (e1, 2, 3, 70, 'RIR', 4, ts, ts),
    (e1, 3, 3, 70, 'RIR', 4, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 59, false, 3, 6, 60, '3 RIR') RETURNING id INTO e2;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e2, 1, 6, 60, 'RIR', 4, ts, ts),
    (e2, 2, 6, 60, 'RIR', 3, ts, ts),
    (e2, 3, 6, 60, 'RIR', 3, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 45, false, 2, 8, 42.5, '3 RIR') RETURNING id INTO e3;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e3, 1, 8, 42.5, 'RIR', 4, ts, ts),
    (e3, 2, 8, 42.5, 'RIR', 3, ts, ts);

  -- DE_LOWER dia -17 (descarga)
  ts := NOW() - INTERVAL '17 days';
  INSERT INTO workout_session (session_date, training_session_template_id) VALUES (CURRENT_DATE - 17, tpl_de_lower) RETURNING id INTO sess_id;

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 6, false, 3, 2, 62.5, '4 RIR') RETURNING id INTO e1;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e1, 1, 2, 62.5, 'RIR', 5, ts, ts),
    (e1, 2, 2, 62.5, 'RIR', 5, ts, ts),
    (e1, 3, 2, 62.5, 'RIR', 4, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 30, false, 3, 2, 80, '4 RIR') RETURNING id INTO e2;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e2, 1, 2, 80, 'RIR', 5, ts, ts),
    (e2, 2, 2, 80, 'RIR', 5, ts, ts),
    (e2, 3, 2, 80, 'RIR', 4, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 36, false, 2, 10, 35, '3 RIR') RETURNING id INTO e3;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e3, 1, 10, 35, 'RIR', 4, ts, ts),
    (e3, 2, 10, 35, 'RIR', 3, ts, ts);

  -- RE_UPPER dia -16 (descarga)
  ts := NOW() - INTERVAL '16 days';
  INSERT INTO workout_session (session_date, training_session_template_id) VALUES (CURRENT_DATE - 16, tpl_re_upper) RETURNING id INTO sess_id;

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 23, false, 2, 12, 20, '3 RIR') RETURNING id INTO e1;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e1, 1, 12, 20, 'RIR', 4, ts, ts),
    (e1, 2, 12, 20, 'RIR', 3, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 56, false, 3, 8, NULL, '3 RIR') RETURNING id INTO e2;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e2, 1, 8, NULL, 'RIR', 4, ts, ts),
    (e2, 2, 8, NULL, 'RIR', 3, ts, ts),
    (e2, 3, 8, NULL, 'RIR', 3, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 61, false, 2, 12, 22, '3 RIR') RETURNING id INTO e3;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e3, 1, 12, 22, 'RIR', 3, ts, ts),
    (e3, 2, 12, 22, 'RIR', 3, ts, ts);

  -- ================================================================
  -- SEMANA -2 (hace 14-11 dias) — ACUMULACION (vuelta a cargar)
  -- ================================================================

  -- ME_LOWER dia -14
  ts := NOW() - INTERVAL '14 days';
  INSERT INTO workout_session (session_date, training_session_template_id) VALUES (CURRENT_DATE - 14, tpl_me_lower) RETURNING id INTO sess_id;

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 1, false, 5, 3, 100, '1 RIR') RETURNING id INTO e1;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e1, 1, 3, 100, 'RIR', 2, ts, ts),
    (e1, 2, 3, 100, 'RIR', 2, ts, ts),
    (e1, 3, 3, 100, 'RIR', 1, ts, ts),
    (e1, 4, 3, 100, 'RIR', 1, ts, ts),
    (e1, 5, 2, 100, 'RIR', 1, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 33, false, 3, 8, 82.5, '2 RIR') RETURNING id INTO e2;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e2, 1, 8, 82.5, 'RIR', 3, ts, ts),
    (e2, 2, 8, 82.5, 'RIR', 2, ts, ts),
    (e2, 3, 8, 82.5, 'RIR', 2, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 80, false, 3, 10, 155, '2 RIR') RETURNING id INTO e3;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e3, 1, 10, 155, 'RIR', 2, ts, ts),
    (e3, 2, 10, 155, 'RIR', 2, ts, ts),
    (e3, 3, 10, 155, 'RIR', 2, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 82, false, 3, 12, 42.5, '1 RIR') RETURNING id INTO e4;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e4, 1, 12, 42.5, 'RIR', 2, ts, ts),
    (e4, 2, 12, 42.5, 'RIR', 1, ts, ts),
    (e4, 3, 11, 42.5, 'RIR', 1, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 89, false, 3, 45, NULL, NULL) RETURNING id INTO e5;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e5, 1, 45, NULL, NULL, NULL, ts, ts),
    (e5, 2, 45, NULL, NULL, NULL, ts, ts),
    (e5, 3, 45, NULL, NULL, NULL, ts, ts);

  -- ME_UPPER dia -12
  ts := NOW() - INTERVAL '12 days';
  INSERT INTO workout_session (session_date, training_session_template_id) VALUES (CURRENT_DATE - 12, tpl_me_upper) RETURNING id INTO sess_id;

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 15, false, 5, 3, 82.5, '1 RIR') RETURNING id INTO e1;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e1, 1, 3, 82.5, 'RIR', 2, ts, ts),
    (e1, 2, 3, 82.5, 'RIR', 2, ts, ts),
    (e1, 3, 3, 82.5, 'RIR', 1, ts, ts),
    (e1, 4, 3, 82.5, 'RIR', 1, ts, ts),
    (e1, 5, 2, 82.5, 'RIR', 1, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 59, false, 4, 6, 72.5, '2 RIR') RETURNING id INTO e2;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e2, 1, 6, 72.5, 'RIR', 3, ts, ts),
    (e2, 2, 6, 72.5, 'RIR', 2, ts, ts),
    (e2, 3, 6, 72.5, 'RIR', 2, ts, ts),
    (e2, 4, 6, 72.5, 'RIR', 2, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 45, false, 3, 8, 52.5, '1 RIR') RETURNING id INTO e3;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e3, 1, 8, 52.5, 'RIR', 3, ts, ts),
    (e3, 2, 8, 52.5, 'RIR', 2, ts, ts),
    (e3, 3, 7, 52.5, 'RIR', 1, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 77, false, 3, 12, 22.5, '1 RIR') RETURNING id INTO e4;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e4, 1, 12, 22.5, 'RIR', 2, ts, ts),
    (e4, 2, 12, 22.5, 'RIR', 1, ts, ts),
    (e4, 3, 11, 22.5, 'RIR', 1, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 64, false, 3, 15, 17.5, '1 RIR') RETURNING id INTO e5;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e5, 1, 15, 17.5, 'RIR', 1, ts, ts),
    (e5, 2, 15, 17.5, 'RIR', 1, ts, ts),
    (e5, 3, 14, 17.5, 'RIR', 1, ts, ts);

  -- DE_LOWER dia -10
  ts := NOW() - INTERVAL '10 days';
  INSERT INTO workout_session (session_date, training_session_template_id) VALUES (CURRENT_DATE - 10, tpl_de_lower) RETURNING id INTO sess_id;

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 6, false, 5, 2, 72.5, '3 RIR') RETURNING id INTO e1;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e1, 1, 2, 72.5, 'RIR', 4, ts, ts),
    (e1, 2, 2, 72.5, 'RIR', 4, ts, ts),
    (e1, 3, 2, 72.5, 'RIR', 3, ts, ts),
    (e1, 4, 2, 72.5, 'RIR', 3, ts, ts),
    (e1, 5, 2, 72.5, 'RIR', 3, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 30, false, 5, 2, 92.5, '3 RIR') RETURNING id INTO e2;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e2, 1, 2, 92.5, 'RIR', 4, ts, ts),
    (e2, 2, 2, 92.5, 'RIR', 4, ts, ts),
    (e2, 3, 2, 92.5, 'RIR', 3, ts, ts),
    (e2, 4, 2, 92.5, 'RIR', 3, ts, ts),
    (e2, 5, 2, 92.5, 'RIR', 3, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 36, false, 3, 10, 42.5, '2 RIR') RETURNING id INTO e3;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e3, 1, 10, 42.5, 'RIR', 3, ts, ts),
    (e3, 2, 10, 42.5, 'RIR', 2, ts, ts),
    (e3, 3, 10, 42.5, 'RIR', 2, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 81, false, 3, 15, 32.5, '2 RIR') RETURNING id INTO e4;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e4, 1, 15, 32.5, 'RIR', 2, ts, ts),
    (e4, 2, 15, 32.5, 'RIR', 2, ts, ts),
    (e4, 3, 14, 32.5, 'RIR', 2, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 90, false, 3, 10, NULL, NULL) RETURNING id INTO e5;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e5, 1, 10, NULL, NULL, NULL, ts, ts),
    (e5, 2, 10, NULL, NULL, NULL, ts, ts),
    (e5, 3, 10, NULL, NULL, NULL, ts, ts);

  -- RE_UPPER dia -9
  ts := NOW() - INTERVAL '9 days';
  INSERT INTO workout_session (session_date, training_session_template_id) VALUES (CURRENT_DATE - 9, tpl_re_upper) RETURNING id INTO sess_id;

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 23, false, 3, 12, 24, '2 RIR') RETURNING id INTO e1;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e1, 1, 12, 24, 'RIR', 3, ts, ts),
    (e1, 2, 12, 24, 'RIR', 2, ts, ts),
    (e1, 3, 11, 24, 'RIR', 1, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 56, false, 4, 8, NULL, '2 RIR') RETURNING id INTO e2;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e2, 1, 8, NULL, 'RIR', 3, ts, ts),
    (e2, 2, 8, NULL, 'RIR', 2, ts, ts),
    (e2, 3, 8, NULL, 'RIR', 2, ts, ts),
    (e2, 4, 7, NULL, 'RIR', 1, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 61, false, 3, 12, 26, '2 RIR') RETURNING id INTO e3;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e3, 1, 12, 26, 'RIR', 2, ts, ts),
    (e3, 2, 12, 26, 'RIR', 2, ts, ts),
    (e3, 3, 12, 26, 'RIR', 2, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 66, false, 3, 15, 9, '1 RIR') RETURNING id INTO e4;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e4, 1, 15, 9, 'RIR', 2, ts, ts),
    (e4, 2, 15, 9, 'RIR', 1, ts, ts),
    (e4, 3, 14, 9, 'RIR', 1, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 74, false, 3, 12, 14, '1 RIR') RETURNING id INTO e5;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e5, 1, 12, 14, 'RIR', 2, ts, ts),
    (e5, 2, 12, 14, 'RIR', 1, ts, ts),
    (e5, 3, 12, 14, 'RIR', 1, ts, ts);

  -- ================================================================
  -- SEMANA -1 (hace 7-4 dias) — INTENSIFICACION (semana pico)
  -- ================================================================

  -- ME_LOWER dia -7
  ts := NOW() - INTERVAL '7 days';
  INSERT INTO workout_session (session_date, training_session_template_id) VALUES (CURRENT_DATE - 7, tpl_me_lower) RETURNING id INTO sess_id;

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 1, false, 5, 3, 102.5, '1 RIR') RETURNING id INTO e1;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e1, 1, 3, 102.5, 'RIR', 2, ts, ts),
    (e1, 2, 3, 102.5, 'RIR', 2, ts, ts),
    (e1, 3, 3, 102.5, 'RIR', 1, ts, ts),
    (e1, 4, 3, 102.5, 'RIR', 1, ts, ts),
    (e1, 5, 2, 102.5, 'RIR', 0, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 33, false, 3, 8, 85, '2 RIR') RETURNING id INTO e2;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e2, 1, 8, 85, 'RIR', 2, ts, ts),
    (e2, 2, 8, 85, 'RIR', 2, ts, ts),
    (e2, 3, 8, 85, 'RIR', 2, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 80, false, 3, 10, 160, '2 RIR') RETURNING id INTO e3;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e3, 1, 10, 160, 'RIR', 2, ts, ts),
    (e3, 2, 10, 160, 'RIR', 2, ts, ts),
    (e3, 3, 10, 160, 'RIR', 2, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 82, false, 3, 12, 45, '1 RIR') RETURNING id INTO e4;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e4, 1, 12, 45, 'RIR', 2, ts, ts),
    (e4, 2, 12, 45, 'RIR', 1, ts, ts),
    (e4, 3, 11, 45, 'RIR', 1, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 89, false, 3, 45, NULL, NULL) RETURNING id INTO e5;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e5, 1, 45, NULL, NULL, NULL, ts, ts),
    (e5, 2, 45, NULL, NULL, NULL, ts, ts),
    (e5, 3, 45, NULL, NULL, NULL, ts, ts);

  -- ME_UPPER dia -5
  ts := NOW() - INTERVAL '5 days';
  INSERT INTO workout_session (session_date, training_session_template_id) VALUES (CURRENT_DATE - 5, tpl_me_upper) RETURNING id INTO sess_id;

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 15, false, 5, 3, 85, '1 RIR') RETURNING id INTO e1;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e1, 1, 3, 85, 'RIR', 2, ts, ts),
    (e1, 2, 3, 85, 'RIR', 2, ts, ts),
    (e1, 3, 3, 85, 'RIR', 1, ts, ts),
    (e1, 4, 3, 85, 'RIR', 1, ts, ts),
    (e1, 5, 2, 85, 'RIR', 0, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 59, false, 4, 6, 75, '2 RIR') RETURNING id INTO e2;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e2, 1, 6, 75, 'RIR', 2, ts, ts),
    (e2, 2, 6, 75, 'RIR', 2, ts, ts),
    (e2, 3, 6, 75, 'RIR', 2, ts, ts),
    (e2, 4, 6, 75, 'RIR', 2, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 45, false, 3, 8, 55, '1 RIR') RETURNING id INTO e3;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e3, 1, 8, 55, 'RIR', 2, ts, ts),
    (e3, 2, 8, 55, 'RIR', 1, ts, ts),
    (e3, 3, 7, 55, 'RIR', 1, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 77, false, 3, 12, 22.5, '1 RIR') RETURNING id INTO e4;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e4, 1, 12, 22.5, 'RIR', 1, ts, ts),
    (e4, 2, 12, 22.5, 'RIR', 1, ts, ts),
    (e4, 3, 11, 22.5, 'RIR', 1, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 64, false, 3, 15, 17.5, '1 RIR') RETURNING id INTO e5;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e5, 1, 15, 17.5, 'RIR', 1, ts, ts),
    (e5, 2, 15, 17.5, 'RIR', 1, ts, ts),
    (e5, 3, 15, 17.5, 'RIR', 1, ts, ts);

  -- DE_LOWER dia -3
  ts := NOW() - INTERVAL '3 days';
  INSERT INTO workout_session (session_date, training_session_template_id) VALUES (CURRENT_DATE - 3, tpl_de_lower) RETURNING id INTO sess_id;

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 6, false, 5, 2, 75, '3 RIR') RETURNING id INTO e1;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e1, 1, 2, 75, 'RIR', 4, ts, ts),
    (e1, 2, 2, 75, 'RIR', 4, ts, ts),
    (e1, 3, 2, 75, 'RIR', 3, ts, ts),
    (e1, 4, 2, 75, 'RIR', 3, ts, ts),
    (e1, 5, 2, 75, 'RIR', 3, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 30, false, 5, 2, 95, '3 RIR') RETURNING id INTO e2;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e2, 1, 2, 95, 'RIR', 4, ts, ts),
    (e2, 2, 2, 95, 'RIR', 4, ts, ts),
    (e2, 3, 2, 95, 'RIR', 3, ts, ts),
    (e2, 4, 2, 95, 'RIR', 3, ts, ts),
    (e2, 5, 2, 95, 'RIR', 3, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 36, false, 3, 10, 45, '2 RIR') RETURNING id INTO e3;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e3, 1, 10, 45, 'RIR', 3, ts, ts),
    (e3, 2, 10, 45, 'RIR', 2, ts, ts),
    (e3, 3, 10, 45, 'RIR', 2, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 81, false, 3, 15, 35, '2 RIR') RETURNING id INTO e4;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e4, 1, 15, 35, 'RIR', 2, ts, ts),
    (e4, 2, 15, 35, 'RIR', 2, ts, ts),
    (e4, 3, 14, 35, 'RIR', 2, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 90, false, 3, 12, NULL, NULL) RETURNING id INTO e5;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e5, 1, 12, NULL, NULL, NULL, ts, ts),
    (e5, 2, 12, NULL, NULL, NULL, ts, ts),
    (e5, 3, 10, NULL, NULL, NULL, ts, ts);

  -- RE_UPPER dia -2
  ts := NOW() - INTERVAL '2 days';
  INSERT INTO workout_session (session_date, training_session_template_id) VALUES (CURRENT_DATE - 2, tpl_re_upper) RETURNING id INTO sess_id;

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 23, false, 3, 12, 24, '1 RIR') RETURNING id INTO e1;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e1, 1, 12, 24, 'RIR', 2, ts, ts),
    (e1, 2, 12, 24, 'RIR', 1, ts, ts),
    (e1, 3, 11, 24, 'RIR', 1, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 56, false, 4, 9, NULL, '1 RIR') RETURNING id INTO e2;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e2, 1, 9, NULL, 'RIR', 2, ts, ts),
    (e2, 2, 9, NULL, 'RIR', 1, ts, ts),
    (e2, 3, 8, NULL, 'RIR', 1, ts, ts),
    (e2, 4, 7, NULL, 'RIR', 1, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 61, false, 3, 12, 28, '2 RIR') RETURNING id INTO e3;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e3, 1, 12, 28, 'RIR', 2, ts, ts),
    (e3, 2, 12, 28, 'RIR', 2, ts, ts),
    (e3, 3, 12, 28, 'RIR', 2, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 66, false, 3, 15, 10, '1 RIR') RETURNING id INTO e4;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e4, 1, 15, 10, 'RIR', 2, ts, ts),
    (e4, 2, 15, 10, 'RIR', 1, ts, ts),
    (e4, 3, 14, 10, 'RIR', 1, ts, ts);

  INSERT INTO workout_entry (workout_session_id, exercise_id, is_warmup, actual_sets, actual_reps, actual_plate_weight, actual_rir_or_rpe) VALUES (sess_id, 74, false, 3, 12, 14, '1 RIR') RETURNING id INTO e5;
  INSERT INTO workout_set (workout_entry_id, set_number, reps, weight, intensity_type, intensity_value, created_at, updated_at) VALUES
    (e5, 1, 12, 14, 'RIR', 1, ts, ts),
    (e5, 2, 12, 14, 'RIR', 1, ts, ts),
    (e5, 3, 12, 14, 'RIR', 1, ts, ts);

  -- ================================================================
  -- SESIONES FUTURAS: semanas +1 a +6 (planificadas, sin datos aun)
  -- ================================================================

  INSERT INTO workout_session (session_date, training_session_template_id) VALUES
  -- Semana +1
  (CURRENT_DATE + 5,  tpl_me_lower),
  (CURRENT_DATE + 7,  tpl_me_upper),
  (CURRENT_DATE + 9,  tpl_de_lower),
  (CURRENT_DATE + 10, tpl_re_upper),
  -- Semana +2
  (CURRENT_DATE + 12, tpl_me_lower),
  (CURRENT_DATE + 14, tpl_me_upper),
  (CURRENT_DATE + 16, tpl_de_lower),
  (CURRENT_DATE + 17, tpl_re_upper),
  -- Semana +3
  (CURRENT_DATE + 19, tpl_me_lower),
  (CURRENT_DATE + 21, tpl_me_upper),
  (CURRENT_DATE + 23, tpl_de_lower),
  (CURRENT_DATE + 24, tpl_re_upper),
  -- Semana +4
  (CURRENT_DATE + 26, tpl_me_lower),
  (CURRENT_DATE + 28, tpl_me_upper),
  (CURRENT_DATE + 30, tpl_de_lower),
  (CURRENT_DATE + 31, tpl_re_upper),
  -- Semana +5
  (CURRENT_DATE + 33, tpl_me_lower),
  (CURRENT_DATE + 35, tpl_me_upper),
  (CURRENT_DATE + 37, tpl_de_lower),
  (CURRENT_DATE + 38, tpl_re_upper),
  -- Semana +6
  (CURRENT_DATE + 40, tpl_me_lower),
  (CURRENT_DATE + 42, tpl_me_upper),
  (CURRENT_DATE + 44, tpl_de_lower),
  (CURRENT_DATE + 45, tpl_re_upper);

END $$;
