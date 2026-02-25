package org.egovframe.cloud.userservice.config;

import static org.egovframe.cloud.common.config.GlobalConstant.SECURITY_PERMITALL_ANTPATTERNS;

import org.egovframe.cloud.userservice.service.role.AuthorizationService;
import org.egovframe.cloud.userservice.service.user.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

import lombok.RequiredArgsConstructor;

/**
 * org.egovframe.cloud.userservice.SecurityConfig
 * <p>
 * Spring Security Config 클래스
 * AuthenticationFilter 를 추가하고 로그인 인증처리를 한다
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
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity // Spring Security 설정들을 활성화시켜 준다
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final UserService userService;
    private final AuthenticationConfiguration authConfig;
    private final AuthorizationService authorizationService;

    /**
     * 스프링 시큐리티 설정
     *
     * @param http
     * @return SecurityFilterChain
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 토큰 사용하기 때문에 세션은 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SECURITY_PERMITALL_ANTPATTERNS).permitAll()
                        .anyRequest().access(new WebExpressionAuthorizationManager("@authorizationService.isAuthorization(request, authentication)"))) // 호출 시 권한 인가 데이터 확인
                .addFilter(getAuthenticationFilter())
                .logout(logout -> logout.logoutSuccessUrl("/"));

        return http.build();
    }

    /**
     * 로그인 인증정보를 받아 토큰을 발급할 수 있도록 필터를 등록해준다.
     *
     * @return AuthenticationFilter
     * @throws Exception
     */
    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        return new AuthenticationFilter(authConfig.getAuthenticationManager(), tokenProvider, userService);
    }

    /**
     * UserDetailsService 빈 등록
     * 사용자 정보를 로드하는 서비스
     *
     * @return UserDetailsService
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return userService;
    }

    /**
     * AuthenticationManager 빈 등록
     * 인증 관련 - 로그인 처리
     * DB 에서 조회하여 일치하는지 체크한다.
     *
     * @param authConfig
     * @return AuthenticationManager
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

}
