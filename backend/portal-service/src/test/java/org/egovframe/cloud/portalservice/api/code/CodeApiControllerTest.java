package org.egovframe.cloud.portalservice.api.code;

import static org.assertj.core.api.Assertions.assertThat;

import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.portalservice.api.code.dto.CodeResponseDto;
import org.egovframe.cloud.portalservice.api.code.dto.CodeSaveRequestDto;
import org.egovframe.cloud.portalservice.api.code.dto.CodeUpdateRequestDto;
import org.egovframe.cloud.portalservice.support.AuthorizedHttpTest;
import org.egovframe.cloud.portalservice.support.TestTokens;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * org.egovframe.cloud.portalservice.api.code.CodeApiControllerTest
 * <p>
 * 공통코드 CRUD 요청을 처리하는 REST API Controller 테스트
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/07/12
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/12    jaeyeolkim  최초 생성
 * </pre>
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@EnableConfigurationProperties
class CodeApiControllerTest extends AuthorizedHttpTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String TEST_CODE_ID = "/TEST";
    private static final String TEST_COM = "@test.com";
    private static final String TEST_EMAIL = System.currentTimeMillis() + TEST_COM;
    private static final String API_URL = "/api/v1/codes";

    // login 후 발급된 토큰 값이 입력된다
    private static String ACCESS_TOKEN = "";

    @Test
    @Order(Integer.MIN_VALUE)
    public void setup() throws Exception {
        // user-service 로그인 대신, AuthenticationFilter 가 검증하는 형식으로 서명한 관리자 JWT 를 발급한다
        ACCESS_TOKEN = TestTokens.bearer(TEST_EMAIL, TestTokens.ADMIN);

        // 발급한 토큰이 실제 보안 필터와 (user-service 스텁을 통한) 인가 위임을 통과하는지 확인한다
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN);
        ResponseEntity<String> responseEntity = restTemplate.exchange(API_URL, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.info("token ={}", ACCESS_TOKEN);
    }

    @Test
    @Order(Integer.MAX_VALUE)
    public void cleanup() throws Exception {
        // 테스트 후 데이터 삭제
        String url = API_URL + TEST_CODE_ID;
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        restTemplate.exchange(url, HttpMethod.DELETE, httpEntity, String.class);

        // TODO 사용자 데이터 삭제(어드민 유저 서비스)
    }

    @Test
    @Order(1)
    void 공통코드_저장된다() throws Exception {
        // given
//        String url = PORTAL_SERVICE_URL + API_URL;
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);

        CodeSaveRequestDto requestDto = CodeSaveRequestDto.builder()
                .codeId("TEST")
                .codeName("테스트")
                .codeDescription("테스트 공통코드입니다")
                .sortSeq(1)
                .useAt(true)
                .build();

        // when
        HttpEntity<String> httpEntity = new HttpEntity<>(new ObjectMapper().writeValueAsString(requestDto), headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(API_URL, HttpMethod.POST, httpEntity, String.class);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @Order(2)
    public void 공통코드_목록_검색어로_조회된다() throws Exception {
        // given
//        String url = PORTAL_SERVICE_URL + API_URL;
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);

        RequestDto requestDto = RequestDto.builder()
                .keywordType("codeId")
                .keyword("ES")
                .build();

        // when
        HttpEntity<String> httpEntity = new HttpEntity<>(new ObjectMapper().writeValueAsString(requestDto), headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(API_URL, HttpMethod.GET, httpEntity, String.class);
        log.info("responseEntity.getBody() ={}", responseEntity.getBody());

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).contains("pageable");
    }

    @Test
    @Order(3)
    public void 공통코드_단건_조회된다() throws Exception {
        // given
//        String url = PORTAL_SERVICE_URL + API_URL + TEST_CODE_ID;
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        // when
        ResponseEntity<CodeResponseDto> responseEntity = restTemplate.exchange(API_URL + TEST_CODE_ID, HttpMethod.GET, httpEntity, CodeResponseDto.class);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getCodeName()).isEqualTo("테스트");
    }

    @Test
    @Order(4)
    void 공통코드_수정된다() throws Exception {
        // given
//        String url = PORTAL_SERVICE_URL + API_URL + TEST_CODE_ID;
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);

        CodeUpdateRequestDto requestDto = CodeUpdateRequestDto.builder()
                .codeId("TEST")
                .codeName("테스트2")
                .codeDescription("테스트 공통코드입니다2")
                .sortSeq(2)
                .useAt(false)
                .build();

        // when
        HttpEntity<String> httpEntity = new HttpEntity<>(new ObjectMapper().writeValueAsString(requestDto), headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(API_URL + TEST_CODE_ID, HttpMethod.PUT, httpEntity, String.class);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(5)
    void 공통코드_사용여부_토글된다() throws Exception {
        // given
        String url = API_URL + TEST_CODE_ID + "/toggle-use?useAt=true";
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // when
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        log.info("url={}", url);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 수정된 데이터 확인
        url = API_URL + TEST_CODE_ID;
        ResponseEntity<CodeResponseDto> gerResponseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, CodeResponseDto.class);
        assertThat(gerResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(gerResponseEntity.getBody().getUseAt()).isTrue();
    }

}