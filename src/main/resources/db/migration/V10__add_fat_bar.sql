-- V10__add_fat_bar_implement.sql
-- Añade FAT_BAR al catalogo de implementos. Un CHECK no se puede extender:
-- hay que recrearlo con la lista completa.

ALTER TABLE exercise DROP CONSTRAINT IF EXISTS exercise_implement_check;

ALTER TABLE exercise ADD CONSTRAINT exercise_implement_check
    CHECK (implement IN ('BARBELL','DUMBBELL','KETTLEBELL','TRAP_BAR','SAFETY_BAR','CAMBERED_BAR','SWISS_BAR','PLATES',
                         'CABLE','MACHINE','SMITH','RINGS','BODY_WEIGHT','LANDMINE','PULLUP_BAR','BAND',
                         'LEVERAGE_MACHINE','WEIGHTED','EX_BARBELL','FAT_BAR','OTHER', 'SPIDER_BAR'));