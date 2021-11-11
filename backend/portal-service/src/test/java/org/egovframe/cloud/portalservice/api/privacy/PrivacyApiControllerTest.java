package org.egovframe.cloud.portalservice.api.privacy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.assertj.core.api.Condition;
import org.egovframe.cloud.portalservice.api.privacy.dto.PrivacyListResponseDto;
import org.egovframe.cloud.portalservice.api.privacy.dto.PrivacyResponseDto;
import org.egovframe.cloud.portalservice.domain.privacy.Privacy;
import org.egovframe.cloud.portalservice.domain.privacy.PrivacyRepository;
import org.egovframe.cloud.portalservice.util.RestResponsePage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * org.egovframe.cloud.portalservice.api.privacy.PrivacyApiControllerTest
 *
 * 개인정보처리방침 Rest API 컨트롤러 테스트 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/23
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/23    jooho       최초 생성
 * </pre>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles(profiles = "test")
class PrivacyApiControllerTest {

    /**
     * test rest template
     */
    @Autowired
    TestRestTemplate restTemplate;

    /**
     * 개인정보처리방침 레파지토리 인터페이스
     */
    @Autowired
    PrivacyRepository privacyRepository;

    /**
     * 개인정보처리방침 API 경로
     */
    private static final String URL = "/api/v1/privacies";

    /**
     * 테스트 데이터 등록 횟수
     */
    private final Integer GIVEN_DATA_COUNT = 10;

    /**
     * 테스트 데이터
     */
    private final String CONTENT_TITLE_PREFIX = "개인정보처리방침 제목";
    private final String CONTENT_CONTENT_PREFIX = "개인정보처리방침 내용";

    private final Integer CONTENT_NO = GIVEN_DATA_COUNT + 1;
    private final String INSERT_CONTENT_TITLE = CONTENT_TITLE_PREFIX + "_" + CONTENT_NO;
    private final String INSERT_CONTENT_CONTENT = CONTENT_CONTENT_PREFIX + "_" + CONTENT_NO;
    private final Boolean INSERT_USE_AT = true;

    private final String UPDATE_CONTENT_TITLE = CONTENT_TITLE_PREFIX + "_" + (CONTENT_NO + 1);
    private final String UPDATE_CONTENT_CONTENT = CONTENT_CONTENT_PREFIX + "_" + (CONTENT_NO + 1);
    private final Boolean UPDATE_USE_AT = false;

    /**
     * 테스트 데이터
     */
    private final List<Privacy> privacies = new ArrayList<>();

    /**
     * 테스트 시작 전 수행
     */
    @BeforeEach
    void setUp() {
    }

    /**
     * 테스트 종료 후 수행
     */
    @AfterEach
    void tearDown() {
        //개인정보처리방침 삭제
        privacyRepository.deleteAll();
    }

    /**
     * 개인정보처리방침 페이지 목록 조회 테스트
     */
    @Test
    void 개인정보처리방침_페이지_목록_조회() {
        // given
        insertPrivacies();

        String queryString = "?keywordType=privacyTitle&keyword=" + CONTENT_TITLE_PREFIX; // 검색 조건
        queryString += "&page=0&size=" + GIVEN_DATA_COUNT; // 페이지 정보

        // when
        ResponseEntity<RestResponsePage<PrivacyListResponseDto>> responseEntity = restTemplate.exchange(
                URL + queryString,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<RestResponsePage<PrivacyListResponseDto>>() {
                }
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        RestResponsePage<PrivacyListResponseDto> page = responseEntity.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getNumberOfElements()).isEqualTo(GIVEN_DATA_COUNT);
        assertThat(page.getContent())
                .isNotEmpty()
                .has(new Condition<>(l -> (CONTENT_TITLE_PREFIX + "_10").equals(l.get(0).getPrivacyTitle()), "PrivacyApiControllerTest.findPage contains " + CONTENT_TITLE_PREFIX + "_10"))
                .has(new Condition<>(l -> (CONTENT_TITLE_PREFIX + "_9").equals(l.get(1).getPrivacyTitle()), "PrivacyApiControllerTest.findPage contains " + CONTENT_TITLE_PREFIX + "_9"));

        deletePrivacies();
    }

    /**
     * 개인정보처리방침 사용중 전체 목록 조회 테스트
     */
    @Test
    void 개인정보처리방침_사용중_전체_목록_조회() {
        // given
        insertPrivacies();

        // when
        ResponseEntity<List<PrivacyListResponseDto>> responseEntity = restTemplate.exchange(
                URL + "/all/use",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<PrivacyListResponseDto>>() {
                }
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<PrivacyListResponseDto> list = responseEntity.getBody();
        assertThat(list).isNotNull();
        assertThat(list.size()).isEqualTo(GIVEN_DATA_COUNT / 2);
        assertThat(list)
                .isNotEmpty()
                .has(new Condition<>(l -> (CONTENT_TITLE_PREFIX + "_9").equals(l.get(0).getPrivacyTitle()), "PrivacyApiControllerTest.findAllByUseAtOrderByPrivacyNoDesc contains " + CONTENT_TITLE_PREFIX + "_9"))
                .has(new Condition<>(l -> (CONTENT_TITLE_PREFIX + "_7").equals(l.get(1).getPrivacyTitle()), "PrivacyApiControllerTest.findAllByUseAtOrderByPrivacyNoDesc contains " + CONTENT_TITLE_PREFIX + "_7"));

        deletePrivacies();
    }

    /**
     * 개인정보처리방침 상세 조회 테스트
     */
    @Test
    void 개인정보처리방침_상세_조회() {
        // given
        Privacy entity = insertPrivacy();

        final Integer privacyNo = entity.getPrivacyNo();

        String url = URL + "/" + privacyNo;

        // when
        ResponseEntity<PrivacyResponseDto> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PrivacyResponseDto>() {
                }
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        PrivacyResponseDto dto = responseEntity.getBody();
        assertThat(dto).isNotNull();
        assertThat(dto.getPrivacyNo()).isEqualTo(privacyNo);
        assertThat(dto.getPrivacyTitle()).isEqualTo(INSERT_CONTENT_TITLE);
        assertThat(dto.getPrivacyContent()).isEqualTo(INSERT_CONTENT_CONTENT);
        assertThat(dto.getUseAt()).isEqualTo(INSERT_USE_AT);

        deletePrivacy(privacyNo);
    }

    /**
     * 개인정보처리방침 등록 테스트
     */
    @Test
    void 개인정보처리방침_등록() {
        // given
        Map<String, Object> params = new HashMap<>();
        params.put("privacyTitle", INSERT_CONTENT_TITLE);
        params.put("privacyContent", INSERT_CONTENT_CONTENT);
        params.put("useAt", INSERT_USE_AT);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(params);

        // when
        //ResponseEntity<BoardResponseDto> responseEntity = restTemplate.postForEntity(URL, requestDto, BoardResponseDto.class);
        ResponseEntity<PrivacyResponseDto> responseEntity = restTemplate.exchange(
                URL,
                HttpMethod.POST,
                httpEntity,
                new ParameterizedTypeReference<PrivacyResponseDto>() {
                }
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        PrivacyResponseDto dto = responseEntity.getBody();
        assertThat(dto).isNotNull();

        final Integer privacyNo = dto.getPrivacyNo();

        Optional<Privacy> privacy = selectData(privacyNo);
        assertThat(privacy).isPresent();

        Privacy entity = privacy.get();
        assertThat(entity.getPrivacyNo()).isEqualTo(privacyNo);
        assertThat(entity.getPrivacyTitle()).isEqualTo(INSERT_CONTENT_TITLE);
        assertThat(entity.getPrivacyContent()).isEqualTo(INSERT_CONTENT_CONTENT);
        assertThat(entity.getUseAt()).isEqualTo(INSERT_USE_AT);

        deletePrivacy(privacyNo);
    }

    /**
     * 개인정보처리방침 수정 테스트
     */
    @Test
    void 개인정보처리방침_수정() {
        // given
        Privacy entity = insertPrivacy();

        final Integer privacyNo = entity.getPrivacyNo();

        Map<String, Object> params = new HashMap<>();
        params.put("privacyTitle", UPDATE_CONTENT_TITLE);
        params.put("privacyContent", UPDATE_CONTENT_CONTENT);
        params.put("useAt", UPDATE_USE_AT);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(params);

        String url = URL + "/" + privacyNo;

        // when
        ResponseEntity<PrivacyResponseDto> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                httpEntity,
                new ParameterizedTypeReference<PrivacyResponseDto>() {
                }
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        PrivacyResponseDto dto = responseEntity.getBody();
        assertThat(dto).isNotNull();

        Optional<Privacy> privacy = selectData(privacyNo);
        assertThat(privacy).isPresent();

        Privacy updatedPrivacy = privacy.get();
        assertThat(updatedPrivacy.getPrivacyNo()).isEqualTo(privacyNo);
        assertThat(updatedPrivacy.getPrivacyTitle()).isEqualTo(UPDATE_CONTENT_TITLE);
        assertThat(updatedPrivacy.getPrivacyContent()).isEqualTo(UPDATE_CONTENT_CONTENT);
        assertThat(updatedPrivacy.getUseAt()).isEqualTo(UPDATE_USE_AT);

        deletePrivacy(privacyNo);
    }

    /**
     * 개인정보처리방침 사용 여부 수정 테스트
     */
    @Test
    void 개인정보처리방침_사용여부_수정() {
        // given
        Privacy entity = insertPrivacy();

        final Integer privacyNo = entity.getPrivacyNo();

        String url = URL + "/" + privacyNo + "/" + UPDATE_USE_AT;

        // when
        ResponseEntity<PrivacyResponseDto> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                null,
                new ParameterizedTypeReference<PrivacyResponseDto>() {
                }
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        PrivacyResponseDto dto = responseEntity.getBody();
        assertThat(dto).isNotNull();

        Optional<Privacy> privacy = selectData(privacyNo);
        assertThat(privacy).isPresent();

        Privacy updatedPrivacy = privacy.get();
        assertThat(updatedPrivacy.getPrivacyNo()).isEqualTo(privacyNo);
        assertThat(updatedPrivacy.getUseAt()).isEqualTo(UPDATE_USE_AT);

        deletePrivacy(privacyNo);
    }

    /**
     * 개인정보처리방침 삭제 테스트
     */
    @Test
    void 개인정보처리방침_삭제() {
        // given
        Privacy entity = insertPrivacy();

        final Integer privacyNo = entity.getPrivacyNo();

        String url = URL + "/" + privacyNo;

        // when
        ResponseEntity<PrivacyResponseDto> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                null,
                PrivacyResponseDto.class
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Optional<Privacy> privacy = selectData(privacyNo);
        assertThat(privacy).isNotPresent();
    }

    /**
     * 테스트 데이터 등록
     */
    private void insertPrivacies() {
        for (int i = 1; i <= GIVEN_DATA_COUNT; i++) {
            privacies.add(privacyRepository.save(Privacy.builder()
                    .privacyTitle(CONTENT_TITLE_PREFIX + "_" + i)
                    .privacyContent(CONTENT_CONTENT_PREFIX + "_" + i)
                    .useAt(i % 2 == 1)
                    .build()));
        }
    }

    /**
     * 테스트 데이터 삭제
     */
    private void deletePrivacies() {
        privacyRepository.deleteAll(privacies);

        privacies.clear();
    }

    /**
     * 테스트 데이터 단건 등록
     *
     * @return Privacy 개인정보처리방침 엔티티
     */
    private Privacy insertPrivacy() {
        return privacyRepository.save(Privacy.builder()
                .privacyTitle(INSERT_CONTENT_TITLE)
                .privacyContent(INSERT_CONTENT_CONTENT)
                .useAt(INSERT_USE_AT)
                .build());
    }

    /**
     * 테스트 데이터 단건 삭제
     */
    private void deletePrivacy(Integer privacyNo) {
        privacyRepository.deleteById(privacyNo);
    }

    /**
     * 테스트 데이터 단건 조회
     *
     * @param privacyNo 개인정보처리방침 번호
     * @return Optional<Privacy> 개인정보처리방침 엔티티
     */
    private Optional<Privacy> selectData(Integer privacyNo) {
        return privacyRepository.findById(privacyNo);
    }

}