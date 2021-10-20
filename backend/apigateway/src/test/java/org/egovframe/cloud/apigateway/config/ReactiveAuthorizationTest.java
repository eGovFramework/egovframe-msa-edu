package org.egovframe.cloud.apigateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
class ReactiveAuthorizationTest {

    @Value("${server.port}")
    private String PORT;

    @Test
    public void API요청시_토큰인증_만료된다() throws Exception {
        // given
        String baseUrl = "http://localhost:" + PORT;
        String notValidToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI2NWEwMGY2NS04NDYwLTQ5YWYtOThlYy0wNDI5NzdlNTZmNGIiLCJhdXRob3JpdGllcyI6IlJPTEVfVVNFUiIsImV4cCI6MTYyNjc4MjQ0N30.qiScvtr1m88SHPLpHqcJiklXFyIQ7WBJdiFcdcb2B8YSWC59QcdRRgMtXDGSZnjBgF194W-GRBpHUta6VCkrfQ";

        // when, then
        WebTestClient.bindToServer().baseUrl(baseUrl).defaultHeader(HttpHeaders.AUTHORIZATION, notValidToken).build()
                .get()
                .uri("/user-service/api/v1/users")
                .exchange().expectStatus().isUnauthorized()
        ;
    }
}