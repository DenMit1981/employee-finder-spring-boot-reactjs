package com.empire.employeefinder.security.jwt;

import com.empire.employeefinder.exception.JwtAuthenticationException;
import com.empire.employeefinder.model.enums.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.*;

@Component
@Slf4j
public class JwtTokenProvider {

    private static final String WRONG_JWT_TOKEN = "JWT token is expired or invalid";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.lifetime}")
    private Duration lifetime;

    private final UserDetailsService userDetailsService;

    public JwtTokenProvider(@Lazy UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public String createToken(final String email, final Role role, final String userId) {
        final Claims claims = Jwts.claims().setSubject(email);

        claims.put("role", role);
        claims.put("id", userId);

        final Date now = new Date();
        final Date validity = new Date(now.getTime() + lifetime.toMillis());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(getSignKeys(), SignatureAlgorithm.HS256)
                .compact();
    }

    Authentication getAuthentication(final String token) {
        final UserDetails userDetails =
                this.userDetailsService.loadUserByUsername(this.getUsername(token));

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    String resolveToken(final HttpServletRequest req) {
        final String bearerToken = req.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    boolean isValidToken(final String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignKeys())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration()
                    .after(new Date());
        } catch (JwtException | IllegalArgumentException exception) {
            throw new JwtAuthenticationException(WRONG_JWT_TOKEN);
        }
    }

    String getUsername(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKeys())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    private Key getSignKeys() {
        byte[] key = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(key);
    }
}
