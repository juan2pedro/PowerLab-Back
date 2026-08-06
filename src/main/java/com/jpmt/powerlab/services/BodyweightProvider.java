package com.jpmt.powerlab.services;

import java.time.LocalDate;

public interface BodyweightProvider {
    Double bodyweightKg(Long athleteId, LocalDate date);

}
