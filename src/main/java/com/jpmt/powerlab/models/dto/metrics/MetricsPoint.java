package com.jpmt.powerlab.models.dto.metrics;

public record MetricsPoint(
        String period,           // "2026-W23" | "2026-06-15" | "2026-06" | null
        int sets,
        int quantity,            // Σ reps/seg/m/pasos
        Double tonnage,          // null si no aplica (TIME, loadMode=NONE)
        Double plannedTonnage,   // null si la sesión no tiene plantilla o la prescripción es por RPE/RIR
        Double avgLoad,
        Double peakLoad,
        int hardSets,
        int failedSets,
        Double bestE1rm,         // null si banda o no-REPS
        Double avgE1rm,
        boolean e1rmEstimated,   // banda presente y e1RM calculado igualmente
        Double avgRpe,
        Double avgIntensityPct   // IMR ponderado por reps vs 1RM del levantamiento de competición
) {}