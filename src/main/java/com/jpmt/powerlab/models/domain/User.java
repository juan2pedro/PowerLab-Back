package com.jpmt.powerlab.models.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;

import java.time.LocalDate;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @Column(nullable = false)
    private Long id;

    @UniqueElements
    private String email;

    private String passwordHash;

    private String username;

    private Sex sex;

    private Boolean isCoach;

    private LocalDate dateOfBirth;

    private LocalDate createdAt;

    private LocalDate updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "default_Pr_Visibility", nullable = false, length = 20)
    @Builder.Default
    private Visibility defaultPrVisibility = Visibility.PRIVATE;
}
