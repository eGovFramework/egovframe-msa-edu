package org.egovframe.cloud.portalservice.config;

import static org.egovframe.cloud.common.config.GlobalConstant.SECURITY_PERMITALL_ANTPATTERNS;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultHttpSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

import lombok.RequiredArgsConstructor;

/**
 * org.egovframe.cloud.portalservice.config.SecurityConfig
 * <p>
 * Spring Security Config 클래스
 * AuthenticationFilter 를 추가하고 토큰으로 setAuthentication 인증처리를 한다
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
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity // Spring Security 설정들을 활성화시켜 준다
public class SecurityConfig {

    @Value("${token.secret}")
    private String TOKEN_SECRET;

    private final ApplicationContext applicationContext;

    /**
     * 스프링 시큐리티 설정
     *
     * @param http
     * @return SecurityFilterChain
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            WebExpressionAuthorizationManager webExpressionAuthorizationManager) throws Exception {
        AuthenticationManager authenticationManager = authentication -> {
            throw new ProviderNotFoundException(
                    "Username/password login is not configured for this service; use JWT in Authorization header.");
        };

        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SECURITY_PERMITALL_ANTPATTERNS).permitAll()
                        .anyRequest().access(webExpressionAuthorizationManager)) // 호출 시 권한 인가 데이터 확인 (SpEL @authorizationService)
                .addFilter(getAuthenticationFilter(authenticationManager))
                .logout(logout -> logout.logoutSuccessUrl("/"));

        return http.build();
    }

    /**
     * WebExpressionAuthorizationManager 생성 시 ExpressionHandler에 ApplicationContext를 설정하여
     * SpEL 표현식 내 @authorizationService 빈 참조가 동작하도록 한다.
     * (Spring Security 6.x 기본 설정에서는 bean resolver가 등록되지 않아 EL1057E 발생)
     */
    @Bean
    public WebExpressionAuthorizationManager webExpressionAuthorizationManager() {
        DefaultHttpSecurityExpressionHandler expressionHandler = new DefaultHttpSecurityExpressionHandler();
        expressionHandler.setApplicationContext(applicationContext);
        WebExpressionAuthorizationManager authorization = new WebExpressionAuthorizationManager(
                "@authorizationService.isAuthorization(request, authentication)");
        authorization.setExpressionHandler(expressionHandler);
        return authorization;
    }

    /**
     * 토큰에 담긴 정보로 Authentication 정보를 설정하여 jpa audit 처리에 사용된다.
     * 이 처리를 하지 않으면 AnonymousAuthenticationToken 으로 처리된다.
     *
     * @return AuthenticationFilter
     */
    private AuthenticationFilter getAuthenticationFilter(AuthenticationManager authenticationManager) {
        return new AuthenticationFilter(authenticationManager, TOKEN_SECRET);
    }

}
