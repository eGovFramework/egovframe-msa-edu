package org.egovframe.cloud.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;

import io.jsonwebtoken.security.Keys;

/**
 * JWT HS256 서명/검증용 HMAC 키 파생.
 * <p>
 * user-service {@code TokenProvider}와 동일하게 UTF-8 시크릿 문자열을 SHA-256으로 다이제스트한 뒤
 * {@link Keys#hmacShaKeyFor(byte[])}로 키를 만든다.
 * </p>
 */
public final class JwtHs512Keys {

    private JwtHs512Keys() {
    }

    public static SecretKey keyFromSecret(String tokenSecret) {
        byte[] digest = sha256(tokenSecret.getBytes(StandardCharsets.UTF_8));
        return Keys.hmacShaKeyFor(digest);
    }

    private static byte[] sha256(byte[] input) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(input);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    /**
     * Authorization 헤더 값에서 Bearer 접두를 제거한 순수 JWT 문자열.
     */
    public static String normalizeBearerToken(String token) {
        if (token == null) {
            return "";
        }
        String t = token.trim();
        if (t.startsWith("Bearer ")) {
            return t.substring(7).trim();
        }
        return t;
    }
}
