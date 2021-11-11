package org.egovframe.cloud.portalservice.api.content;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.assertj.core.api.Condition;
import org.egovframe.cloud.portalservice.api.content.dto.ContentListResponseDto;
import org.egovframe.cloud.portalservice.api.content.dto.ContentResponseDto;
import org.egovframe.cloud.portalservice.domain.content.Content;
import org.egovframe.cloud.portalservice.domain.content.ContentRepository;
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
 * org.egovframe.cloud.portalservice.api.content.ContentApiControllerTest
 * <p>
 * 컨텐츠 Rest API 컨트롤러 테스트 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/23
 *
 *        <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/23    jooho       최초 생성
 *        </pre>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles(profiles = "test")
class ContentApiControllerTest {

	/**
	 * test rest template
	 */
	@Autowired
	TestRestTemplate restTemplate;

	/**
	 * 컨텐츠 레파지토리 인터페이스
	 */
	@Autowired
	ContentRepository contentRepository;

	/**
	 * 컨텐츠 API 경로
	 */
	private static final String URL = "/api/v1/contents";

	/**
	 * 테스트 데이터 등록 횟수
	 */
	private final Integer GIVEN_DATA_COUNT = 10;

	/**
	 * 테스트 데이터
	 */
	private final String CONTENT_NAME_PREFIX = "컨텐츠 명";
	private final String CONTENT_REMARK_PREFIX = "컨텐츠 비고";
	private final String CONTENT_VALUE_PREFIX = "컨텐츠 값";

	private final Integer CONTENT_NO = GIVEN_DATA_COUNT + 1;
	private final String INSERT_CONTENT_NAME = CONTENT_NAME_PREFIX + "_" + CONTENT_NO;
	private final String INSERT_CONTENT_REMARK = CONTENT_REMARK_PREFIX + "_" + CONTENT_NO;
	private final String INSERT_CONTENT_VALUE = CONTENT_VALUE_PREFIX + "_" + CONTENT_NO;

	private final String UPDATE_CONTENT_NAME = CONTENT_NAME_PREFIX + "_" + (CONTENT_NO + 1);
	private final String UPDATE_CONTENT_REMARK = CONTENT_REMARK_PREFIX + "_" + (CONTENT_NO + 1);
	private final String UPDATE_CONTENT_VALUE = CONTENT_VALUE_PREFIX + "_" + (CONTENT_NO + 1);

	/**
	 * 테스트 데이터
	 */
	private final List<Content> contents = new ArrayList<>();

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
		// 컨텐츠 삭제
		contentRepository.deleteAll();
	}

	/**
	 * 컨텐츠 페이지 목록 조회 테스트
	 */
	@Test
	void 컨텐츠_페이지_목록_조회() {
		// given
		insertContents();

		String queryString = "?keywordType=contentName&keyword=" + CONTENT_NAME_PREFIX; // 검색 조건
		queryString += "&page=0&size=" + GIVEN_DATA_COUNT; // 페이지 정보

		// when
		ResponseEntity<RestResponsePage<ContentListResponseDto>> responseEntity = restTemplate.exchange(
				URL + queryString, HttpMethod.GET, null,
				new ParameterizedTypeReference<RestResponsePage<ContentListResponseDto>>() {
				});

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

		RestResponsePage<ContentListResponseDto> page = responseEntity.getBody();
		assertThat(page).isNotNull();
		assertThat(page.getNumberOfElements()).isEqualTo(GIVEN_DATA_COUNT);
		assertThat(page.getContent()).isNotEmpty()
				.has(new Condition<>(l -> (CONTENT_NAME_PREFIX + "_10").equals(l.get(0).getContentName()),
						"ContentApiControllerTest.findPage contains " + CONTENT_NAME_PREFIX + "_10"))
				.has(new Condition<>(l -> (CONTENT_NAME_PREFIX + "_9").equals(l.get(1).getContentName()),
						"ContentApiControllerTest.findPage contains " + CONTENT_NAME_PREFIX + "_9"));

		deleteContents();
	}

	/**
	 * 컨텐츠 상세 조회 테스트
	 */
	@Test
	void 컨텐츠_상세_조회() {
		// given
		Content entity = insertContent();

		final Integer contentNo = entity.getContentNo();

		String url = URL + "/" + contentNo;

		// when
		ResponseEntity<ContentResponseDto> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null,
				new ParameterizedTypeReference<ContentResponseDto>() {
				});

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

		ContentResponseDto dto = responseEntity.getBody();
		assertThat(dto).isNotNull();
		assertThat(dto.getContentNo()).isEqualTo(contentNo);
		assertThat(dto.getContentName()).isEqualTo(INSERT_CONTENT_NAME);
		assertThat(dto.getContentRemark()).isEqualTo(INSERT_CONTENT_REMARK);
		assertThat(dto.getContentValue()).isEqualTo(INSERT_CONTENT_VALUE);

		deleteContent(contentNo);
	}

	/**
	 * 컨텐츠 등록 테스트
	 */
	@Test
	void 컨텐츠_등록() {
		// given
		Map<String, Object> params = new HashMap<>();
		params.put("contentName", INSERT_CONTENT_NAME);
		params.put("contentRemark", INSERT_CONTENT_REMARK);
		params.put("contentValue", INSERT_CONTENT_VALUE);
		HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(params);

		// when
		// ResponseEntity<BoardResponseDto> responseEntity =
		// restTemplate.postForEntity(URL, requestDto, BoardResponseDto.class);
		ResponseEntity<ContentResponseDto> responseEntity = restTemplate.exchange(URL, HttpMethod.POST, httpEntity,
				new ParameterizedTypeReference<ContentResponseDto>() {
				});

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		ContentResponseDto dto = responseEntity.getBody();
		assertThat(dto).isNotNull();

		final Integer contentNo = dto.getContentNo();

		Optional<Content> content = selectData(contentNo);
		assertThat(content).isPresent();

		Content entity = content.get();
		assertThat(entity.getContentNo()).isEqualTo(contentNo);
		assertThat(entity.getContentName()).isEqualTo(INSERT_CONTENT_NAME);
		assertThat(entity.getContentRemark()).isEqualTo(INSERT_CONTENT_REMARK);
		assertThat(entity.getContentValue()).isEqualTo(INSERT_CONTENT_VALUE);

		deleteContent(contentNo);
	}

	/**
	 * 컨텐츠 수정 테스트
	 */
	@Test
	void 컨텐츠_수정() {
		// given
		Content entity = insertContent();

		final Integer contentNo = entity.getContentNo();

		Map<String, Object> params = new HashMap<>();
		params.put("contentName", UPDATE_CONTENT_NAME);
		params.put("contentRemark", UPDATE_CONTENT_REMARK);
		params.put("contentValue", UPDATE_CONTENT_VALUE);
		HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(params);

		String url = URL + "/" + contentNo;

		// when
		ResponseEntity<ContentResponseDto> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, httpEntity,
				new ParameterizedTypeReference<ContentResponseDto>() {
				});

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

		ContentResponseDto dto = responseEntity.getBody();
		assertThat(dto).isNotNull();

		Optional<Content> content = selectData(contentNo);
		assertThat(content).isPresent();

		Content updatedContent = content.get();
		assertThat(updatedContent.getContentNo()).isEqualTo(contentNo);
		assertThat(updatedContent.getContentName()).isEqualTo(UPDATE_CONTENT_NAME);
		assertThat(updatedContent.getContentRemark()).isEqualTo(UPDATE_CONTENT_REMARK);
		assertThat(updatedContent.getContentValue()).isEqualTo(UPDATE_CONTENT_VALUE);

		deleteContent(contentNo);
	}

	/**
	 * 컨텐츠 삭제 테스트
	 */
	@Test
	void 컨텐츠_삭제() {
		// given
		Content entity = insertContent();

		final Integer contentNo = entity.getContentNo();

		String url = URL + "/" + contentNo;

		// when
		ResponseEntity<ContentResponseDto> responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, null,
				ContentResponseDto.class);

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		Optional<Content> content = selectData(contentNo);
		assertThat(content).isNotPresent();
	}

	/**
	 * 테스트 데이터 등록
	 */
	private void insertContents() {
		for (int i = 1; i <= GIVEN_DATA_COUNT; i++) {
			contents.add(contentRepository.save(Content.builder().contentName(CONTENT_NAME_PREFIX + "_" + i)
					.contentRemark(CONTENT_REMARK_PREFIX + "_" + i).contentValue(CONTENT_VALUE_PREFIX + "_" + i)
					.build()));
		}
	}

	/**
	 * 테스트 데이터 삭제
	 */
	private void deleteContents() {
		contentRepository.deleteAll(contents);

		contents.clear();
	}

	/**
	 * 테스트 데이터 단건 등록
	 *
	 * @return Content 컨텐츠 엔티티
	 */
	private Content insertContent() {
		return contentRepository.save(Content.builder().contentName(INSERT_CONTENT_NAME)
				.contentRemark(INSERT_CONTENT_REMARK).contentValue(INSERT_CONTENT_VALUE).build());
	}

	/**
	 * 테스트 데이터 단건 삭제
	 */
	private void deleteContent(Integer contentNo) {
		contentRepository.deleteById(contentNo);
	}

	/**
	 * 테스트 데이터 단건 조회
	 *
	 * @param contentNo 컨텐츠 번호
	 * @return Optional<Content> 컨텐츠 엔티티
	 */
	private Optional<Content> selectData(Integer contentNo) {
		return contentRepository.findById(contentNo);
	}

}