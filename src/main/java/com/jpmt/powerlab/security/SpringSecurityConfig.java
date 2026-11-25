package com.jpmt.powerlab.security;

import com.jpmt.powerlab.security.filter.JwtAuthenticationFilter;
import com.jpmt.powerlab.security.filter.JwtValidationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;

import static com.jpmt.powerlab.security.TokenJwtToken.key;

@Configuration
@EnableMethodSecurity
public class SpringSecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public SpringSecurityConfig(AuthenticationConfiguration authenticationConfiguration,
                                RestAuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        SecretKey secretKey = key(jwtSecret);
        return http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/swagger-ui/**","/v3/api-docs/**","/swagger-ui.html").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users/register").permitAll()
                .anyRequest().authenticated())
                .addFilter(new JwtAuthenticationFilter(authenticationManager(), secretKey))
                .addFilter(new JwtValidationFilter(authenticationManager(), secretKey))
                .cors(Customizer.withDefaults())
                // CSRF deshabilitado a proposito: API stateless con JWT en cabecera Authorization
                // (sin cookies de sesion) => CSRF no aplica. Reactivarlo (Customizer.withDefaults)
                // rompe todos los POST/PUT/DELETE con 403. El hallazgo de CodeQL es falso positivo aqui.
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
}
