package org.egovframe.cloud.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authorization.AuthorizationContext;

/**
 * org.egovframe.cloud.apigateway.config.WebFluxSecurityConfig
 * <p>
 * Spring Security Config 클래스
 * ReactiveAuthorizationManager<AuthorizationContext> 구현체 ReactiveAuthorization 클래스를 통해 인증/인가 처리를 구현한다.
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/06/30
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/06/30    jaeyeolkim  최초 생성
 * </pre>
 */
@EnableWebFluxSecurity // Spring Security 설정들을 활성화시켜 준다
public class WebFluxSecurityConfig {

    private final static String[] PERMITALL_ANTPATTERNS = {
            ReactiveAuthorization.AUTHORIZATION_URI, "/", "/csrf",
            "/user-service/login", "/?*-service/api/v1/messages/**", "/api/v1/messages/**",
            "/?*-service/actuator/?*", "/actuator/?*",
            "/v3/api-docs/**", "/?*-service/v3/api-docs", "**/configuration/*", "/swagger*/**", "/webjars/**"
    };
    private final static String USER_JOIN_ANTPATTERNS = "/user-service/api/v1/users";

    /**
     * WebFlux 스프링 시큐리티 설정
     *
     * @see ReactiveAuthorization
     * @param http
     * @param check check(Mono<Authentication> authentication, AuthorizationContext context)
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityWebFilterChain configure(ServerHttpSecurity http, ReactiveAuthorizationManager<AuthorizationContext> check) throws Exception {
        http
                .csrf().disable()
                .headers().frameOptions().disable()
            .and()
                .formLogin().disable()
                .httpBasic().authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)) // login dialog disabled & 401 HttpStatus return
            .and()
                .authorizeExchange()
                .pathMatchers(PERMITALL_ANTPATTERNS).permitAll()
                .pathMatchers(HttpMethod.POST, USER_JOIN_ANTPATTERNS).permitAll()
                .anyExchange().access(check);
        return http.build();
    }

}
