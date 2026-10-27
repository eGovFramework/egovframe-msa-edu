package org.egovframe.cloud.portalservice.support;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * portal-service {@code AuthenticationFilter} 가 검증하는 형식(HS256, 키 = SHA-256(secret))의 테스트용 JWT 발급.
 * 비밀 값은 {@code application-test.yml} 의 {@code token.secret} 과 같다.
 */
public final class TestTokens {

    public static final String SECRET = "egovframe_user_token";
    public static final String ADMIN = "ROLE_ADMIN";
    public static final String USER = "ROLE_USER";

    private TestTokens() {
    }

    public static String bearer(String subject, String authorities) {
        return "Bearer " + Jwts.builder()
                .subject(subject)
                .claim("authorities", authorities)
                .expiration(new Date(System.currentTimeMillis() + 3_600_000L))
                .signWith(key())
                .compact();
    }

    private static SecretKey key() {
        try {
            return Keys.hmacShaKeyFor(MessageDigest.getInstance("SHA-256").digest(SECRET.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
