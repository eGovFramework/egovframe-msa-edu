package org.egovframe.cloud.portalservice.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.egovframe.cloud.portalservice.support.AuthorizedHttpTest;
import org.egovframe.cloud.portalservice.support.TestTokens;
import org.egovframe.cloud.portalservice.support.UserServiceAuthorizationStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;

/** 실제 보안 필터 → JWT 검증 → 서비스 디스커버리 → user-service 인가 HTTP 계약을 통과한다. */
class PortalAuthorizationHttpTest extends AuthorizedHttpTest {

    private static final String PATH = "/api/v1/codes";

    @BeforeEach
    void resetCalls() {
        UserServiceAuthorizationStub.clearCalls();
    }

    @Test
    void unauthenticatedRequestIsForbidden() {
        ResponseEntity<String> response = anonymousClient().getForEntity(PATH, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(UserServiceAuthorizationStub.calls()).anySatisfy(call -> {
            assertThat(call.httpMethod()).isEqualTo("GET");
            assertThat(call.requestPath()).isEqualTo("/portal-service" + PATH);
            assertThat(call.authorization()).isNull();
            assertThat(call.allowed()).isFalse();
        });
    }

    @Test
    void authenticatedUserWithoutAdminPermissionIsForbidden() {
        ResponseEntity<String> response = clientWithRole("portal-user", TestTokens.USER)
                .getForEntity(PATH, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(UserServiceAuthorizationStub.calls()).anySatisfy(call -> {
            assertThat(call.httpMethod()).isEqualTo("GET");
            assertThat(call.requestPath()).isEqualTo("/portal-service" + PATH);
            assertThat(call.authorization()).startsWith("Bearer ");
            assertThat(call.allowed()).isFalse();
        });
    }

    @Test
    void authorizedAdminReceivesJsonFromRealController() {
        ResponseEntity<JsonNode> response = clientWithRole("portal-admin", TestTokens.ADMIN)
                .getForEntity(PATH, JsonNode.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isNotNull();
        assertThat(response.getHeaders().getContentType().isCompatibleWith(MediaType.APPLICATION_JSON)).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().path("content").isArray()).isTrue();
        assertThat(response.getBody().path("totalElements").isNumber()).isTrue();
        assertThat(UserServiceAuthorizationStub.calls()).anySatisfy(call -> {
            assertThat(call.httpMethod()).isEqualTo("GET");
            assertThat(call.requestPath()).isEqualTo("/portal-service" + PATH);
            assertThat(call.authorization()).startsWith("Bearer ");
            assertThat(call.allowed()).isTrue();
        });
    }
}
