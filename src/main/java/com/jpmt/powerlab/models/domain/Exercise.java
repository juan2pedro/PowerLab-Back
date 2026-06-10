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

    @Column(name = "tempo", length = 20)
    private String tempo;

    @Column(name = "pause_seconds")
    private Integer pauseSeconds;

    @Enumerated(EnumType.STRING)
    @Column(name = "height_mode", length = 30)
    private HeightMode heightMode;

    @Column(name = "height_length")
    private Integer heightLength;

    @Column(name = "style", length = 50)
    private String style;

    @Column(name = "bar", length = 50)
    private String bar;

    @Column(name = "resistance", length = 50)
    private String resistance;

    @Enumerated(EnumType.STRING)
    @Column(name = "loading_method", length = 30)
    private LoadingMethod loadingMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "loading_unit", length = 10)
    private LoadingUnit loadingUnit;

    @Column(name = "equipment", length = 100)
    private String equipment;

    @Column(name = "notes", length = 500)
    private String notes;

}
