package com.jpmt.powerlab.controllers;

import com.jpmt.powerlab.models.domain.MainPattern;
import com.jpmt.powerlab.models.dto.ApiErrorResponse;
import com.jpmt.powerlab.models.dto.metrics.*;
import com.jpmt.powerlab.services.MetricsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/metrics")
@Tag(name = "Metricas", description = "Endpoints para obtener métricas de rendimiento, progreso y estadísticas de entrenamiento.")
public class MetricsController {

    private final MetricsService metricsService;

    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @GetMapping
    @Operation(
            summary = "Serie temporal de métricas",
            description = "Serie de métricas de entrenamiento (tonelaje, e1RM, intensidad, hard sets...) agrupable por " +
                    "ejercicio o patrón y bucketizada por periodo. Por defecto: últimas 12 semanas, agrupado por ejercicio, " +
                    "periodo semanal, solo sets de trabajo."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Serie de métricas obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MetricsResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parámetros inválidos (rango de fechas, filtros excluyentes)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<MetricsResponse> getMetrics(
            @Parameter(description = "Fecha inicio en formato ISO 8601 (YYYY-MM-DD). Por defecto: hace 12 semanas")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Fecha fin en formato ISO 8601 (YYYY-MM-DD). Por defecto: hoy")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @Parameter(description = "Bucketización temporal. Por defecto: WEEK")
            @RequestParam(required = false) MetricsPeriod period,
            @Parameter(description = "Agrupación de las series. Por defecto: EXERCISE")
            @RequestParam(required = false) MetricsGroupBy groupBy,
            @Parameter(description = "Filtro de sets. Por defecto: WORKING_SETS")
            @RequestParam(required = false) SetFilter setFilter,
            @Parameter(description = "Filtra por IDs de ejercicio (excluyente con patterns)")
            @RequestParam(required = false) List<Long> exerciseIds,
            @Parameter(description = "Filtra por patrones principales (excluyente con exerciseIds)")
            @RequestParam(required = false) List<MainPattern> patterns) {

        MetricsQuery query = MetricsQuery.normalized(from, to, period, groupBy, setFilter, exerciseIds, patterns);
        return ResponseEntity.ok(metricsService.series(query));
    }

    @GetMapping("/prs")
    @Operation(
            summary = "Récords personales",
            description = "Mejor peso, mejor e1RM y mejores marcas por número de repeticiones (1-10) por ejercicio. " +
                    "Los intentos fallados (RPE > 10) no cuentan como récord: se devuelven aparte en " +
                    "bestFailedSingle. Sin filtros, resuelve los tres levantamientos de competición del atleta."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Récords personales obtenidos exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PersonalRecordsResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parámetros inválidos (rango de fechas, filtros excluyentes)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<PersonalRecordsResponse> getPersonalRecords(
            @Parameter(description = "Fecha inicio en formato ISO 8601 (YYYY-MM-DD). Por defecto: histórico completo")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Fecha fin en formato ISO 8601 (YYYY-MM-DD). Por defecto: hoy")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @Parameter(description = "Filtra por IDs de ejercicio (excluyente con patterns)")
            @RequestParam(required = false) List<Long> exerciseIds,
            @Parameter(description = "Filtra por patrones principales (excluyente con exerciseIds)")
            @RequestParam(required = false) List<MainPattern> patterns) {

        return ResponseEntity.ok(metricsService.personalRecords(from, to, exerciseIds, patterns));
    }

    @GetMapping("/summary")
    @Operation(
            summary = "Resumen lúdico/social",
            description = "Total SBD y puntuaciones normalizadas (DOTS, IPF GL), consistencia (rachas, adherencia) " +
                    "y tonelaje acumulado con hitos. El total real suma los RM reales (mejor single conseguido) " +
                    "de los tres movimientos y es null mientras falte alguno; DOTS e IPF GL lo siguen. " +
                    "La ventana de adherencia por defecto son los últimos 28 días."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Resumen obtenido exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MetricsSummaryResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Rango de fechas inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<MetricsSummaryResponse> getSummary(
            @Parameter(description = "Fecha inicio de la ventana de adherencia. Por defecto: hace 28 días")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate adherenceFrom,
            @Parameter(description = "Fecha fin de la ventana de adherencia. Por defecto: hoy")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate adherenceTo) {

        return ResponseEntity.ok(metricsService.summary(adherenceFrom, adherenceTo));
    }
}
