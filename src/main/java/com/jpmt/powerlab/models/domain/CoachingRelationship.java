package com.jpmt.powerlab.models.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "coaching_relationship")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoachingRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "coach_id", nullable = false)
    private User coach;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "athlete_id", nullable = false)
    private User athlete;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CoachingStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "initiated_by", nullable = false, length = 20)
    private CoachingInitiator initiatedBy;

    @Column(name = "invite_code")
    private String inviteCode;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "responded_at")
    private OffsetDateTime respondedAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }
}
