package org.egovframe.cloud.reservechecksevice.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;

import org.egovframe.cloud.common.domain.Role;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * 테스트 전용 JWT 발급기.
 * 운영 {@code AuthenticationConverter} 와 같은 규칙(SHA-256(UTF-8) 키 파생, {@code authorities} claim)으로
 * 서명하므로, 테스트 요청은 운영 보안 필터 체인을 그대로 통과해야 한다.
 */
public final class JwtTestTokens {

    private JwtTestTokens() {
    }

    public static String bearer(String secret, String userId, Role role) {
        return "Bearer " + token(secret, userId, role == null ? null : role.getKey());
    }

    public static String token(String secret, String userId, String authorities) {
        var builder = Jwts.builder().subject(userId);
        if (authorities != null) {
            builder.claim("authorities", authorities);
        }
        return builder.signWith(key(secret)).compact();
    }

    private static SecretKey key(String secret) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(secret.getBytes(StandardCharsets.UTF_8));
            return Keys.hmacShaKeyFor(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
