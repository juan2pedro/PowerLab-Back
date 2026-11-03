package com.jpmt.powerlab.models.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;

import java.time.LocalDate;

@Entity
@Table(name = "exercise")
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


}
