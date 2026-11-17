package com.jpmt.powerlab.models.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "body_weight")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bodyweight {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   @ManyToOne(optional = false, fetch = FetchType.LAZY)
   @JoinColumn(name = "user_id", nullable = false)
   private User user;


   private LocalDate date;

   private Double weight;

   private String notes;


}
