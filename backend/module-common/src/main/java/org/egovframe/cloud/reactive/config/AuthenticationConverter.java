package org.egovframe.cloud.reactive.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
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
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;


/**
 * org.egovframe.cloud.reserveitemservice.config.AuthenticationConverter
 *
 * 요청을 authentiation으로 변환하는 클래스
 * AuthenticationWebFilter에서 호출됨.
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/09/06
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/06    shinmj       최초 생성
 * </pre>
 */
@Slf4j
@Component
public class AuthenticationConverter implements ServerAuthenticationConverter {
    @Value("${token.secret}")
    private String TOKEN_SECRET;
    final String TOKEN_CLAIM_NAME = "authorities";

    /**
     * 요청에 담긴 토큰을 조회하여 Authentication 정보를 설정한다.
     *
     * @param exchange
     * @return
     */
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
                    String authorities = claims.get(TOKEN_CLAIM_NAME, String.class);
                    List<SimpleGrantedAuthority> roleList = new ArrayList<>();
                    roleList.add(new SimpleGrantedAuthority(authorities));

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
