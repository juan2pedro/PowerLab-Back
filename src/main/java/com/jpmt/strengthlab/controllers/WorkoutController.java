package com.jpmt.strengthlab.controllers;

import com.jpmt.strengthlab.models.dto.ApiErrorResponse;
import com.jpmt.strengthlab.models.dto.workoutentry.WorkoutEntryCreateRequest;
import com.jpmt.strengthlab.models.dto.workoutentry.WorkoutEntryResponse;
import com.jpmt.strengthlab.models.dto.workoutentry.WorkoutEntryUpdateRequest;
import com.jpmt.strengthlab.models.dto.workoutsession.WorkoutDayResponse;
import com.jpmt.strengthlab.models.dto.workoutsession.WorkoutSessionDetailResponse;
import com.jpmt.strengthlab.models.dto.workoutsession.WorkoutSessionRequest;
import com.jpmt.strengthlab.models.dto.workoutsession.WorkoutSessionSummaryResponse;
import com.jpmt.strengthlab.models.dto.workoutset.WorkoutSetRequest;
import com.jpmt.strengthlab.models.dto.workoutset.WorkoutSetResponse;
import com.jpmt.strengthlab.services.WorkoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/workouts")
@Tag(name = "Workouts", description = "APIs para gestionar sesiones de entrenamiento, entradas y series")
public class WorkoutController {

    private final WorkoutService workoutService;
    private static final Logger logger = LoggerFactory.getLogger(WorkoutController.class);

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    // ============================================================================
    // WORKOUT SESSIONS - Sesiones de entrenamiento (GET, POST, DELETE)
    // ============================================================================

    @GetMapping("/range")
    @Operation(
            summary = "List sessions by date range",
            description = "Obtiene un listado de sesiones en un rango de fechas (por defecto últimos 30 días). Fechas en formato ISO 8601 (YYYY-MM-DD)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de sesiones obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WorkoutSessionSummaryResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Rango de fechas inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<List<WorkoutSessionSummaryResponse>> getWorkoutSessionsByRange(
            @Parameter(description = "Fecha inicio en formato ISO 8601 (YYYY-MM-DD). Por defecto: hace 30 días")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Fecha fin en formato ISO 8601 (YYYY-MM-DD). Por defecto: hoy")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        if (from == null) from = LocalDate.now().minusDays(30);
        if (to == null) to = LocalDate.now();

        List<WorkoutSessionSummaryResponse> sessions = workoutService.findAllWorkoutSessionsByDateRange(from, to);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/day")
    @Operation(
            summary = "Get full workout by date",
            description = "Obtiene la sesión de entrenamiento completa con todas sus entradas y series para una fecha específica"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sesión del día encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WorkoutDayResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe sesión para la fecha especificada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<WorkoutDayResponse> getFullWorkoutByDate(
            @Parameter(description = "Fecha en formato ISO 8601 (YYYY-MM-DD). Por defecto: hoy")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null) date = LocalDate.now();
        WorkoutDayResponse day = workoutService.findFullDayByDate(date);
        return ResponseEntity.ok(day);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get workout session by ID",
            description = "Obtiene el detalle completo de una sesión de entrenamiento incluyendo sus entradas y series"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sesión encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WorkoutSessionDetailResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sesión no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<WorkoutSessionDetailResponse> getWorkoutById(
            @Parameter(description = "ID de la sesión") @PathVariable Long id) {
        WorkoutSessionDetailResponse session = workoutService.findWorkoutById(id);
        return ResponseEntity.ok(session);
    }

    @PostMapping
    @Operation(
            summary = "Create free workout session",
            description = "Crea una nueva sesión de entrenamiento libre (sin plantilla). Usar /template/{templateId} para crear desde plantilla",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la sesión a crear",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WorkoutSessionRequest.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Sesión creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WorkoutSessionDetailResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud inválida (validación fallida)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<WorkoutSessionDetailResponse> createWorkoutSession(
            @Valid @RequestBody WorkoutSessionRequest session) {
        WorkoutSessionDetailResponse createdSession = workoutService.saveWorkoutSession(session);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdSession.id())
                .toUri();
        return ResponseEntity.created(location).body(createdSession);
    }

    @PostMapping("/template/{templateId}")
    @Operation(
            summary = "Create workout session from template",
            description = "Crea una nueva sesión de entrenamiento basada en una plantilla de programación existente. Hereda ejercicios y sets de la plantilla",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la sesión (fecha, notas, etc.)",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WorkoutSessionRequest.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Sesión creada exitosamente desde la plantilla",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WorkoutSessionDetailResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud inválida (validación fallida)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Plantilla de programación no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<WorkoutSessionDetailResponse> createWorkoutFromTemplate(
            @Parameter(description = "ID de la plantilla de programación") @PathVariable Long templateId,
            @Valid @RequestBody WorkoutSessionRequest session) {
        WorkoutSessionDetailResponse createdSession = workoutService.createWorkoutFromTemplate(templateId, session);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdSession.id())
                .toUri();
        return ResponseEntity.created(location).body(createdSession);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete workout session",
            description = "Elimina una sesión de entrenamiento y todas sus entradas y series asociadas"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Sesión eliminada exitosamente",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sesión no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<Void> deleteWorkoutById(
            @Parameter(description = "ID de la sesión") @PathVariable Long id) {
        workoutService.deleteWorkoutById(id);
        return ResponseEntity.noContent().build();
    }

    // ============================================================================
    // WORKOUT ENTRIES - Entradas de ejercicios dentro de sesiones (POST, PUT, DELETE)
    // ============================================================================

    @PostMapping("/entries")
    @Operation(
            summary = "Create workout entry (exercise)",
            description = "Añade una nueva entrada (ejercicio) a una sesión de entrenamiento existente",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la entrada (ejercicio, sesión, etc.)",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WorkoutEntryCreateRequest.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Entrada creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WorkoutEntryResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud inválida (validación fallida)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sesión o ejercicio no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<WorkoutEntryResponse> addWorkoutEntry(
            @Valid @RequestBody WorkoutEntryCreateRequest entry) {
        WorkoutEntryResponse createdEntry = workoutService.saveWorkoutEntry(entry);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdEntry.id())
                .toUri();
        return ResponseEntity.created(location).body(createdEntry);
    }

    @PutMapping("/entries/{id}")
    @Operation(
            summary = "Update workout entry",
            description = "Actualiza una entrada existente (modificar datos del ejercicio registrado)",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos actualizados de la entrada",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WorkoutEntryUpdateRequest.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Entrada actualizada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WorkoutEntryResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud inválida (validación fallida)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Entrada no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<WorkoutEntryResponse> updateWorkoutEntry(
            @Parameter(description = "ID de la entrada") @PathVariable Long id,
            @Valid @RequestBody WorkoutEntryUpdateRequest entry) {
        WorkoutEntryResponse updatedEntry = workoutService.updateEntry(id, entry);
        return ResponseEntity.ok(updatedEntry);
    }

    @DeleteMapping("/entries/{id}")
    @Operation(
            summary = "Delete workout entry",
            description = "Elimina una entrada de ejercicio y todas sus series asociadas"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Entrada eliminada exitosamente",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Entrada no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<Void> deleteWorkoutEntryById(
            @Parameter(description = "ID de la entrada") @PathVariable Long id) {
        workoutService.deleteWorkoutEntryById(id);
        return ResponseEntity.noContent().build();
    }

    // ============================================================================
    // WORKOUT SETS - Series dentro de entradas (GET, POST, PUT, DELETE)
    // ============================================================================

    @GetMapping("/{workoutId}/entries/{entryId}/sets")
    @Operation(
            summary = "List sets for an entry",
            description = "Obtiene todas las series registradas para una entrada de ejercicio específica"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de series obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WorkoutSetResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Entrada o sesión no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<List<WorkoutSetResponse>> getWorkoutSetsByEntryId(
            @Parameter(description = "ID de la sesión (contexto)") @PathVariable Long workoutId,
            @Parameter(description = "ID de la entrada") @PathVariable Long entryId) {
        List<WorkoutSetResponse> sets = workoutService.findAllWorkoutSets(entryId);
        return ResponseEntity.ok(sets);
    }

    @PostMapping("/{workoutId}/entries/{entryId}/sets")
    @Operation(
            summary = "Create workout set",
            description = "Añade una nueva serie (set) a una entrada de ejercicio existente con peso, repeticiones, etc.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del set a crear (peso, reps, RPE, notas, etc.)",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WorkoutSetRequest.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Serie creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WorkoutSetResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud inválida (validación fallida)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Entrada o sesión no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<WorkoutSetResponse> addWorkoutSet(
            @Parameter(description = "ID de la sesión (contexto)") @PathVariable Long workoutId,
            @Parameter(description = "ID de la entrada") @PathVariable Long entryId,
            @Valid @RequestBody WorkoutSetRequest workoutSet) {
        WorkoutSetResponse createdSet = workoutService.saveWorkoutSet(entryId, workoutSet);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdSet.id())
                .toUri();
        return ResponseEntity.created(location).body(createdSet);
    }

    @PutMapping("/{workoutId}/entries/{entryId}/sets/{setId}")
    @Operation(
            summary = "Update workout set",
            description = "Actualiza una serie existente (modificar peso, repeticiones, RPE, notas, etc.)",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos actualizados del set",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WorkoutSetRequest.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Serie actualizada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WorkoutSetResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud inválida (validación fallida)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Serie, entrada o sesión no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<WorkoutSetResponse> updateWorkoutSet(
            @Parameter(description = "ID de la sesión (contexto)") @PathVariable Long workoutId,
            @Parameter(description = "ID de la entrada (contexto)") @PathVariable Long entryId,
            @Parameter(description = "ID de la serie") @PathVariable Long setId,
            @Valid @RequestBody WorkoutSetRequest workoutSet) {
        WorkoutSetResponse updatedSet = workoutService.updateWorkoutSet(setId, workoutSet);
        return ResponseEntity.ok(updatedSet);
    }

    @DeleteMapping("/{workoutId}/entries/{entryId}/sets/{setId}")
    @Operation(
            summary = "Delete workout set",
            description = "Elimina una serie específica de una entrada de ejercicio"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Serie eliminada exitosamente",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Serie, entrada o sesión no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<Void> deleteWorkoutSetById(
            @Parameter(description = "ID de la sesión (contexto)") @PathVariable Long workoutId,
            @Parameter(description = "ID de la entrada (contexto)") @PathVariable Long entryId,
            @Parameter(description = "ID de la serie") @PathVariable Long setId) {
        logger.info("Deleting workout set id={} for workoutId={} entryId={}", setId, workoutId, entryId);
        workoutService.deleteWorkoutSetById(setId);
        return ResponseEntity.noContent().build();
    }
}
