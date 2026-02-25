package org.egovframe.cloud.servlet.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.ObjectUtils;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * org.egovframe.cloud.servlet.config.AuthenticationFilter
 * <p>
 * Spring Security AuthenticationFilter 처리
 * 로그인 인증정보를 받아 토큰을 발급한다
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/06/30
 *
 * <pre>
 * ===== 개정이력(Modification Information) =====
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/06/30    jaeyeolkim  최초 생성
 * </pre>
 */
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final String TOKEN_CLAIM_NAME = "authorities";
    private final String TOKEN_SECRET;

    public AuthenticationFilter(AuthenticationManager authenticationManager, String tokenSecret) {
        super.setAuthenticationManager(authenticationManager);
        this.TOKEN_SECRET = tokenSecret;
    }

    /**
     * AuthenticationFilter.doFilter 메소드에서 UsernamePasswordAuthenticationToken 정보를 세팅할 때 호출된다.
     */
    public Claims getClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(TOKEN_SECRET.getBytes());
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 로그인 요청 뿐만 아니라 모든 요청시마다 호출된다.
     * 토큰에 담긴 정보로 Authentication 정보를 설정한다.
     * 이 처리를 하지 않으면 AnonymousAuthenticationToken 으로 처리된다.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String token = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (ObjectUtils.isEmpty(token)) {
            super.doFilter(request, response, chain);
        } else {
            Claims claims = getClaimsFromToken(token);
            String authorities = claims.get(TOKEN_CLAIM_NAME, String.class);
            List<SimpleGrantedAuthority> roleList = new ArrayList<>();
            roleList.add(new SimpleGrantedAuthority(authorities));

            String username = claims.getSubject();
            if (username != null) {
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(username, null, roleList));
                chain.doFilter(request, response);
            } else {
                SecurityContextHolder.getContext().setAuthentication(null);
                HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            }
        }
    }

}
