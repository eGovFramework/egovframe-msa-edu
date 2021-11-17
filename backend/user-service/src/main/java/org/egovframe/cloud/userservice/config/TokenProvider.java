package org.egovframe.cloud.userservice.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.egovframe.cloud.userservice.api.user.dto.UserResponseDto;
import org.egovframe.cloud.userservice.service.user.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.stream.Collectors;

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

        // JWT Refresh 토큰 생성 후 사용자 도메인에 저장하여 토큰 재생성 요청시 활용한다.
        String refreshToken = createRefreshToken();
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
                .setSubject(userId)
                .claim(TOKEN_CLAIM_NAME, authorities)
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(TOKEN_EXPIRATION_TIME)))
                .signWith(SignatureAlgorithm.HS512, TOKEN_SECRET)
                .compact();
    }

    /**
     * JWT Refresh Token 생성
     * 중복 로그인을 허용하려면 user domain 에 있는 refresh token 값을 반환하고 없는 경우에만 생성하도록 처리한다.
     *
     * @return
     */
    private String createRefreshToken() {
        return Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(TOKEN_REFRESH_TIME)))
                .signWith(SignatureAlgorithm.HS512, TOKEN_SECRET)
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
        // refresh token 으로 유효한 사용자가 있는지 찾는다.
        org.egovframe.cloud.userservice.domain.user.User user = userService.findByRefreshToken(refreshToken);
        // 사용자가 있으면 access token 을 새로 발급하여 리턴한다.
        String accessToken = createAccessToken(user.getRoleKey(), user.getUserId());

        String filteredRefreshToken = refreshToken.replaceAll("\r", "").replaceAll("\n", "");

        // Header에 토큰 세팅
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
                .setSigningKey(TOKEN_SECRET)
                .parseClaimsJws(token)
                .getBody();
    }

}
