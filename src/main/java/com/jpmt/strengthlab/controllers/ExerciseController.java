package com.jpmt.strengthlab.controllers;

import com.jpmt.strengthlab.models.domain.MainPattern;
import com.jpmt.strengthlab.models.dto.ApiErrorResponse;
import com.jpmt.strengthlab.models.dto.exercise.ExerciseRequest;
import com.jpmt.strengthlab.models.dto.exercise.ExerciseResponse;
import com.jpmt.strengthlab.services.ExerciseService;
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
@RequestMapping("/api/exercises")
@Tag(name = "Exercises", description = "APIs para gestionar ejercicios")
public class ExerciseController {

    private final ExerciseService service;

    public ExerciseController(ExerciseService service) {
        this.service = service;
    }

    @GetMapping()
    @Operation(summary = "List exercises", description = "Devuelve la lista completa de ejercicios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExerciseResponse.class)))
    })
    public ResponseEntity<List<ExerciseResponse>> findAllExercises() {
        return ResponseEntity.ok(this.service.findAll());
    }

    @GetMapping("/search/{mainPattern}")
    @Operation(summary = "Search exercises by main pattern")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExerciseResponse.class)))
    })
    public ResponseEntity<List<ExerciseResponse>> findAllExercisesByMainPattern(
            @Parameter(description = "Main pattern filter") @PathVariable MainPattern mainPattern) {
        return ResponseEntity.ok(this.service.findAllByMainPattern(mainPattern));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get exercise by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exercise found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExerciseResponse.class))),
            @ApiResponse(responseCode = "404", description = "Exercise not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<ExerciseResponse> findById(@Parameter(description = "ID del ejercicio") @PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create exercise",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Exercise payload",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExerciseRequest.class))
            ))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Exercise created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExerciseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<ExerciseResponse> save(@Valid @RequestBody ExerciseRequest exercise) {
        ExerciseResponse savedExercise = this.service.save(exercise);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedExercise.id())
                .toUri();
        return ResponseEntity.created(location).body(savedExercise);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update exercise",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Exercise payload",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExerciseRequest.class))
            ))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exercise updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExerciseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Exercise not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<ExerciseResponse> update(@Parameter(description = "ID del ejercicio") @PathVariable Long id,
                                                   @Valid @RequestBody ExerciseRequest exercise) {
        return ResponseEntity.ok(this.service.update(id, exercise));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete exercise")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Exercise deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Exercise not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteById(@Parameter(description = "ID del ejercicio") @PathVariable Long id) {
        this.service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
