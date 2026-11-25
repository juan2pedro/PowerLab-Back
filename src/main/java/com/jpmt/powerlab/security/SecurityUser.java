package com.jpmt.powerlab.security;

import com.jpmt.powerlab.models.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class SecurityUser implements UserDetails {
    private final User user;

    public SecurityUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return user.getId();
    }
    public String getEmail() {
        return user.getEmail();
    }

    @Override public String getUsername() { return user.getEmail(); }
    @Override public String getPassword() { return user.getPasswordHash(); }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.isCoach()
                ? List.of(new SimpleGrantedAuthority("ROLE_COACH"))
                : List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }
    @Override public boolean isEnabled() { return user.isActive(); }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
}
