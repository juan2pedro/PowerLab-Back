package com.jpmt.powerlab.models.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "training_session_template")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class TrainingSessionTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "block_id", nullable = false)
    private Block block;

    @NotNull
    @Min(1)
    @Column(name = "week_number")

    private Integer weekNumber;

    @NotNull
    @Min(1)
    @Max(7)
    @Column(name = "day_index")
    private Integer dayIndex;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "conjugate_day_type")
    private ConjugatedDayType conjugatedDayType;

    @Column(name = "notes", length = 1000)
    private String notes;

    @OneToMany(mappedBy = "sessionTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    private List<TrainingSetTemplate> trainingSets;

}
