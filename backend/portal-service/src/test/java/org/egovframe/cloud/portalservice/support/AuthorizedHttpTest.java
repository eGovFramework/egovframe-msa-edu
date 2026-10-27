package org.egovframe.cloud.portalservice.support;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * 실제 HTTP(RANDOM_PORT)로 컨트롤러를 호출하는 테스트의 공통 기반.
 * 서비스 간 인가 호출은 {@link UserServiceAuthorizationStub} 로 향하고, 주입된 {@code TestRestTemplate} 의 기본 요청은 관리자 JWT 를 가진다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(AuthorizedHttpTestConfig.class)
public abstract class AuthorizedHttpTest {

    @LocalServerPort
    private int serverPort;

    @DynamicPropertySource
    static void userServiceStub(DynamicPropertyRegistry registry) {
        UserServiceAuthorizationStub.register(registry);
    }

    /** Authorization 헤더를 전혀 보내지 않는 (로그인하지 않은 사용자) 클라이언트. 서버 루트 URI 가 설정되어 있다. */
    protected TestRestTemplate anonymousClient() {
        return new TestRestTemplate(new RestTemplateBuilder()
                .rootUri("http://localhost:" + serverPort));
    }

    /** 지정한 권한의 JWT 를 매 요청에 실어 보내는 클라이언트. 서버 루트 URI 가 설정되어 있다. */
    protected TestRestTemplate clientWithRole(String subject, String authorities) {
        return new TestRestTemplate(new RestTemplateBuilder()
                .rootUri("http://localhost:" + serverPort)
                .defaultHeader("Authorization", TestTokens.bearer(subject, authorities)));
    }
}
