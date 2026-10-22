package com.jpmt.powerlab.models.dto.metrics;

import java.time.LocalDate;

public record TonnageMilestone(double thresholdKg, String label, boolean achieved, LocalDate achievedOn) {}
