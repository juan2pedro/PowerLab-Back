package com.jpmt.powerlab.services;

import com.jpmt.powerlab.models.domain.MainPattern;
import com.jpmt.powerlab.models.dto.metrics.MetricsQuery;
import com.jpmt.powerlab.models.dto.metrics.MetricsResponse;
import com.jpmt.powerlab.models.dto.metrics.MetricsSummaryResponse;
import com.jpmt.powerlab.models.dto.metrics.PersonalRecordsResponse;

import java.time.LocalDate;
import java.util.List;

public interface MetricsService {

    /**
     * Serie temporal de métricas de entrenamiento agrupable por ejercicio/patrón y por periodo.
     */
    MetricsResponse series(MetricsQuery query);

    /**
     * Récords personales (mejor peso, mejor e1RM, mejores marcas por número de reps) por ejercicio.
     * Sin filtros, resuelve los tres levantamientos de competición del atleta.
     */
    PersonalRecordsResponse personalRecords(LocalDate from, LocalDate to,
                                             List<Long> exerciseIds, List<MainPattern> patterns);

    /**
     * Métricas lúdicas/sociales: total SBD y puntuaciones normalizadas (DOTS, IPF GL),
     * consistencia (rachas, adherencia) y tonelaje acumulado con hitos.
     */
    MetricsSummaryResponse summary(LocalDate adherenceFrom, LocalDate adherenceTo);
}
