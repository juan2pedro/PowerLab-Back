package com.jpmt.powerlab.services;

import com.jpmt.powerlab.repositories.BodyweightRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class BodyweightProviderImpl implements BodyweightProvider {

    /** Fallback cuando el atleta aun no ha registrado ningun pesaje. */
    @Value("${bodyweight.default:80.0}")
    private Double defaultBodyweight;

    private final BodyweightRepository bodyweightRepository;

    public BodyweightProviderImpl(BodyweightRepository bodyweightRepository) {
        this.bodyweightRepository = bodyweightRepository;
    }

    /**
     * Peso vigente del atleta en la fecha dada: ultimo pesaje con date <= date (carry-forward).
     * Si el atleta no tiene ningun pesaje registrado, cae al valor por defecto de config.
     */
    @Override
    public Double bodyweightKg(Long athleteId, LocalDate date) {
        return bodyweightRepository
                .findTopByUser_IdAndDateLessThanEqualOrderByDateDesc(athleteId, date)
                .map(bw -> bw.getWeight())
                .filter(w -> w != null && w > 0)
                .orElse(defaultBodyweight);
    }
}
