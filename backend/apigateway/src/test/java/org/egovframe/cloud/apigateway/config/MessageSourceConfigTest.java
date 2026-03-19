package org.egovframe.cloud.apigateway.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(profiles = "test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
class MessageSourceConfigTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void 메세지를_외부위치에서_읽어온다() {
        // when
        String message = restTemplate.getForObject("/api/v1/messages/common.login/ko", String.class);

        // then
        assertThat(message).isEqualTo("로그인");
    }
}