package com.jpmt.powerlab.models.dto.metrics;

public record ConsistencySummary(
        int currentWeekStreak,
        int longestWeekStreak,
        double avgSessionsPerWeek,
        int plannedSessions,
        int completedSessions,
        int partialSessions,
        Double adherencePct
) {}
