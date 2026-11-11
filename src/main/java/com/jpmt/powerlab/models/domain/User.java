package com.jpmt.powerlab.models.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.Date;

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

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(unique = true, nullable = false)
    private String username;

    private Sex sex;

    @Builder.Default
    @Column(nullable = false)
    private boolean coach = false;

    private Date dateOfBirth;

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
