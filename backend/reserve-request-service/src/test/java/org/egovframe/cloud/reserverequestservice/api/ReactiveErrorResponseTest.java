package org.egovframe.cloud.reserverequestservice.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.egovframe.cloud.common.exception.EntityNotFoundException;
import org.egovframe.cloud.reserverequestservice.service.ReserveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.main.web-application-type=reactive",
        "spring.cloud.config.enabled=false", "spring.cloud.discovery.enabled=false",
        "eureka.client.enabled=false", "spring.cloud.bus.enabled=false",
        "spring.cloud.stream.function.autodetect=false", "spring.cloud.function.definition=",
        "spring.rabbitmq.listener.simple.auto-startup=false",
        "spring.r2dbc.url=r2dbc:h2:mem:///advicerequest;DB_CLOSE_DELAY=-1",
        "messages.directory=classpath:/messages", "token.secret=synthetic-advice-test-only",
        "management.tracing.enabled=false", "spring.sql.init.mode=never"
})
class ReactiveErrorResponseTest {

    @Autowired
    private WebTestClient client;

    @MockitoBean
    private ReserveService reserveService;

    @BeforeEach
    void authenticate() throws Exception {
        byte[] key = MessageDigest.getInstance("SHA-256")
                .digest("synthetic-advice-test-only".getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder().subject("advice-test-user").claim("authorities", "ROLE_USER")
                .signWith(Keys.hmacShaKeyFor(key)).compact();
        client = client.mutate().defaultHeader("Authorization", "Bearer " + token).build();
    }

    @Test
    void missingReservationUsesDeclaredBusinessError() {
        when(reserveService.create(any()))
                .thenReturn(Mono.error(new EntityNotFoundException("missing-synthetic")));

        client.post().uri("/api/v1/requests/evaluates").contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}").exchange()
                .expectStatus().isBadRequest().expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.status").isEqualTo(400).jsonPath("$.code").isEqualTo("E003")
                .jsonPath("$.timestamp").exists().jsonPath("$.errors").isEmpty();
    }

    @Test
    void unexpectedFailureUsesCommonServerError() {
        when(reserveService.create(any()))
                .thenReturn(Mono.error(new IllegalStateException("synthetic failure")));

        client.post().uri("/api/v1/requests/evaluates").contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}").exchange()
                .expectStatus().is5xxServerError().expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.status").isEqualTo(500).jsonPath("$.code").isEqualTo("E999")
                .jsonPath("$.errors").isEmpty();
    }

    @Test
    void healthRouteStillReturnsServiceInformation() {
        client.get().uri("/actuator/health-info").exchange().expectStatus().isOk()
                .expectBody(String.class).value(body -> org.assertj.core.api.Assertions.assertThat(body)
                        .contains("Reserve Request Service"));
    }
}
