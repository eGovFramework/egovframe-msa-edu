package org.egovframe.cloud.portalservice.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles(profiles = "test")
class MessageSourceConfigTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    public void 메세지를_외부위치에서_읽어온다() throws Exception {
        // when
        String message = restTemplate.getForObject("/api/v1/messages/common.login/ko", String.class);

        // then
        assertThat(message).isEqualTo("로그인");
    }
}