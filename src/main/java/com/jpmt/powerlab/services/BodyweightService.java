package com.jpmt.powerlab.services;

import com.jpmt.powerlab.models.dto.bodyweight.BodyweightRequest;
import com.jpmt.powerlab.models.dto.bodyweight.BodyweightResponse;

import java.util.List;

public interface BodyweightService {

    /** Registra (o actualiza si ya hay pesaje ese dia) el peso del usuario actual. */
    BodyweightResponse record(BodyweightRequest request);

    /** Historial de pesajes del usuario actual, mas reciente primero. */
    List<BodyweightResponse> findMine();

    void deleteById(Long id);
}
