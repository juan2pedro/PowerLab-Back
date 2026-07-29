package com.jpmt.powerlab.services;

import java.util.Optional;

public interface OneRepMaxProvider {
    Optional <Double> referenceMax(Long athleteId, Long exerciseId);
}
