package org.egovframe.cloud.userservice.config;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.egovframe.cloud.userservice.api.user.dto.UserResponseDto;
import org.egovframe.cloud.userservice.service.user.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * org.egovframe.cloud.userservice.config.TokenProvider
 * <p>
 * 로그인 성공 인증정보로 토큰을 생성한다.
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/07/01
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/01    jaeyeolkim  최초 생성
 * </pre>
 */
@Component
public class TokenProvider {

    private final UserService userService;

    public TokenProvider(UserService userService) {
        this.userService = userService;
    }

    @Value("${token.secret}")
    private String TOKEN_SECRET;

    @Value("${token.expiration_time}")
    private String TOKEN_EXPIRATION_TIME;

    @Value("${token.refresh_time}")
    private String TOKEN_REFRESH_TIME;

    final String TOKEN_CLAIM_NAME = "authorities";
    final String TOKEN_ACCESS_KEY = "access-token";
    final String TOKEN_REFRESH_KEY = "refresh-token";
    final String TOKEN_USER_ID = "token-id";

    /**
     * token.secret UTF-8 문자열의 SHA-256 다이제스트로 HMAC 키를 만든다.
     */
    private SecretKey signingKey() {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                .digest(TOKEN_SECRET.getBytes(StandardCharsets.UTF_8));
            return Keys.hmacShaKeyFor(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private String normalizeBearerToken(String token) {
        if (token == null) {
            return "";
        }
        String t = token.trim();
        if (t.startsWith("Bearer ")) {
            return t.substring(7).trim();
        }
        return t;
    }

    /**
     * 로그인 후 토큰을 생성하고 헤더에 정보를 담는다.
     *
     * @param request
     * @param response
     * @param chain
     * @param authResult
     */
    public void createTokenAndAddHeader(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        // 로그인 성공 후 토큰 처리
        String email = authResult.getName();
        String authorities = authResult.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // userid 가져오기
        UserResponseDto userResponseDto = userService.findByEmail(email);
        String userId = userResponseDto.getUserId();

        // JWT Access 토큰 생성
        String accessToken = createAccessToken(authorities, userId);

        // JWT Refresh 토큰 생성 후 해시값을 DB에 저장한다.
        String refreshToken = createRefreshToken(userId);
        userService.updateRefreshToken(userId, refreshToken);

        // Header에 토큰 세팅
        response.addHeader(TOKEN_ACCESS_KEY, accessToken);
        response.addHeader(TOKEN_REFRESH_KEY, refreshToken);
        response.addHeader(TOKEN_USER_ID, userId);
    }

    /**
     * JWT Access Token 생성
     *
     * @param authorities
     * @param userId
     * @return
     */
    private String createAccessToken(String authorities, String userId) {
        return Jwts.builder()
                .subject(userId)
                .claim(TOKEN_CLAIM_NAME, authorities)
                .expiration(new Date(System.currentTimeMillis() + Long.parseLong(TOKEN_EXPIRATION_TIME)))
                .signWith(signingKey())
                .compact();
    }

    /**
     * JWT Refresh Token 생성 (subject/JTI 바인딩)
     *
     * @param userId
     * @return
     */
    private String createRefreshToken(String userId) {
        return Jwts.builder()
                .subject(userId)
                .id(java.util.UUID.randomUUID().toString())
                .expiration(new Date(System.currentTimeMillis() + Long.parseLong(TOKEN_REFRESH_TIME)))
                .signWith(signingKey())
                .compact();
    }

    /**
     * 사용자가 있으면 access token 을 새로 발급하여 리턴한다.
     *
     * @param refreshToken
     * @param response
     * @return
     */
    public String refreshToken(String refreshToken, HttpServletResponse response) {
        String normalizedRefreshToken = normalizeBearerToken(refreshToken);
        Claims claims = getClaimsFromToken(normalizedRefreshToken);
        String userId = claims.getSubject();

        org.egovframe.cloud.userservice.domain.user.User user =
            userService.findByRefreshToken(normalizedRefreshToken);

        if (userId == null || !userId.equals(user.getUserId())) {
            throw new org.springframework.security.core.userdetails.UsernameNotFoundException("Invalid refresh token subject");
        }

        String accessToken = createAccessToken(user.getRoleKey(), user.getUserId());
        String newRefreshToken = createRefreshToken(user.getUserId());
        userService.updateRefreshToken(user.getUserId(), newRefreshToken);

        String filteredRefreshToken = newRefreshToken.replaceAll("\r", "").replaceAll("\n", "");

        response.addHeader(TOKEN_ACCESS_KEY, accessToken);
        response.addHeader(TOKEN_REFRESH_KEY, filteredRefreshToken);
        response.addHeader(TOKEN_USER_ID, user.getUserId());
        return accessToken;
    }

    /**
     * AuthenticationFilter.doFilter 메소드에서 UsernamePasswordAuthenticationToken 정보를 세팅할 때 호출된다.
     *
     * @param token
     * @return
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(normalizeBearerToken(token))
                .getPayload();
    }

}
