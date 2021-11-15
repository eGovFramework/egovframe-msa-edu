package org.egovframe.cloud.apigateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles(profiles = "test")
class ReactiveAuthorizationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void API요청시_토큰인증_만료된다() throws Exception {
        // given
        String notValidToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI2NWEwMGY2NS04NDYwLTQ5YWYtOThlYy0wNDI5NzdlNTZmNGIiLCJhdXRob3JpdGllcyI6IlJPTEVfVVNFUiIsImV4cCI6MTYyNjc4MjQ0N30.qiScvtr1m88SHPLpHqcJiklXFyIQ7WBJdiFcdcb2B8YSWC59QcdRRgMtXDGSZnjBgF194W-GRBpHUta6VCkrfQ";

        // when, then
        webTestClient
                .get()
                .uri("/user-service/api/v1/users")
                .header(HttpHeaders.AUTHORIZATION, notValidToken)
                .exchange().expectStatus().isUnauthorized()
        ;
    }
}