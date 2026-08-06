package com.jpmt.powerlab.services;

import com.jpmt.powerlab.models.domain.MainPattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class CompLiftProviderImpl implements CompLiftProvider {

    private final Map<String, Long> compLifts;

    public CompLiftProviderImpl(@Value("#{${metrics.comp-lifts:{}}}") Map<String, Long> compLifts) {
        this.compLifts = compLifts;
    }

    @Override
    public Optional<Long> compLiftExerciseId(Long athleteId, MainPattern lift) {
        return Optional.ofNullable(compLifts.get(lift.name()));
    }
}
