package com.jpmt.powerlab.security;

import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public class TokenJwtToken {
    public static final String PREFIX_TOKEN = "Bearer ";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String CONTENT_TYPE = "application/json";

    private TokenJwtToken() {
    }

    /** Construye la clave HMAC a partir del secreto configurado (HS256 => minimo 32 bytes). */
    public static SecretKey key(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
