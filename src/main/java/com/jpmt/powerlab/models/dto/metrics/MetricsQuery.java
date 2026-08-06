package com.jpmt.powerlab.models.dto.metrics;

import com.jpmt.powerlab.exceptions.BadRequestException;
import com.jpmt.powerlab.models.domain.MainPattern;

import java.time.LocalDate;
import java.util.List;

public record MetricsQuery(
        Long athleteId,
        LocalDate from,
        LocalDate to,
        MetricsPeriod period,
        MetricsGroupBy groupBy,
        SetFilter setFilter,
        List<Long> exerciseIds,
        List<MainPattern> patterns
) {
    public static final long DEFAULT_ATHLETE_ID = 1L;

    public static MetricsQuery normalized(LocalDate from, LocalDate to, MetricsPeriod period,
                                           MetricsGroupBy groupBy, SetFilter setFilter,
                                           List<Long> exerciseIds, List<MainPattern> patterns) {
        boolean hasExerciseIds = exerciseIds != null && !exerciseIds.isEmpty();
        boolean hasPatterns = patterns != null && !patterns.isEmpty();
        if (hasExerciseIds && hasPatterns) {
            throw new BadRequestException("Filtra por exerciseIds o por patterns, no por ambos a la vez.");
        }

        LocalDate resolvedTo = to != null ? to : LocalDate.now();
        LocalDate resolvedFrom = from != null ? from : resolvedTo.minusWeeks(12);
        if (resolvedFrom.isAfter(resolvedTo)) {
            throw new BadRequestException("El parámetro 'from' no puede ser posterior a 'to'.");
        }

        return new MetricsQuery(
                DEFAULT_ATHLETE_ID,
                resolvedFrom,
                resolvedTo,
                period != null ? period : MetricsPeriod.WEEK,
                groupBy != null ? groupBy : MetricsGroupBy.EXERCISE,
                setFilter != null ? setFilter : SetFilter.WORKING_SETS,
                hasExerciseIds ? exerciseIds : null,
                hasPatterns ? patterns : null
        );
    }
}
