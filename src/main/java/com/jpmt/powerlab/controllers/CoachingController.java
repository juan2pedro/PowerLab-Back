package com.jpmt.powerlab.controllers;

import com.jpmt.powerlab.models.domain.CoachingStatus;
import com.jpmt.powerlab.models.dto.coaching.CoachingInviteRequest;
import com.jpmt.powerlab.models.dto.coaching.CoachingRelationshipResponse;
import com.jpmt.powerlab.services.CoachingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coaching")
@Tag(name = "Coaching", description = "Relaciones coach-atleta: invitaciones, aceptacion y consulta de cartera.")
public class CoachingController {

    private final CoachingService coachingService;

    public CoachingController(CoachingService coachingService) {
        this.coachingService = coachingService;
    }

    @PostMapping("/invitations")
    @PreAuthorize("hasRole('COACH')")
    @Operation(summary = "Invitar a un atleta (coach)", description = "El coach actual invita a un atleta por email. Crea la relacion en PENDING.")
    public ResponseEntity<CoachingRelationshipResponse> invite(@RequestBody @Valid CoachingInviteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(coachingService.invite(request));
    }

    @GetMapping("/invitations")
    @Operation(summary = "Listar invitaciones/relaciones del usuario actual",
            description = "Por defecto, las recibidas como atleta. direction=sent devuelve las enviadas como coach. " +
                    "status filtra por estado (PENDING/ACTIVE/REVOKED/DECLINED); omitirlo devuelve todas.")
    public List<CoachingRelationshipResponse> invitations(
            @RequestParam(required = false, defaultValue = "received") String direction,
            @RequestParam(required = false) CoachingStatus status) {
        return coachingService.invitations("sent".equalsIgnoreCase(direction), status);
    }

    @PostMapping("/invitations/{id}/accept")
    @Operation(summary = "Aceptar invitacion (atleta)")
    public CoachingRelationshipResponse accept(@PathVariable Long id) {
        return coachingService.accept(id);
    }

    @PostMapping("/invitations/{id}/decline")
    @Operation(summary = "Rechazar invitacion (atleta)")
    public CoachingRelationshipResponse decline(@PathVariable Long id) {
        return coachingService.decline(id);
    }

    @PostMapping("/relationships/{id}/revoke")
    @Operation(summary = "Revocar relacion activa (coach o atleta)")
    public CoachingRelationshipResponse revoke(@PathVariable Long id) {
        return coachingService.revoke(id);
    }

    @GetMapping("/athletes")
    @PreAuthorize("hasRole('COACH')")
    @Operation(summary = "Mis atletas activos (coach)")
    public List<CoachingRelationshipResponse> myAthletes() {
        return coachingService.myAthletes();
    }

    @GetMapping("/coach")
    @Operation(summary = "Mi coach activo (atleta)")
    public CoachingRelationshipResponse myCoach() {
        return coachingService.myCoach();
    }
}
