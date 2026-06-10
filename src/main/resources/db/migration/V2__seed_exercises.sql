-- Seed inicial del catalogo de ejercicios.
-- Cubre los patrones principales (SQUAT, BENCH, DEADLIFT, PRESS, OLY, ACCESSORY)
-- y la mayoria de combinaciones de implement / setup / grip / stance / height_mode / loading_method.

------------------------------------------------------------
-- SQUAT
------------------------------------------------------------
insert into exercise (base_name, main_pattern, implement, setup, stance, grip, loading_unit) values
    ('Sentadilla Low Bar',       'SQUAT', 'BARBELL',      'LOWBAR',  'MEDIUM', 'PRONATED', 'KG'),
    ('Sentadilla High Bar',      'SQUAT', 'BARBELL',      'HIGHBAR', 'MEDIUM', 'PRONATED', 'KG'),
    ('Front Squat',              'SQUAT', 'BARBELL',      'FRONT',   'MEDIUM', 'NONE',     'KG'),
    ('Safety Bar Squat',         'SQUAT', 'SAFETY_BAR',   'SSB',     'MEDIUM', 'NONE',     'KG'),
    ('Zercher Squat',            'SQUAT', 'BARBELL',      'ZERCHER', 'MEDIUM', 'NONE',     'KG'),
    ('Box Squat',                'SQUAT', 'BARBELL',      'BOX',     'WIDE',   'PRONATED', 'KG'),
    ('Cambered Bar Squat',       'SQUAT', 'CAMBERED_BAR', 'CAMBERED','MEDIUM', 'PRONATED', 'KG'),
    ('Smith Machine Squat',      'SQUAT', 'SMITH',        'STANDARD','MEDIUM', 'PRONATED', 'KG'),
    ('Bulgarian Split Squat',    'SQUAT', 'DUMBBELL',     'SPLIT',   'NONE',   'NEUTRAL',  'KG'),
    ('Goblet Squat',             'SQUAT', 'KETTLEBELL',   'STANDARD','MEDIUM', 'NEUTRAL',  'KG');

-- Variantes con pausa / height / loading method
insert into exercise (base_name, main_pattern, implement, setup, stance, grip, pause_seconds, height_mode, height_length, loading_method, loading_unit) values
    ('Pause Squat',              'SQUAT', 'BARBELL', 'LOWBAR', 'MEDIUM', 'PRONATED', 3,    'NONE',   null, 'NONE',  'KG'),
    ('Pin Squat',                'SQUAT', 'BARBELL', 'LOWBAR', 'MEDIUM', 'PRONATED', null, 'PINS',   null, 'NONE',  'KG'),
    ('Squat con Bandas',         'SQUAT', 'BARBELL', 'LOWBAR', 'MEDIUM', 'PRONATED', null, 'NONE',   null, 'BANDS', 'KG'),
    ('Squat con Cadenas',        'SQUAT', 'BARBELL', 'LOWBAR', 'MEDIUM', 'PRONATED', null, 'NONE',   null, 'CHAINS','KG');

------------------------------------------------------------
-- BENCH
------------------------------------------------------------
insert into exercise (base_name, main_pattern, implement, setup, stance, grip, loading_unit) values
    ('Bench Press',              'BENCH', 'BARBELL',   'STANDARD',   'MEDIUM', 'MEDIUM',   'KG'),
    ('Close Grip Bench Press',   'BENCH', 'BARBELL',   'CLOSE_GRIP', 'MEDIUM', 'CLOSE',    'KG'),
    ('Larsen Press',             'BENCH', 'BARBELL',   'STANDARD',   'LARSEN', 'MEDIUM',   'KG'),
    ('Bench Press Paralimpico',  'BENCH', 'BARBELL',   'STANDARD',   'PARALYMPIC','MEDIUM','KG'),
    ('Incline Bench Press',      'BENCH', 'BARBELL',   'STANDARD',   'MEDIUM', 'MEDIUM',   'KG'),
    ('Floor Press',              'BENCH', 'BARBELL',   'STANDARD',   'MEDIUM', 'MEDIUM',   'KG'),
    ('Swiss Bar Bench Press',    'BENCH', 'SWISS_BAR', 'STANDARD',   'MEDIUM', 'NEUTRAL',  'KG'),
    ('DB Bench Press',           'BENCH', 'DUMBBELL',  'STANDARD',   'MEDIUM', 'NEUTRAL',  'KG'),
    ('DB Incline Press',         'BENCH', 'DUMBBELL',  'STANDARD',   'MEDIUM', 'NEUTRAL',  'KG');

-- Variantes con pausa / height / loading method
insert into exercise (base_name, main_pattern, implement, setup, stance, grip, pause_seconds, height_mode, height_length, loading_method, loading_unit) values
    ('Pause Bench Press',        'BENCH', 'BARBELL', 'STANDARD',   'MEDIUM', 'MEDIUM', 2,    'NONE',  null, 'NONE',  'KG'),
    ('Spoto Press',              'BENCH', 'BARBELL', 'STANDARD',   'MEDIUM', 'MEDIUM', 1,    'NONE',  null, 'NONE',  'KG'),
    ('Board Press 2 Boards',     'BENCH', 'BARBELL', 'STANDARD',   'MEDIUM', 'MEDIUM', null, 'BOARD', 2,    'NONE',  'KG'),
    ('Pin Press',                'BENCH', 'BARBELL', 'STANDARD',   'MEDIUM', 'MEDIUM', null, 'PINS',  null, 'NONE',  'KG'),
    ('Bench con Cadenas',        'BENCH', 'BARBELL', 'STANDARD',   'MEDIUM', 'MEDIUM', null, 'NONE',  null, 'CHAINS','KG'),
    ('Bench con Bandas',         'BENCH', 'BARBELL', 'STANDARD',   'MEDIUM', 'MEDIUM', null, 'NONE',  null, 'BANDS', 'KG');

------------------------------------------------------------
-- DEADLIFT
------------------------------------------------------------
insert into exercise (base_name, main_pattern, implement, setup, stance, grip, loading_unit) values
    ('Peso Muerto Convencional', 'DEADLIFT', 'BARBELL',     'STANDARD',    'CONVENTIONAL', 'MIXED',       'KG'),
    ('Peso Muerto Sumo',         'DEADLIFT', 'BARBELL',     'STANDARD',    'SUMO',         'MIXED',       'KG'),
    ('Trap Bar Deadlift',        'DEADLIFT', 'TRAP_BAR',    'STANDARD',    'CONVENTIONAL', 'NEUTRAL',     'KG'),
    ('Romanian Deadlift',        'DEADLIFT', 'BARBELL',     'STANDARD',    'CONVENTIONAL', 'PRONATED',    'KG'),
    ('Stiff Leg Deadlift',       'DEADLIFT', 'BARBELL',     'STANDARD',    'CONVENTIONAL', 'PRONATED',    'KG'),
    ('Snatch Grip Deadlift',     'DEADLIFT', 'BARBELL',     'SNATCH_GRIP', 'CONVENTIONAL', 'SNATCH',      'KG'),
    ('Good Morning',             'DEADLIFT', 'BARBELL',     'STANDARD',    'MEDIUM',       'PRONATED',    'KG'),
    ('Good Morning SSB',         'DEADLIFT', 'SAFETY_BAR',  'SSB',         'MEDIUM',       'NONE',        'KG'),
    ('Hip Thrust',               'DEADLIFT', 'BARBELL',     'STANDARD',    'MEDIUM',       'NONE',        'KG'),
    ('Kettlebell Swing',         'DEADLIFT', 'KETTLEBELL',  'STANDARD',    'MEDIUM',       'PRONATED',    'KG');

-- Variantes con height / loading method
insert into exercise (base_name, main_pattern, implement, setup, stance, grip, height_mode, height_length, loading_method, loading_unit) values
    ('Deficit Deadlift',         'DEADLIFT', 'BARBELL', 'STANDARD', 'CONVENTIONAL', 'MIXED', 'DEFICIT', 5,    'NONE',  'KG'),
    ('Block Pull',               'DEADLIFT', 'BARBELL', 'STANDARD', 'CONVENTIONAL', 'MIXED', 'BLOCK',   10,   'NONE',  'KG'),
    ('Rack Pull',                'DEADLIFT', 'BARBELL', 'STANDARD', 'CONVENTIONAL', 'MIXED', 'PINS',    null, 'NONE',  'KG'),
    ('Deadlift con Bandas',      'DEADLIFT', 'BARBELL', 'STANDARD', 'CONVENTIONAL', 'MIXED', 'NONE',    null, 'BANDS', 'KG'),
    ('Deadlift con Cadenas',     'DEADLIFT', 'BARBELL', 'STANDARD', 'CONVENTIONAL', 'MIXED', 'NONE',    null, 'CHAINS','KG');

------------------------------------------------------------
-- PRESS (overhead)
------------------------------------------------------------
insert into exercise (base_name, main_pattern, implement, setup, stance, grip, loading_unit) values
    ('Overhead Press',           'PRESS', 'BARBELL',   'STANDARD', 'MEDIUM', 'MEDIUM',  'KG'),
    ('Push Press',               'PRESS', 'BARBELL',   'STANDARD', 'MEDIUM', 'MEDIUM',  'KG'),
    ('Z Press',                  'PRESS', 'BARBELL',   'STANDARD', 'NONE',   'MEDIUM',  'KG'),
    ('Seated DB Shoulder Press', 'PRESS', 'DUMBBELL',  'STANDARD', 'NONE',   'NEUTRAL', 'KG'),
    ('Landmine Press',           'PRESS', 'LANDMINE',  'ONE_ARM',  'MEDIUM', 'NEUTRAL', 'KG'),
    ('Arnold Press',             'PRESS', 'DUMBBELL',  'STANDARD', 'NONE',   'NEUTRAL', 'KG');

------------------------------------------------------------
-- OLYMPIC LIFTS
------------------------------------------------------------
insert into exercise (base_name, main_pattern, implement, setup, stance, grip, loading_unit) values
    ('Power Clean',              'OLY', 'BARBELL', 'STANDARD', 'MEDIUM', 'CLEAN',  'KG'),
    ('Hang Clean',               'OLY', 'BARBELL', 'STANDARD', 'MEDIUM', 'CLEAN',  'KG'),
    ('Clean and Jerk',           'OLY', 'BARBELL', 'STANDARD', 'MEDIUM', 'CLEAN',  'KG'),
    ('Power Snatch',             'OLY', 'BARBELL', 'SNATCH_GRIP', 'MEDIUM', 'SNATCH', 'KG'),
    ('Hang Snatch',              'OLY', 'BARBELL', 'SNATCH_GRIP', 'MEDIUM', 'SNATCH', 'KG');

------------------------------------------------------------
-- ACCESSORY - Espalda / Tirones
------------------------------------------------------------
insert into exercise (base_name, main_pattern, implement, setup, stance, grip, loading_unit) values
    ('Dominadas Pronadas',       'ACCESSORY', 'PULLUP_BAR', 'STANDARD', 'NONE', 'PRONATED', 'KG'),
    ('Dominadas Supinadas',      'ACCESSORY', 'PULLUP_BAR', 'STANDARD', 'NONE', 'SUPINATED','KG'),
    ('Dominadas Neutras',        'ACCESSORY', 'PULLUP_BAR', 'STANDARD', 'NONE', 'NEUTRAL',  'KG'),
    ('Remo con Barra',           'ACCESSORY', 'BARBELL',    'STANDARD', 'MEDIUM','PRONATED','KG'),
    ('Pendlay Row',              'ACCESSORY', 'BARBELL',    'STANDARD', 'MEDIUM','PRONATED','KG'),
    ('Remo Mancuerna 1 Brazo',   'ACCESSORY', 'DUMBBELL',   'ONE_ARM',  'NONE', 'NEUTRAL',  'KG'),
    ('Remo en Polea Sentado',    'ACCESSORY', 'CABLE',      'STANDARD', 'NONE', 'NEUTRAL',  'KG'),
    ('Jalon al Pecho',           'ACCESSORY', 'CABLE',      'STANDARD', 'NONE', 'PRONATED', 'KG'),
    ('Face Pull',                'ACCESSORY', 'CABLE',      'STANDARD', 'NONE', 'PRONATED', 'KG'),
    ('Pullover Mancuerna',       'ACCESSORY', 'DUMBBELL',   'STANDARD', 'NONE', 'NEUTRAL',  'KG');

------------------------------------------------------------
-- ACCESSORY - Hombro / Pecho complementario
------------------------------------------------------------
insert into exercise (base_name, main_pattern, implement, setup, stance, grip, loading_unit) values
    ('Elevaciones Laterales',    'ACCESSORY', 'DUMBBELL', 'STANDARD', 'NONE', 'NEUTRAL', 'KG'),
    ('Pajaros (Rear Delt)',      'ACCESSORY', 'DUMBBELL', 'STANDARD', 'NONE', 'NEUTRAL', 'KG'),
    ('Elevacion Frontal Disco',  'ACCESSORY', 'PLATES',   'STANDARD', 'NONE', 'NEUTRAL', 'KG'),
    ('Aperturas con Mancuerna',  'ACCESSORY', 'DUMBBELL', 'STANDARD', 'NONE', 'NEUTRAL', 'KG'),
    ('Pec Deck',                 'ACCESSORY', 'MACHINE',  'STANDARD', 'NONE', 'NEUTRAL', 'KG'),
    ('Fondos en Paralelas',      'ACCESSORY', 'BODYWEIGHT','STANDARD','NONE', 'NONE',    'KG'),
    ('Fondos en Anillas',        'ACCESSORY', 'RINGS',    'STANDARD', 'NONE', 'NEUTRAL', 'KG');

------------------------------------------------------------
-- ACCESSORY - Brazos
------------------------------------------------------------
insert into exercise (base_name, main_pattern, implement, setup, stance, grip, loading_unit) values
    ('Curl con Barra',           'ACCESSORY', 'BARBELL',  'STANDARD', 'NONE', 'SUPINATED','KG'),
    ('Curl con Mancuernas',      'ACCESSORY', 'DUMBBELL', 'STANDARD', 'NONE', 'SUPINATED','KG'),
    ('Curl Martillo',            'ACCESSORY', 'DUMBBELL', 'STANDARD', 'NONE', 'NEUTRAL',  'KG'),
    ('Curl en Polea',            'ACCESSORY', 'CABLE',    'STANDARD', 'NONE', 'SUPINATED','KG'),
    ('Triceps Polea',            'ACCESSORY', 'CABLE',    'STANDARD', 'NONE', 'PRONATED', 'KG'),
    ('Press Frances',            'ACCESSORY', 'BARBELL',  'STANDARD', 'NONE', 'PRONATED', 'KG'),
    ('Extension Triceps Tras Nuca','ACCESSORY','DUMBBELL','STANDARD', 'NONE', 'NEUTRAL',  'KG');

------------------------------------------------------------
-- ACCESSORY - Pierna / Cadera / Gluteo
------------------------------------------------------------
insert into exercise (base_name, main_pattern, implement, setup, stance, grip, loading_unit) values
    ('Prensa de Piernas',        'ACCESSORY', 'MACHINE',  'STANDARD', 'MEDIUM','NONE',    'KG'),
    ('Extension de Cuadriceps',  'ACCESSORY', 'MACHINE',  'STANDARD', 'NONE',  'NONE',    'KG'),
    ('Curl Femoral Tumbado',     'ACCESSORY', 'MACHINE',  'STANDARD', 'NONE',  'NONE',    'KG'),
    ('Hip Thrust en Maquina',    'ACCESSORY', 'MACHINE',  'STANDARD', 'MEDIUM','NONE',    'KG'),
    ('Zancadas Caminando',       'ACCESSORY', 'DUMBBELL', 'STANDARD', 'NONE',  'NEUTRAL', 'KG'),
    ('Step Up',                  'ACCESSORY', 'DUMBBELL', 'STANDARD', 'NONE',  'NEUTRAL', 'KG'),
    ('Gemelo de Pie',            'ACCESSORY', 'MACHINE',  'STANDARD', 'NONE',  'NONE',    'KG'),
    ('Reverse Hyper',            'ACCESSORY', 'MACHINE',  'STANDARD', 'NONE',  'NONE',    'KG'),
    ('Back Extension',           'ACCESSORY', 'BODYWEIGHT','STANDARD','NONE',  'NONE',    'KG');

------------------------------------------------------------
-- ACCESSORY - Core / Acondicionamiento
------------------------------------------------------------
insert into exercise (base_name, main_pattern, implement, setup, stance, grip, loading_unit) values
    ('Plancha Frontal',          'ACCESSORY', 'BODYWEIGHT','STANDARD','NONE','NONE',    'UNKNOWN'),
    ('Rueda Abdominal',          'ACCESSORY', 'BODYWEIGHT','STANDARD','NONE','NEUTRAL', 'UNKNOWN'),
    ('Crunch en Polea',          'ACCESSORY', 'CABLE',     'STANDARD','NONE','NEUTRAL', 'KG'),
    ('Elevacion Piernas Colgado','ACCESSORY', 'PULLUP_BAR','STANDARD','NONE','PRONATED','UNKNOWN'),
    ('Paseo del Granjero',       'ACCESSORY', 'DUMBBELL',  'STANDARD','NONE','NEUTRAL', 'KG');
