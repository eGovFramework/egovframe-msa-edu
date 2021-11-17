package org.egovframe.cloud.apigateway.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


/**
 * org.egovframe.cloud.apigateway.config.ReactiveAuthorization
 * <p>
 * Spring Security 에 의해 요청 url에 대한 사용자 인가 서비스를 수행하는 클래스 요청에 대한 사용자의 권한여부 체크하여 true/false 리턴한다
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/07/19
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/19    jaeyeolkim  최초 생성
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ReactiveAuthorization implements ReactiveAuthorizationManager<AuthorizationContext> {

    @Value("${apigateway.host:http://localhost:8000}")
    private String APIGATEWAY_HOST;

    @Value("${token.secret}")
    private String TOKEN_SECRET;

    // org.egovframe.cloud.common.config.GlobalConstant 값도 같이 변경해주어야 한다.
    public static final String AUTHORIZATION_URI = "/user-service" + "/api/v1/authorizations/check";
    public static final String REFRESH_TOKEN_URI = "/user-service" + "/api/v1/users/token/refresh";

    /**
     * 요청에 대한 사용자의 권한여부 체크하여 true/false 리턴한다 헤더에 토큰이 있으면 유효성을 체크한다.
     *
     * @param authentication
     * @param context
     * @return
     * @see WebFluxSecurityConfig
     */
    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication,
        AuthorizationContext context) {
        ServerHttpRequest request = context.getExchange().getRequest();
        RequestPath requestPath = request.getPath();
        HttpMethod httpMethod = request.getMethod();

        String baseUrl =
            APIGATEWAY_HOST + AUTHORIZATION_URI + "?httpMethod=" + httpMethod + "&requestPath="
                + requestPath;
        log.info("baseUrl={}", baseUrl);

        String authorizationHeader = "";

        List<String> authorizations =
            request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION) ?
                request.getHeaders().get(HttpHeaders.AUTHORIZATION) : null;

        if (authorizations != null && authorizations.size() > 0
            && StringUtils.hasLength(authorizations.get(0))
            && !"undefined".equals(authorizations.get(0))
        ) {
            try {
                authorizationHeader = authorizations.get(0);
                String jwt = authorizationHeader.replace("Bearer", "");
                String subject = Jwts.parser().setSigningKey(TOKEN_SECRET)
                    .parseClaimsJws(jwt)
                    .getBody()
                    .getSubject();

                // refresh token 요청 시 토큰 검증만 하고 인가 처리 한다.
                if (REFRESH_TOKEN_URI.equals(requestPath + "")) {
                    return Mono.just(new AuthorizationDecision(true));
                }
                if (subject == null || subject.isEmpty()) {
                    log.error("토큰 인증 오류");
                    throw new AuthorizationServiceException("토큰 인증 오류");
                }
            } catch (IllegalArgumentException e) {
                log.error("토큰 헤더 오류 : {}", e.getMessage());
                throw new AuthorizationServiceException("토큰 인증 오류");
            } catch (ExpiredJwtException e) {
                log.error("토큰 유효기간이 만료되었습니다. : {}", e.getMessage());
                throw new AuthorizationServiceException("토큰 유효기간 만료");
            } catch (Exception e) {
                log.error("토큰 인증 오류 Exception : {}", e.getMessage());
                throw new AuthorizationServiceException("토큰 인증 오류");
            }
        }

        boolean granted = false;
        try {
            String token = authorizationHeader; // Variable used in lambda expression should be final or effectively final
            Mono<Boolean> body = WebClient.create(baseUrl)
                .get()
                .headers(httpHeaders -> {
                    httpHeaders.add(HttpHeaders.AUTHORIZATION, token);
                })
                .retrieve().bodyToMono(Boolean.class);
            granted = body.blockOptional().orElse(false);
            log.info("Security AuthorizationDecision granted={}", granted);
        } catch (Exception e) {
            log.error("인가 서버에 요청 중 오류 : {}", e.getMessage());
            throw new AuthorizationServiceException("인가 요청시 오류 발생");
        }

        return Mono.just(new AuthorizationDecision(granted));
    }

}
