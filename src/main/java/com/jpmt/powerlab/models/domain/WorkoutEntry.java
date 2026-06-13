package com.jpmt.powerlab.models.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "workout_entry")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class WorkoutEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_session_id", nullable = false)
    private WorkoutSession session;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(name = "actual_sets")
    private Integer actualSets;
    @Column(name = "actual_reps")
    private Integer actualReps;
    @Column(name = "actual_plate_weight")
    private Double actualPlateWeight;
    @Column(name = "actual_rir_or_rpe")
    private String actualRirOrRpe;

    @Column(name = "is_warmup")
    private Boolean isWarmup;

    @Column(name = "notes", length = 500)
    private String notes;

    @OneToMany(mappedBy = "workoutEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sequenceNumber ASC")
    @Builder.Default
    private Set<WorkoutSet> sets = new LinkedHashSet<>();
}
