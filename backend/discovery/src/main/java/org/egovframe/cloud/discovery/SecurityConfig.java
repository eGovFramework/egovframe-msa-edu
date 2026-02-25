package org.egovframe.cloud.discovery;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * org.egovframe.cloud.discovery.SecurityConfig
 * <p>
 * Spring Security Config 클래스
 * Eureka Server 접속 보안
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
 *  2025/09/01    유지보수	  Spring Security 6.5.3 버전에 맞게 수정
 * </pre>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 스프링 시큐리티 설정
     * Spring Security 6.x 버전에서는 WebSecurityConfigurerAdapter가 deprecated되어
     * SecurityFilterChain Bean을 사용하는 방식으로 변경
     *
     * @param http HttpSecurity 객체
     * @return SecurityFilterChain
     * @throws Exception 예외
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.disable())
                )
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> {});
        
        return http.build();
    }

}
