package org.egovframe.cloud.discovery;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * DiscoveryApplicationTests
 *
 * Discovery 애플리케이션 컨텍스트 로드 테스트 클래스
 *
 * @author 표준프레임워크센터
 * @version 1.0
 * @since 2024/01/01
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "eureka.client.register-with-eureka=false",
        "eureka.client.fetch-registry=false"
    }
)
@ActiveProfiles("test")
class DiscoveryApplicationTests {

    @Test
    void contextLoads() {
    }

}
