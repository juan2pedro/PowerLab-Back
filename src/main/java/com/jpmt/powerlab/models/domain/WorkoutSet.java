package com.jpmt.powerlab.models.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.Set;

@Entity
@Table(name = "workout_set")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_entry_id", nullable = false)
    private WorkoutEntry workoutEntry;

    @Min(1)
    @Column(name = "set_number", nullable = false)
    private Integer sequenceNumber;

    @Column(name = "reps", nullable = false)
    private Integer reps;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "rpe_value")
    @Min(0)
    @Max(11)
    private Double rpeValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "set_type", nullable = false, length = 20)
    @Builder.Default
    private SetType type = SetType.WORKING;

    @ElementCollection(targetClass = Equipment.class)
    @CollectionTable(name = "workout_set_equipment",
            joinColumns = @JoinColumn(name = "workout_set_id"))
    @Column(name = "equipment", length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<Equipment> usedEquipment = EnumSet.noneOf(Equipment.class);

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}