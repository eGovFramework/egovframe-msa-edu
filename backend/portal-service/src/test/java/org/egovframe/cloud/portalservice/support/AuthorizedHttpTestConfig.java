package org.egovframe.cloud.portalservice.support;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;

/**
 * 컨트롤러 테스트의 {@code TestRestTemplate} 이 관리자 JWT 를 보내도록 한다.
 * 운영 {@code SecurityConfig} 는 그대로이며 인가 판단은 {@link UserServiceAuthorizationStub} 로의 실제 HTTP 호출로 이뤄진다.
 */
@TestConfiguration(proxyBeanMethods = false)
public class AuthorizedHttpTestConfig {

    @Bean
    RestTemplateCustomizer adminTokenCustomizer() {
        return restTemplate -> restTemplate.getInterceptors().add((request, body, execution) -> {
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                request.getHeaders().set(HttpHeaders.AUTHORIZATION, TestTokens.bearer("portal-admin", TestTokens.ADMIN));
            }
            return execution.execute(request, body);
        });
    }
}
