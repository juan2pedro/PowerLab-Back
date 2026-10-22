package com.jpmt.powerlab.services;

import com.jpmt.powerlab.models.domain.Sex;

public interface SexProvider {
    Sex sex(Long athleteId);
}
