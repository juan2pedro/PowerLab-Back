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
     * Los intentos fallados (RPE &gt; 10) no cuentan como récord: se devuelven aparte.
     * Sin filtros, resuelve los tres levantamientos de competición del atleta.
     */
    PersonalRecordsResponse personalRecords(LocalDate from, LocalDate to,
                                             List<Long> exerciseIds, List<MainPattern> patterns);

    /**
     * Métricas lúdicas/sociales: total SBD y puntuaciones normalizadas (DOTS, IPF GL),
     * consistencia (rachas, adherencia) y tonelaje acumulado con hitos.
     * El total real suma los RM reales (mejor single conseguido) de los tres movimientos, y es
     * null mientras falte alguno; DOTS e IPF GL lo siguen.
     */
    MetricsSummaryResponse summary(LocalDate adherenceFrom, LocalDate adherenceTo);
}
