package com.jpmt.powerlab.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpmt.powerlab.models.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

import static com.jpmt.powerlab.security.TokenJwtToken.CONTENT_TYPE;

/**
 * Devuelve 401 con el mismo formato {@link ApiErrorResponse} que el resto de errores,
 * cuando se accede a un endpoint protegido sin autenticacion valida.
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(CONTENT_TYPE);
        ApiErrorResponse body = new ApiErrorResponse(
                null,
                "No autenticado: falta un token valido.",
                "Unauthorized",
                HttpStatus.UNAUTHORIZED.value(),
                new Date()
        );
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
