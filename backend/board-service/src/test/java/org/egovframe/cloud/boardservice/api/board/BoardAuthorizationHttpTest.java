package org.egovframe.cloud.boardservice.api.board;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.egovframe.cloud.boardservice.support.AuthorizedHttpTest;
import org.egovframe.cloud.boardservice.support.TestTokens;
import org.egovframe.cloud.boardservice.support.UserServiceAuthorizationStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * 운영 {@code SecurityConfig} 의 인가 경로를 실제 HTTP 로 검증한다.
 * 요청 → {@code AuthenticationFilter}(JWT) → {@code DelegatingAuthorizationService} → (HTTP) user-service 스텁의 순서로
 * 동작하며, 인가 결과가 거부이면 컨트롤러에 도달하지 않는다.
 */
class BoardAuthorizationHttpTest extends AuthorizedHttpTest {

    private static final String URL = "/api/v1/boards";

    /** 기본 관리자 토큰을 붙이는 컨텍스트의 TestRestTemplate 과 달리 요청을 그대로 보내는 클라이언트 */
    private TestRestTemplate restTemplate;

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        UserServiceAuthorizationStub.clearCalls();
        restTemplate = new TestRestTemplate(new RestTemplateBuilder().rootUri("http://localhost:" + port));
    }

    /** 컨테이너가 403 이후 /error 로 디스패치하는 요청은 같은 인가 경로를 타므로 제외하고 API 호출만 본다. */
    private static List<UserServiceAuthorizationStub.Call> apiCalls() {
        return UserServiceAuthorizationStub.calls().stream()
                .filter(call -> !call.requestPath().endsWith("/error")).toList();
    }

    private ResponseEntity<String> get(String authorization) {
        HttpHeaders headers = new HttpHeaders();
        if (authorization != null) {
            headers.set(HttpHeaders.AUTHORIZATION, authorization);
        }
        return restTemplate.exchange(URL + "?page=0&size=10", HttpMethod.GET, new HttpEntity<>(headers), String.class);
    }

    @Test
    void 토큰이_없으면_거부되고_인가_서비스에_익명으로_조회한다() {
        ResponseEntity<String> response = get(null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getHeaders().getContentType()).isNull();
        assertThat(apiCalls()).singleElement().satisfies(call -> {
            assertThat(call.httpMethod()).isEqualTo("GET");
            assertThat(call.requestPath()).isEqualTo("/board-service" + URL);
            assertThat(call.authorization()).isNull();
            assertThat(call.allowed()).isFalse();
        });
    }

    @Test
    void 인가_규칙에_없는_권한은_거부된다() {
        ResponseEntity<String> response = get(TestTokens.bearer("someone", "ROLE_GUEST"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(apiCalls()).singleElement()
                .satisfies(call -> assertThat(call.allowed()).isFalse());
    }

    @Test
    void 일반_사용자는_조회는_성공하고_등록은_거부된다() {
        String user = TestTokens.bearer("user1", TestTokens.USER);

        ResponseEntity<String> read = get(user);
        assertThat(read.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(read.getHeaders().getContentType()).isNotNull();
        assertThat(read.getHeaders().getContentType().isCompatibleWith(MediaType.APPLICATION_JSON)).isTrue();
        assertThat(read.getBody()).contains("\"content\"");

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, user);
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> write = restTemplate.exchange(URL, HttpMethod.POST,
                new HttpEntity<>(Map.of("boardName", "거부되어야 하는 게시판"), headers), String.class);
        assertThat(write.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(apiCalls()).extracting(UserServiceAuthorizationStub.Call::allowed)
                .containsExactly(true, false);
    }

    @Test
    void 관리자는_인가되어_JSON_응답을_받고_토큰이_인가_서비스까지_전달된다() {
        String admin = TestTokens.bearer("board-admin", TestTokens.ADMIN);

        ResponseEntity<String> response = get(admin);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType().isCompatibleWith(MediaType.APPLICATION_JSON)).isTrue();
        assertThat(apiCalls()).singleElement().satisfies(call -> {
            assertThat(call.authorization()).isEqualTo(admin);
            assertThat(call.allowed()).isTrue();
        });
    }
}
