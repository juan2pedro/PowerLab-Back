package com.jpmt.powerlab.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpmt.powerlab.models.dto.user.LoginRequest;
import com.jpmt.powerlab.security.SecurityUser;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.jpmt.powerlab.security.TokenJwtToken.*;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final SecretKey secretKey;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, SecretKey secretKey) {
        this.authenticationManager = authenticationManager;
        this.secretKey = secretKey;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response){
        LoginRequest loginRequest ;
        try {
            loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType(CONTENT_TYPE);
            Map<String, String> body = Map.of(
                    "message", "Body de login inválido",
                    "error", "JSON malformado o campos incorrectos"

            );

            {

                try {
                    new ObjectMapper().writeValue(response.getOutputStream(), body);
                } catch (IOException ignored) {
                }
            }
            return null;
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());
        return  authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        SecurityUser principal = (SecurityUser) authResult.getPrincipal();

        String token = Jwts.builder()
                .subject(String.valueOf(principal.getId()))
                .claim("authorities", new ObjectMapper().writeValueAsString(authResult.getAuthorities()))
                .expiration(new Date(System.currentTimeMillis() + 864_000_000))
                .issuedAt(new Date())
                .signWith(secretKey)
                .compact();

        response.setHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + token);
        Map<String, String> body = new HashMap<>(Map.of("token", token));
        body.put("username", authResult.getName());
        body.put("message", "Authentication successful");
        response.setContentType(CONTENT_TYPE);
        new ObjectMapper().writeValue(response.getOutputStream(), body);
        response.setStatus(HttpServletResponse.SC_OK);
        super.successfulAuthentication(request, response, chain, authResult);

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        super.unsuccessfulAuthentication(request, response, failed);
        Map<String, String> body = new HashMap<>();
        body.put("message", "Usuario o contraseña incorrectos");
        body.put("error", failed.getMessage());
        response.setContentType(CONTENT_TYPE);
        new ObjectMapper().writeValue(response.getOutputStream(), body);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    }
}
