package com.jpmt.powerlab.security;

import com.jpmt.powerlab.models.domain.User;
import com.jpmt.powerlab.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserProviderImpl implements  CurrentUserProvider {
    private final UserRepository userRepository;

    public CurrentUserProviderImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("No hay usuario autenticado en el contexto");
        }
        return Long.valueOf(auth.getName());   // auth.getName() == el "sub" del token == userId
    }

    @Override
    public User currentUser() {
        Long id = currentUserId();
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Usuario del token no existe: " + id));
    }
}
