package org.egovframe.cloud.discovery;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

/**
 * SecurityConfigTest
 *
 * Spring Security 설정 단위 테스트 클래스
 *
 * @author 표준프레임워크센터
 * @version 1.0
 * @since 2024/01/01
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.security.user.name=admin",
        "spring.security.user.password=admin",
        "eureka.client.register-with-eureka=false",
        "eureka.client.fetch-registry=false"
    }
)
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @DisplayName("Actuator 헬스체크 경로는 인증 없이 허용되어야 한다")
    @Test
    void should_allowActuatorHealth_without_authentication() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health", String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @DisplayName("인증 정보가 없으면 루트 경로는 401 Unauthorized를 반환해야 한다")
    @Test
    void should_returnUnauthorized_for_root_without_authentication() {
        ResponseEntity<String> response = restTemplate.getForEntity("/", String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @DisplayName("올바른 Basic Auth 인증 정보가 있으면 요청이 허용되어야 한다")
    @Test
    void should_allowRequest_with_validBasicAuth() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity("/", String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @DisplayName("잘못된 Basic Auth 인증 정보가 있으면 401 Unauthorized를 반환해야 한다")
    @Test
    void should_returnUnauthorized_with_invalidBasicAuth() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("admin", "wrongpassword")
                .getForEntity("/", String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
