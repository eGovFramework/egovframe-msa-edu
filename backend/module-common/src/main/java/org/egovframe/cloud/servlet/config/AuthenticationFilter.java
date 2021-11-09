package org.egovframe.cloud.servlet.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * org.egovframe.cloud.servlet.config.AuthenticationFilter
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
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final String TOKEN_SECRET;

    final String TOKEN_CLAIM_NAME = "authorities";

    public AuthenticationFilter(AuthenticationManager authenticationManager, String tokenSecret) {
        super.setAuthenticationManager(authenticationManager);
        this.TOKEN_SECRET = tokenSecret;
    }

    /**
     * AuthenticationFilter.doFilter 메소드에서 UsernamePasswordAuthenticationToken 정보를 세팅할 때 호출된다.
     *
     * @param token
     * @return
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(TOKEN_SECRET)
                .parseClaimsJws(token)
                .getBody();
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
        if (token == null || "undefined".equals(token) || "".equals(token)) {
            super.doFilter(request, response, chain);
        } else {
            Claims claims = getClaimsFromToken(token);
            String authorities = claims.get(TOKEN_CLAIM_NAME, String.class);
            List<SimpleGrantedAuthority> roleList = new ArrayList<>();
            roleList.add(new SimpleGrantedAuthority(authorities));

            String username = claims.getSubject();
            if (username != null) {
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(username, null, roleList));
                chain.doFilter(request, response);
            } else {
                SecurityContextHolder.getContext().setAuthentication(null);
                HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            }
        }
    }
}
