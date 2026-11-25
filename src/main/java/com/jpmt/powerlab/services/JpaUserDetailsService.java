package com.jpmt.powerlab.services;

import com.jpmt.powerlab.models.domain.User;
import com.jpmt.powerlab.repositories.UserRepository;
import com.jpmt.powerlab.security.SecurityUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class JpaUserDetailsService  implements org.springframework.security.core.userdetails.UserDetailsService{

    private final UserRepository userRepository;

    public JpaUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        List < GrantedAuthority> authorities =user.isCoach() ? List.of(() -> "ROLE_COACH") : List.of(() -> "ROLE_USER");
        return new SecurityUser(user);
    }

}
