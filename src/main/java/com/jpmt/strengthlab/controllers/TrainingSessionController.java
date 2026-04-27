package com.jpmt.strengthlab.controllers;

import com.jpmt.strengthlab.models.dto.ApiErrorResponse;
import com.jpmt.strengthlab.models.dto.trainingsessiontemplate.TrainingSessionTemplateDetailResponse;
import com.jpmt.strengthlab.models.dto.trainingsessiontemplate.TrainingSessionTemplateRequest;
import com.jpmt.strengthlab.models.dto.trainingsessiontemplate.TrainingSessionTemplateSummaryResponse;
import com.jpmt.strengthlab.models.dto.trainingsettemplate.TrainingSetTemplateRequest;
import com.jpmt.strengthlab.models.dto.trainingsettemplate.TrainingSetTemplateResponse;
import com.jpmt.strengthlab.models.dto.trainingsettemplate.TrainingSetTemplateUpdateRequest;
import com.jpmt.strengthlab.services.TrainingSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/training-sessions")
@Tag(name = "Training Sessions", description = "APIs para gestionar programaciones de sesiones de entrenamiento")
public class TrainingSessionController {

    private final TrainingSessionService service;

    public TrainingSessionController(TrainingSessionService service) {
        this.service = service;
    }

    // ============================================================================
    // TRAINING SESSION TEMPLATES - programaciones de sesiones de entrenamiento
    // ============================================================================

    @GetMapping
    @Operation(summary = "List training session templates", description = "Devuelve un resumen de todas las programaciones de sesiones")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainingSessionTemplateSummaryResponse.class)))
    })
    public ResponseEntity<List<TrainingSessionTemplateSummaryResponse>> findAllSession() {
        List<TrainingSessionTemplateSummaryResponse> result = service.findAllSession();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search")
    @Operation(summary = "Search session templates by block and week",
            description = "Busca programaciones de sesiones por nombre de bloque y número de semana")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainingSessionTemplateSummaryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parámetros de búsqueda inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<List<TrainingSessionTemplateSummaryResponse>> findAllSessionByBlockNameAndWeekNumber(
            @Parameter(description = "Nombre del bloque de entrenamiento", required = true) @RequestParam String blockName,
            @Parameter(description = "Número de semana (1-12)", required = true) @RequestParam Integer weekNumber) {
        List<TrainingSessionTemplateSummaryResponse> result = service.findAllSessionByBlockNameAndWeekNumber(blockName, weekNumber);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get session template detail by ID",
            description = "Obtiene el detalle completo de una plantilla de sesión incluyendo sus sets")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plantilla encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainingSessionTemplateDetailResponse.class))),
            @ApiResponse(responseCode = "404", description = "Plantilla no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<TrainingSessionTemplateDetailResponse> findSesionById(
            @Parameter(description = "ID de la plantilla de sesión") @PathVariable Long id) {
        TrainingSessionTemplateDetailResponse result = service.findSessionById(id);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @Operation(summary = "Create session template",
            description = "Crea una nueva plantilla de sesión de entrenamiento",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la plantilla a crear",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainingSessionTemplateRequest.class))
            ))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Plantilla creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainingSessionTemplateDetailResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<TrainingSessionTemplateDetailResponse> saveSession(
            @Valid @RequestBody TrainingSessionTemplateRequest session) {
        TrainingSessionTemplateDetailResponse savedSession = service.saveSession(session);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedSession.id())
                .toUri();
        return ResponseEntity.created(location).body(savedSession);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update session template",
            description = "Actualiza una plantilla de sesión existente",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos actualizados de la plantilla",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainingSessionTemplateRequest.class))
            ))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plantilla actualizada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainingSessionTemplateDetailResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Plantilla no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<TrainingSessionTemplateDetailResponse> updateSession(
            @Parameter(description = "ID de la plantilla") @PathVariable Long id,
            @Valid @RequestBody TrainingSessionTemplateRequest session) {
        TrainingSessionTemplateDetailResponse result = service.updateSession(id, session);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete session template",
            description = "Elimina una plantilla de sesión y todos sus sets asociados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Plantilla eliminada exitosamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Plantilla no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteSessionById(
            @Parameter(description = "ID de la plantilla") @PathVariable Long id) {
        service.deleteSessionById(id);
        return ResponseEntity.noContent().build();
    }

    // ============================================================================
    // TRAINING SET TEMPLATES - programaciones de sets dentro de sesiones
    // ============================================================================

    @GetMapping("/{sessionId}/sets")
    @Operation(summary = "List set templates for a session",
            description = "Obtiene todos los sets de una plantilla de sesión ordenados por displayOrder")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de sets obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainingSetTemplateResponse.class))),
            @ApiResponse(responseCode = "404", description = "Sesión no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<List<TrainingSetTemplateResponse>> findResumeSetById(
            @Parameter(description = "ID de la plantilla de sesión") @PathVariable Long sessionId) {
        List<TrainingSetTemplateResponse> response = service.findBySessionIdWithExercise(sessionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{sessionId}/sets/{setId}")
    @Operation(summary = "Get set template by ID",
            description = "Obtiene los detalles de un set de una plantilla de sesión")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Set encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainingSetTemplateResponse.class))),
            @ApiResponse(responseCode = "404", description = "Set o sesión no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<TrainingSetTemplateResponse> findSetById(
            @Parameter(description = "ID de la sesión (contexto)") @PathVariable Long sessionId,
            @Parameter(description = "ID de la plantilla de set") @PathVariable Long setId) {
        return ResponseEntity.ok(this.service.findSetById(setId));
    }

    @PostMapping("/{sessionId}/sets")
    @Operation(summary = "Add set template to session",
            description = "Añade un nuevo set a una plantilla de sesión existente",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del set a crear",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainingSetTemplateRequest.class))
            ))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Set creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainingSetTemplateResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Sesión no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<TrainingSetTemplateResponse> saveSet(
            @Parameter(description = "ID de la plantilla de sesión") @PathVariable Long sessionId,
            @Valid @RequestBody TrainingSetTemplateRequest set) {
        TrainingSetTemplateResponse savedSet = this.service.saveSet(sessionId, set);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedSet.id())
                .toUri();
        return ResponseEntity.created(location).body(savedSet);
    }

    @PutMapping("/{sessionId}/sets/{setId}")
    @Operation(summary = "Update set template",
            description = "Actualiza un set de una plantilla de sesión",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos actualizados del set",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainingSetTemplateUpdateRequest.class))
            ))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Set actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainingSetTemplateResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Set o sesión no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<TrainingSetTemplateResponse> updateSet(
            @Parameter(description = "ID de la sesión (contexto)") @PathVariable Long sessionId,
            @Parameter(description = "ID de la plantilla de set") @PathVariable Long setId,
            @Valid @RequestBody TrainingSetTemplateUpdateRequest set) {
        return ResponseEntity.ok(this.service.updateSet(setId, set));
    }

    @DeleteMapping("/{sessionId}/sets/{setId}")
    @Operation(summary = "Delete set template",
            description = "Elimina un set de una plantilla de sesión")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Set eliminado exitosamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Set o sesión no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteSetById(
            @Parameter(description = "ID de la sesión (contexto)") @PathVariable Long sessionId,
            @Parameter(description = "ID de la plantilla de set") @PathVariable Long setId) {
        this.service.deleteSetById(setId);
        return ResponseEntity.noContent().build();
    }
}
