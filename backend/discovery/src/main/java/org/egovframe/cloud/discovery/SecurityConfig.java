package org.egovframe.cloud.discovery;

import org.springframework.context.annotation.Bean;
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
 *  2024/09/23    이백행        컨트리뷰션 이클립스 문제(Problems) 해결
 * </pre>
 */
@EnableWebSecurity // Spring Security 설정들을 활성화시켜 준다
public class SecurityConfig  {

    /**
     * 스프링 시큐리티 설정
     *
     * @param http
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .headers().frameOptions().disable()
            .and()
                .authorizeRequests()
                .antMatchers("/actuator/?*").permitAll()
                .anyRequest().authenticated()
            .and()
                .httpBasic();
        return http.build();
    }

}
