package org.egovframe.cloud.reserveitemservice.support;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/** Signs test-only JWTs using the production AuthenticationConverter key derivation. */
public final class JwtTestTokens {
    private JwtTestTokens() {
    }

    public static String bearer(String secret) {
        try {
            byte[] key = MessageDigest.getInstance("SHA-256").digest(secret.getBytes(StandardCharsets.UTF_8));
            return "Bearer " + Jwts.builder().subject("test-admin").claim("authorities", "ROLE_ADMIN")
                    .signWith(Keys.hmacShaKeyFor(key)).compact();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
