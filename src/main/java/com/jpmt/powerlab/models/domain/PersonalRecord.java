package com.jpmt.powerlab.models.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "personal_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(name = "achieved_on", nullable = false)
    private LocalDate date;

    @Column(name = "value_kg", nullable = false)
    private Double weight;              // peso REAL levantado, sin fórmula

    @Column(name = "bodyweight_kg")
    private Double bodyweightKg;         // snapshot opcional

    @Enumerated(EnumType.STRING)
    @Column(name = "context", nullable = false, length = 20)
    private PrContext context;           // COMPETITION | TRAINING_BLOCK | FREE_TRAINING | DECLARED

    @Column(name = "context_label")
    private String contextLabel;         // snapshot: "Bloque Fuerza S3", nombre del meet...

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_set_id")  // null = externo (competi/declarado), autoridad propia
    private WorkoutSet sourceSet;

    @Column(name = "note")
    private String note;
}
