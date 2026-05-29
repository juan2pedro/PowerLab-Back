package com.jpmt.strengthlab.models.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    @Column(name = "block_name", length = 100)
    private String blockName;

    @NotNull
    @Min(1)
    @Column(name = "week_number")
    private Integer weekNumber;

    @NotNull
    @Min(1)
    @Max(7)
    @Column(name = "day_in_week")
    private Integer dayInWeek;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "conjugate_day_type")
    private ConjugatedDayType conjugatedDayType;

    @Column(name = "notes", length = 1000)
    private String notes;

    @OneToMany(mappedBy = "sessionTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrainingSetTemplate> setTemplates;

}
