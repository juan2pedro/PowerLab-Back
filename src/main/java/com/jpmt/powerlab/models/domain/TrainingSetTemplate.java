package com.jpmt.powerlab.models.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity
@Table(name = "training_set_template")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class TrainingSetTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(1)
    @Max(5)
    @Column(name = "target_sets")
    private Integer targetSets;

    @Min(1)
    @Max(50)
    @Column(name = "target_reps")
    private Integer targetReps;

    @Min(1)
    @Max(1000)
    @Column(name = "target_weight")
    private Double targetWeight;

    @Column(name = "target_intensity")
    private Integer targetIntensity;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_intensity_type")
    private IntensityType targetIntensityType;

    @Column(name = "rest")
    private String rest;

    @Column(name = "notes", length = 500)
    private String notes;

    @Min(1)
    @Max(10)
    @Column(name = "display_order")
    private Integer displayOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_template_id")
    private TrainingSessionTemplate sessionTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

}
