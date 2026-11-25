package com.jpmt.powerlab.services;

public interface AuthorizationService {

    /**
     * Resuelve el atleta cuyos datos se van a leer.
     * - Si {@code requestedAthleteId} es null o es el propio usuario -> el usuario actual.
     * - Si es otro usuario -> solo si el actual es su coach ACTIVO; si no, 403.
     */
    Long resolveReadableAthlete(Long requestedAthleteId);
}
