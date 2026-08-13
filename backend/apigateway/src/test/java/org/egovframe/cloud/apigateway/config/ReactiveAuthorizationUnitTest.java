package org.egovframe.cloud.apigateway.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

class ReactiveAuthorizationUnitTest {

    @Test
    void grantsAccessWhenAuthorizationServerReturnsTrue() {
        ReactiveAuthorization authorization = authorizationReturning("true");

        assertThat(authorization.check(Mono.empty(), authorizationContext()).block())
                .matches(decision -> decision != null && decision.isGranted());
    }

    @Test
    void deniesAccessWhenAuthorizationServerReturnsFalse() {
        ReactiveAuthorization authorization = authorizationReturning("false");

        assertThat(authorization.check(Mono.empty(), authorizationContext()).block())
                .matches(decision -> decision != null && !decision.isGranted());
    }

    @Test
    void deniesAccessWhenAuthorizationServerReturnsEmptyBody() {
        ReactiveAuthorization authorization = authorizationReturning("");

        assertThat(authorization.check(Mono.empty(), authorizationContext()).block())
                .matches(decision -> decision != null && !decision.isGranted());
    }

    @Test
    void mapsAuthorizationServerFailureToAuthorizationServiceException() {
        ExchangeFunction exchangeFunction = request -> Mono.error(new IllegalStateException("connection failed"));
        ReactiveAuthorization authorization = authorizationWith(exchangeFunction);

        assertThatThrownBy(() -> authorization.check(Mono.empty(), authorizationContext()).block())
                .isInstanceOf(AuthorizationServiceException.class)
                .hasCauseInstanceOf(IllegalStateException.class);
    }

    private ReactiveAuthorization authorizationReturning(String responseBody) {
        ExchangeFunction exchangeFunction = request -> {
            ClientResponse.Builder response = ClientResponse
                    .create(HttpStatus.OK)
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            if (!responseBody.isEmpty()) {
                response.body(responseBody);
            }
            return Mono.just(response.build());
        };
        return authorizationWith(exchangeFunction);
    }

    private ReactiveAuthorization authorizationWith(ExchangeFunction exchangeFunction) {
        ReactiveAuthorization authorization = new ReactiveAuthorization(
                WebClient.builder().exchangeFunction(exchangeFunction));
        ReflectionTestUtils.setField(authorization, "APIGATEWAY_HOST", "http://localhost:8000");
        ReflectionTestUtils.setField(authorization, "TOKEN_SECRET", "test-token-secret");
        return authorization;
    }

    private AuthorizationContext authorizationContext() {
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/user-service/api/v1/users")
                .build();
        return new AuthorizationContext(MockServerWebExchange.from(request));
    }
}
