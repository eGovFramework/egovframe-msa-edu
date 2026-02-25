package org.egovframe.cloud.boardservice.config;

import org.egovframe.cloud.servlet.config.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

/**
 * org.egovframe.cloud.boardservice.config.SecurityConfig
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
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity // Spring Security 설정들을 활성화시켜 준다
public class SecurityConfig {

    @Value("${token.secret}")
    private String TOKEN_SECRET;
    private final AuthenticationConfiguration authConfig;

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
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilter(getAuthenticationFilter());

        return http.build();
    }

    /**
     * 토큰에 담긴 정보로 Authentication 정보를 설정하여 jpa audit 처리에 사용된다.
     * 이 처리를 하지 않으면 AnonymousAuthenticationToken 으로 처리된다.
     *
     * @return
     * @throws Exception
     */
    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        return new AuthenticationFilter(authConfig.getAuthenticationManager(), TOKEN_SECRET);
    }

}
