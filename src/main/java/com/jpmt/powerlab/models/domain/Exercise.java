package com.jpmt.powerlab.models.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "exercise")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "base_name", nullable = false, length = 100)
    private String baseName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "main_pattern", nullable = false, length = 30)
    private MainPattern mainPattern;

    @Enumerated(EnumType.STRING)
    @Column(name = "implement", length = 30)
    private Implement implement;

    @Enumerated(EnumType.STRING)
    @Column(name = "setup", length = 30)
    private Setup setup;

    @Enumerated(EnumType.STRING)
    @Column(name = "stance", length = 30)
    private Stance stance;

    @Enumerated(EnumType.STRING)
    @Column(name = "grip", length = 30)
    private Grip grip;

    @Enumerated(EnumType.STRING)
    @Column(name = "height_mode", length = 30)
    private HeightMode heightMode;

    @Column(name = "height_value")
    private Double heightValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "accommodating_resistance", length = 30)
    private AccommodatingResistance accommodatingResistance;

    @NotNull
    @Column(nullable = false)
    @Builder.Default
    private Boolean unilateral = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "measurement_type", length = 30)
    private MeasurementType measurementType;

    @Enumerated(EnumType.STRING)
    @Column(name = "load_mode", length = 30)
    private LoadMode loadMode;

    @Column(name = "notes", length = 500)
    private String notes;

}
