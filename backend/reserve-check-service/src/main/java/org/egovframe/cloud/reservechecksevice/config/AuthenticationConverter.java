package org.egovframe.cloud.reservechecksevice.config;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

/** SHA-256(UTF-8) 키 파생으로 오버라이드 */
@Component
public class AuthenticationConverter implements ServerAuthenticationConverter {

    private static final String TOKEN_CLAIM_NAME = "authorities";

    @Value("${token.secret}")
    private String TOKEN_SECRET;

    private SecretKey verificationKey() {
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

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange)
                .flatMap(e -> Mono.justOrEmpty(e.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION)))
                .flatMap(auth -> {
                    if (auth == null) {
                        return Mono.empty();
                    }

                    String token = auth.get(0);
                    if (!StringUtils.hasText(token) || "undefined".equals(token)) {
                        return Mono.empty();
                    }

                    Claims claims = getClaimsFromToken(token);
                    String authoritiesStr = claims.get(TOKEN_CLAIM_NAME, String.class);
                    List<SimpleGrantedAuthority> roleList = authoritiesStr == null ? List.of() :
                        Arrays.stream(authoritiesStr.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    String username = claims.getSubject();

                    if (username == null) {
                        ReactiveSecurityContextHolder.withAuthentication(null);
                        return Mono.empty();
                    }
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(username, null, roleList);

                    ReactiveSecurityContextHolder.withAuthentication(authenticationToken);
                    return Mono.just(authenticationToken);
                });

    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(verificationKey())
                .build()
                .parseSignedClaims(normalizeBearerToken(token))
                .getPayload();
    }
}
