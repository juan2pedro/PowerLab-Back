package com.jpmt.powerlab.services;

import com.jpmt.powerlab.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class BodyweightProviderImpl implements BodyweightProvider {
    @Value("${bodyweight.default:80.0}")
    private Double bodyweight;

    @Override
    public Double bodyweightKg(Long athleteId, LocalDate date) {
        Double bw = bodyweight;
        if (bw == null || bw <= 0) {
            throw new ResourceNotFoundException("Bodyweight not found for athlete " + athleteId + " on " + date);
        }
        return bw;
    }
}
