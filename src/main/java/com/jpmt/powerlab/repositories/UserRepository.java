package com.jpmt.powerlab.repositories;

import com.jpmt.powerlab.models.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {


    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}
