package org.egovframe.cloud.portalservice.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.ObjectUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** SHA-256(UTF-8) 키 파생으로 오버라이드 */
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final String TOKEN_CLAIM_NAME = "authorities";
    private final String TOKEN_SECRET;

    public AuthenticationFilter(AuthenticationManager authenticationManager, String tokenSecret) {
        super.setAuthenticationManager(authenticationManager);
        this.TOKEN_SECRET = tokenSecret;
    }

    private SecretKey verificationKey() {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                .digest(TOKEN_SECRET.getBytes(StandardCharsets.UTF_8));
            return Keys.hmacShaKeyFor(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public Claims getClaimsFromToken(String token) {
        String tokenContent = token.trim();
        if (tokenContent.startsWith("Bearer ")) {
            tokenContent = tokenContent.substring(7).trim();
        }
        return Jwts.parser()
            .verifyWith(verificationKey())
            .build()
            .parseSignedClaims(tokenContent)
            .getPayload();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String token = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (ObjectUtils.isEmpty(token)) {
            super.doFilter(request, response, chain);
        } else {
            Claims claims = getClaimsFromToken(token);
            String authoritiesStr = claims.get(TOKEN_CLAIM_NAME, String.class);
            List<SimpleGrantedAuthority> roleList = authoritiesStr == null ? List.of() :
                Arrays.stream(authoritiesStr.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
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
