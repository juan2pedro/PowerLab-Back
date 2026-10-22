package com.jpmt.powerlab.services;

import com.jpmt.powerlab.models.domain.MainPattern;

import java.util.Optional;

public interface CompLiftProvider {
    /**
     * Ejercicio configurado como variante de competición del atleta para este levantamiento
     * (p.ej. su sentadilla de competición puede ser low bar). Vacío si no hay uno configurado;
     * el llamador decide el fallback (p.ej. mejor marca dentro del mainPattern).
     */
    Optional<Long> compLiftExerciseId(Long athleteId, MainPattern lift);
}
