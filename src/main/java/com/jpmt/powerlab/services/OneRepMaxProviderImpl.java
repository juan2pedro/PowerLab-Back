package com.jpmt.powerlab.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class OneRepMaxProviderImpl implements OneRepMaxProvider {

    private final Map<Long, Double> maxes;

    public OneRepMaxProviderImpl(
            @Value("#{${metrics.reference-maxes:{}}}") Map<Long, Double> maxes) {
        this.maxes = maxes;
    }

    @Override
    public Optional<Double> referenceMax(Long athleteId, Long exerciseId) {
        return Optional.ofNullable(maxes.get(exerciseId));
    }
}