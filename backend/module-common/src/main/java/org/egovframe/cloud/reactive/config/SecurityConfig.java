package org.egovframe.cloud.reactive.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import reactor.core.publisher.Mono;

/**
 * org.egovframe.cloud.reserveitemservice.config.SecurityConfig
 *
 * Spring Security Config 클래스
 * AuthenticationFilter 를 추가하고 토큰으로 setAuthentication 인증처리를 한다
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
@RequiredArgsConstructor
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final AuthenticationConverter authenticationConverter;

    /**
     * Reactive Security 설정
     *
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityWebFilterChain configure(ServerHttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .logout().disable()
                .addFilterAt(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    /**
     * AuthenticationManager
     * Api Gateway에서 인증되기 때문에 따로 확인하지 않는다.
     *
     * @return
     */
    @Bean
    public ReactiveAuthenticationManager authenticationManager() {
        return authentication -> Mono.just(authentication);
    }

    /**
     * 인증 요청 필터
     * AuthenticationConverter 를 적용하여 Authentication 정보를 설정한다.
     *
     * @return
     * @throws Exception
     */
    public AuthenticationWebFilter authenticationWebFilter() throws Exception {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(authenticationManager());
        filter.setServerAuthenticationConverter(authenticationConverter);
        return  filter;
    }
}
