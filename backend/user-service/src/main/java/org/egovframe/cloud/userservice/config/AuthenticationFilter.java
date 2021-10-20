package org.egovframe.cloud.userservice.config;

import static org.egovframe.cloud.common.config.GlobalConstant.LOGIN_URI;
import static org.springframework.util.StringUtils.hasLength;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.egovframe.cloud.common.util.LogUtil;
import org.egovframe.cloud.userservice.api.user.dto.UserLoginRequestDto;
import org.egovframe.cloud.userservice.api.user.dto.UserResponseDto;
import org.egovframe.cloud.userservice.service.user.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;

/**
 * org.egovframe.cloud.userservice.config.AuthenticationFilter
 * <p>
 * Spring Security AuthenticationFilter 처리
 * 로그인 인증정보를 받아 토큰을 발급한다
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
@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final TokenProvider tokenProvider;
    private final UserService userService;

    public AuthenticationFilter(AuthenticationManager authenticationManager, TokenProvider tokenProvider, UserService userService) {
        super.setAuthenticationManager(authenticationManager);
        this.tokenProvider = tokenProvider;
        this.userService = userService;
    }

    /**
     * 로그인 요청 시 호출되는 메소드이다.
     * 계정 정보를 받아 인증정보를 생성한다.
     *
     * @param request  http 요청
     * @param response http 응답
     * @return Authentication 인증정보
     * @throws NullPointerException 널 포인터 예외
     * @throws Exception            예외
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            // 사용자가 입력한 인증정보 받기, POST method 값이기 때문에 input stream으로 받았다.
            UserLoginRequestDto creds = new ObjectMapper().readValue(request.getInputStream(), UserLoginRequestDto.class);

            UsernamePasswordAuthenticationToken upat = null;
            if (creds.getProvider() != null && !"email".equals(creds.getProvider())) {
                UserResponseDto userDto = userService.loadUserBySocial(creds);

                upat = new UsernamePasswordAuthenticationToken(
                        userDto.getEmail(),
                        null,
                        AuthorityUtils.createAuthorityList(userDto.getRoleId())
                );

                SecurityContextHolder.getContext().setAuthentication(upat);

                return upat;
            } else {
                upat = new UsernamePasswordAuthenticationToken(
                        creds.getEmail(),
                        creds.getPassword(),
                        new ArrayList<>()
                );

                // 인증정보 만들기
                return getAuthenticationManager().authenticate(upat);
            }
        } catch (NullPointerException e) {
            log.error(e.getLocalizedMessage());
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 로그인 인증 성공 후 호출된다.
     * 토큰을 생성하여 헤더에 토큰 정보를 담는다.
     *
     * @param request
     * @param response
     * @param chain
     * @param authResult
     * @throws IOException
     * @throws ServletException
     */
    @Transactional
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        // 토큰 생성 및 response header add
        tokenProvider.createTokenAndAddHeader(request, response, chain, authResult);
        // 로그인 성공 후처리
        userService.loginCallback(LogUtil.getSiteId(request), authResult.getName(), true, "");
    }

    @Transactional
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        String failContent = failed.getMessage();
        if (failed instanceof InternalAuthenticationServiceException) {
            log.info("{} 해당 사용자가 없습니다", request.getAttribute("email"));
        } else if (failed instanceof BadCredentialsException) {
            failContent = "패스워드 인증에 실패하였습니다. " + failContent;
        }
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        // 로그인 실패 후처리
        String email = (String) request.getAttribute("email");
        userService.loginCallback(LogUtil.getSiteId(request), email, false, failContent);
        super.unsuccessfulAuthentication(request, response, failed);
    }


    /**
     * 로그인 요청 뿐만 아니라 모든 요청시마다 호출된다.
     * 토큰에 담긴 정보로 Authentication 정보를 설정한다.
     * 이 처리를 하지 않으면 AnonymousAuthenticationToken 으로 처리된다.
     *
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String token = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (!hasLength(token) || "undefined".equals(token)) {
            super.doFilter(request, response, chain);
        } else {
            try {
                final String requestURI = httpRequest.getRequestURI();
                log.info("httpRequest.getRequestURI() ={}", requestURI);

                if (LOGIN_URI.equals(requestURI)) {
                    // 로그인 등 토큰 정보를 꺼낼 필요가 없는 경우
                    SecurityContextHolder.getContext().setAuthentication(null);
                } else {
                    // 토큰 유효성 검사는 API Gateway ReactiveAuthorization 클래스에서 미리 처리된다.
                    Claims claims = tokenProvider.getClaimsFromToken(token);

                    String username = claims.getSubject();
                    if (username == null) {
                        // refresh token 에는 subject, authorities 정보가 없다.
                        SecurityContextHolder.getContext().setAuthentication(null);
                    } else {
                        List<SimpleGrantedAuthority> roleList = Arrays.stream(claims.get(tokenProvider.TOKEN_CLAIM_NAME, String.class).split(","))
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());
                        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(username, null, roleList));
                    }
                }
                chain.doFilter(request, response);

            } catch (Exception e) {
                SecurityContextHolder.getContext().setAuthentication(null);
                HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                log.error("AuthenticationFilter doFilter", e);
            }
        }
    }
}
