package org.egovframe.cloud.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Config Server 애플리케이션의 Actuator 엔드포인트가 정상적으로 동작하는지 검증한다.<br>
 * 테스트마다 임의의 포트로 애플리케이션을 실행하여 실제 HTTP 요청과 응답을 확인한다.<br>
 *
 * @author 공통컴포넌트 개발팀 홍길동
 * @since 2026-09-08
 * @version 5.0.1
 * @see
 *
 *      <pre>
 *  == 개정이력(Modification Information) ==
 *
 *   수정일      수정자           수정내용
 *  -------    --------    ---------------------------
 *   2026-09-08  이백행          [2026년 컨트리뷰션] 최초 생성
 *      </pre>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConfigApplicationTests {

	/** 테스트 서버에 HTTP 요청을 전송하기 위한 REST 클라이언트 */
	@Autowired
	private TestRestTemplate restTemplate;

//	@Test
//	void contextLoads() {
//	}

	/**
	 * 헬스 체크 엔드포인트가 정상 상태(UP)를 반환하는지 검증한다.
	 */
	@Test
	void healthEndpointReturnsUp() {
		ResponseEntity<JsonNode> response = restTemplate.getForEntity("/actuator/health", JsonNode.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().path("status").asText()).isEqualTo("UP");
	}

	/**
	 * 애플리케이션 정보 엔드포인트에 정상적으로 접근할 수 있는지 검증한다.
	 */
	@Test
	void infoEndpointIsAvailable() {
		ResponseEntity<JsonNode> response = restTemplate.getForEntity("/actuator/info", JsonNode.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
	}

}
