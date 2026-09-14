package com.jpmt.powerlab.models.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.EnumSet;
import java.util.Set;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "slot_code", length = 10)
    private SlotCode slotCode;

    @Min(1)
    @Max(20)
    @Column(name = "target_sets")
    private Integer targetSets;

    @Min(1)
    @Column(name = "target_reps_min")
    private Integer targetRepsMin;

    @Min(1)
    @Column(name = "target_reps_max")
    private Integer targetRepsMax;

    @Column(name = "amrap", nullable = false)
    @Builder.Default
    private Boolean amrap = false;

    @Min(1)
    @Max(1000)
    @Column(name = "target_weight")
    private Double targetWeight;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_intensity_type")
    private IntensityType targetIntensityType;

    @Enumerated(EnumType.STRING)
    @Column(name = "reference_slot_code", length = 10)
    private SlotCode referenceSlotCode;

    @Min(0)
    @Max(100)
    @Column(name = "target_intensity_min")
    private Double targetIntensityMin;

    @Min(1)
    @Max(100)
    @Column(name = "target_intensity_max")
    private Double targetIntensityMax;

    @Min(-100)
    @Max(100)
    @Column(name = "target_resistance_top_pct")
    private Integer targetResistanceTopPct;

    @Min(-100)
    @Max(100)
    @Column(name = "target_resistance_bottom_pct")
    private Integer targetResistanceBottomPct;

    @Column(name = "tempo", length = 20)
    @Pattern(regexp = "\\d-\\d-[\\dX]-\\d")
    private String tempo;

    @Enumerated(EnumType.STRING)
    @Column(name = "prescribed_gear_class", length = 20)
    private GearClass prescribedGearClass;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "prescribed_gear", columnDefinition = "varchar(20)[]")
    @Builder.Default
    private Set<SupportGear> prescribedGear = EnumSet.noneOf(SupportGear.class);

    @Column(name = "rest")
    private String rest;

    @Column(name = "notes", length = 500)
    private String notes;

    @Min(1)
    @Max(20)
    @Column(name = "display_order")
    private Integer displayOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reference_exercise_id")
    private Exercise referenceExercise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_template_id")
    private TrainingSessionTemplate sessionTemplate;

    @Enumerated(EnumType.STRING)
    @Column(name = "reference_source", length = 20)
    private ReferenceSource referenceSource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;
}
