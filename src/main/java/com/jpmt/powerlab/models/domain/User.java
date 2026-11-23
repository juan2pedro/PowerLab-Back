package com.jpmt.powerlab.models.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String passwordHash;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Sex sex;

    @Builder.Default
    @Column(nullable = false)
    private boolean coach = false;

    private LocalDate dateOfBirth;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "default_pr_visibility", nullable = false, length = 20)
    @Builder.Default
    private Visibility defaultPrVisibility = Visibility.PRIVATE;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

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
