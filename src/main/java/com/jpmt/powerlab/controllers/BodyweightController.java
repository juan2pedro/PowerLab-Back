package com.jpmt.powerlab.controllers;

import com.jpmt.powerlab.models.dto.bodyweight.BodyweightRequest;
import com.jpmt.powerlab.models.dto.bodyweight.BodyweightResponse;
import com.jpmt.powerlab.services.BodyweightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bodyweight")
@Tag(name = "Peso corporal", description = "Registro y consulta del peso corporal del usuario (serie temporal).")
public class BodyweightController {

    private final BodyweightService bodyweightService;

    public BodyweightController(BodyweightService bodyweightService) {
        this.bodyweightService = bodyweightService;
    }

    @PostMapping
    @Operation(summary = "Registrar un pesaje", description = "Registra el peso del usuario actual. Si ya hay un pesaje ese dia, lo actualiza.")
    public ResponseEntity<BodyweightResponse> record(@RequestBody @Valid BodyweightRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bodyweightService.record(request));
    }

    @GetMapping
    @Operation(summary = "Historial de pesajes", description = "Lista los pesajes del usuario actual, mas reciente primero.")
    public List<BodyweightResponse> findMine() {
        return bodyweightService.findMine();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Borrar un pesaje")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bodyweightService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
