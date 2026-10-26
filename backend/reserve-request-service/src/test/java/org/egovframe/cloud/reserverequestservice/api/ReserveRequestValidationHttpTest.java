package org.egovframe.cloud.reserverequestservice.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.egovframe.cloud.common.util.MessageUtil;
import org.egovframe.rte.fdl.cmmn.trace.LeaveaTrace;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.egovframe.cloud.reactive.exception.ExceptionHandlerAdvice;
import org.egovframe.cloud.reserverequestservice.config.MessageListenerContainerFactory;
import org.egovframe.cloud.reserverequestservice.domain.Reserve;
import org.egovframe.cloud.reserverequestservice.domain.ReserveRepository;
import org.egovframe.cloud.reserverequestservice.domain.ReserveValidator;
import org.egovframe.cloud.reserverequestservice.service.ReserveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.ReactiveWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.HttpHandlerAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

@SpringBootTest(classes = ReserveRequestValidationHttpTest.HttpApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {"spring.cloud.bootstrap.enabled=false", "spring.cloud.config.enabled=false"})
class ReserveRequestValidationHttpTest {
    private static final String OWNER = "authenticated-owner";
    private static final String[] FIELDS = {
        "reserveItemId", "reservePurposeContent", "userContactNo", "userEmail"
    };

    @LocalServerPort int port;
    @Autowired ReserveRepository repository;
    @Autowired ReserveValidator validator;
    @Autowired AmqpAdmin amqpAdmin;
    @Autowired StreamBridge streamBridge;
    WebTestClient client;

    @BeforeEach
    void setup() {
        client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
        reset(repository, validator, amqpAdmin, streamBridge);
        when(repository.insert(any(Reserve.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));
        when(validator.checkSpace(any())).thenAnswer(i -> Mono.just(i.getArgument(0)));
    }

    static Stream<Arguments> invalidBodies() {
        return Stream.of("/api/v1/requests/evaluates", "/api/v1/requests")
            .flatMap(path -> Stream.of("space", "education")
                .flatMap(category -> Stream.of(FIELDS)
                    .flatMap(field -> Stream.of(false, true)
                        .map(explicitNull -> Arguments.of(path, category, field, explicitNull)))));
    }

    @ParameterizedTest
    @MethodSource("invalidBodies")
    void rejectsMissingAndNullClientFieldsBeforeSideEffects(String path, String category,
            String field, boolean explicitNull) {
        Map<String, Object> body = body(category);
        if (explicitNull) body.put(field, null);
        else body.remove(field);

        client.post().uri(path).bodyValue(body).exchange()
            .expectStatus().isBadRequest()
            .expectBody().jsonPath("$.code").isEqualTo("E001")
            .jsonPath("$.errors.length()").isEqualTo(1)
            .jsonPath("$.errors[0].field").isEqualTo(field);
        verifyNoInteractions(repository, validator, amqpAdmin);
        verify(streamBridge, never()).send(any(String.class), any(Object.class));
    }

    static Stream<Arguments> validBodies() {
        return Stream.of("/api/v1/requests/evaluates", "/api/v1/requests")
            .flatMap(path -> Stream.of("space", "education")
                .flatMap(category -> Stream.of("omitted", "spoofed")
                    .map(owner -> Arguments.of(path, category, owner))));
    }

    @ParameterizedTest
    @MethodSource("validBodies")
    void acceptsClientFieldsAndPersistsAuthenticatedOwner(String path, String category, String owner) {
        Map<String, Object> body = body(category);
        if (owner.equals("spoofed")) body.put("userId", "other-user");

        client.post().uri(path).bodyValue(body).exchange()
            .expectStatus().isCreated()
            .expectBody().jsonPath("$.userId").isEqualTo(OWNER);

        ArgumentCaptor<Reserve> saved = ArgumentCaptor.forClass(Reserve.class);
        verify(repository).insert(saved.capture());
        assertThat(saved.getValue().getUserId()).isEqualTo(OWNER);
        if (path.equals("/api/v1/requests") && category.equals("education")) {
            // saveForEvent delegates to create before declaring the result queue and publishing.
            verify(amqpAdmin).declareQueue(any());
            verify(streamBridge).send(eq("reserveRequest-out-0"), any(Object.class));
        } else {
            verifyNoInteractions(amqpAdmin);
            verify(streamBridge, never()).send(any(String.class), any(Object.class));
        }
    }

    private static Map<String, Object> body(String category) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("reserveItemId", 101);
        body.put("reservePurposeContent", "meeting");
        body.put("userContactNo", "010-0000-0000");
        body.put("userEmail", "owner@example.com");
        body.put("categoryId", category);
        return body;
    }

    @SpringBootConfiguration
    @ImportAutoConfiguration({ReactiveWebServerFactoryAutoConfiguration.class,
        WebFluxAutoConfiguration.class, HttpHandlerAutoConfiguration.class, JacksonAutoConfiguration.class, ValidationAutoConfiguration.class})
    // Direct advice import compensates for unmerged prerequisite PR #316; discovery is tested there.
    @Import({ReserveApiController.class, ReserveService.class, ExceptionHandlerAdvice.class})
    static class HttpApplication {
        @Bean LeaveaTrace leaveaTrace() {
            return new LeaveaTrace();
        }
        @Bean ReserveRepository repository() { return mock(ReserveRepository.class); }
        @Bean ReserveValidator validator() { return mock(ReserveValidator.class); }
        @Bean StreamBridge streamBridge() { return mock(StreamBridge.class); }
        @Bean AmqpAdmin amqpAdmin() { return mock(AmqpAdmin.class); }
        @Bean ConnectionFactory connectionFactory() {
            return mock(ConnectionFactory.class);
        }
        @Bean MessageListenerContainerFactory containers() { return mock(MessageListenerContainerFactory.class); }
        @Bean MessageUtil messageUtil() {
            return new MessageUtil();
        }
        @Bean StaticMessageSource messageSource() { return new StaticMessageSource(); }
        @Bean WebFilter authenticatedIdentity() {
            return (exchange, chain) -> chain.filter(exchange).contextWrite(
                ReactiveSecurityContextHolder.withAuthentication(
                    new UsernamePasswordAuthenticationToken(OWNER, "unused", java.util.List.of())));
        }
    }
}
