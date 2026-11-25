package com.jpmt.powerlab.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpmt.powerlab.security.SimpleGrantedAuthorityJsonCreator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.jpmt.powerlab.security.TokenJwtToken.*;

public class JwtValidationFilter extends BasicAuthenticationFilter {
    private final SecretKey secretKey;

    public JwtValidationFilter(AuthenticationManager authenticationManager, SecretKey secretKey) {
        super(authenticationManager);
        this.secretKey = secretKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(HEADER_AUTHORIZATION);
        if(header == null || !header.startsWith(PREFIX_TOKEN)){
            chain.doFilter(request, response);
            return;
        }
        String token = header.replace(PREFIX_TOKEN, "");

        try {
            Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
            String userId = claims.getSubject();
            Object authoritiesClaims = claims.get("authorities");

            Collection<? extends GrantedAuthority> authorities = Arrays.asList(
                    new ObjectMapper()
                            .addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityJsonCreator.class)
                            .readValue(authoritiesClaims.toString().getBytes(), SimpleGrantedAuthority[].class));

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userId, null,authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            chain.doFilter(request,response);
        }catch (JwtException e){
            Map <String,String> body = new HashMap<>();
            body.put("error", e.getMessage());
            body.put("message", "El token JWT es invalido!");
            response.setContentType(CONTENT_TYPE);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        }


    }
}
