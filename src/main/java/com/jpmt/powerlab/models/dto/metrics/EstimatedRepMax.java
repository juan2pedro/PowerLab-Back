package com.jpmt.powerlab.models.dto.metrics;

/**
 * RM estimado por reps a partir del mejor e1RM, invirtiendo la fórmula configurada en el backend.
 * El front lo usa directamente para no tener que replicar la fórmula ni arriesgar desincronizarse.
 */
public record EstimatedRepMax(int reps, double weight) {}
