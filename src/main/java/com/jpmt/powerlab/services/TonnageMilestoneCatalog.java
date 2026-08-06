package com.jpmt.powerlab.services;

import java.util.List;

public final class TonnageMilestoneCatalog {

    public record Entry(double thresholdKg, String label) {}

    public static final List<Entry> MILESTONES = List.of(
            new Entry(10_000, "Un autobús urbano (10 t)"),
            new Entry(25_000, "Una excavadora (25 t)"),
            new Entry(55_000, "Un tanque Leopard 2 (55 t)"),
            new Entry(150_000, "Una ballena azul adulta (150 t)"),
            new Entry(400_000, "Un Boeing 747 al despegue (400 t)"),
            new Entry(575_000, "Un Airbus A380 al despegue (575 t)"),
            new Entry(1_000_000, "1.000 toneladas: los cables del Golden Gate"),
            new Entry(10_100_000, "La Torre Eiffel completa (10.100 t)")
    );

    private TonnageMilestoneCatalog() {}
}
