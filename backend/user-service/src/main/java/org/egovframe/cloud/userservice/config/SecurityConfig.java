package org.egovframe.cloud.userservice.config;

import lombok.RequiredArgsConstructor;
import org.egovframe.cloud.userservice.service.user.UserService;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.egovframe.cloud.common.config.GlobalConstant.SECURITY_PERMITALL_ANTPATTERNS;

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
@EnableWebSecurity // Spring Security 설정들을 활성화시켜 준다
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final TokenProvider tokenProvider;
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 스프링 시큐리티 설정
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .headers().frameOptions().disable()
            .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 토큰 사용하기 때문에 세션은 비활성화
            .and()
                .authorizeRequests()
                .antMatchers(SECURITY_PERMITALL_ANTPATTERNS).permitAll()
                .anyRequest().access("@authorizationService.isAuthorization(request, authentication)") // 호출 시 권한 인가 데이터 확인
            .and()
                .addFilter(getAuthenticationFilter())
                .logout()
                .logoutSuccessUrl("/");
    }

    /**
     * 로그인 인증정보를 받아 토큰을 발급할 수 있도록 필터를 등록해준다.
     *
     * @return
     * @throws Exception
     */
    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        return new AuthenticationFilter(authenticationManager(), tokenProvider, userService);
    }

    /**
     * 인증 관련 - 로그인 처리
     * DB 에서 조회하여 일치하는지 체크한다.
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // userService.loadUserByUsername 메소드
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }

}
