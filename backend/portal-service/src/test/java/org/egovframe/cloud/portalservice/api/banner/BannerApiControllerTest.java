package org.egovframe.cloud.portalservice.api.banner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.assertj.core.api.Condition;
import org.egovframe.cloud.portalservice.api.banner.dto.BannerImageResponseDto;
import org.egovframe.cloud.portalservice.api.banner.dto.BannerListResponseDto;
import org.egovframe.cloud.portalservice.api.banner.dto.BannerResponseDto;
import org.egovframe.cloud.portalservice.domain.banner.Banner;
import org.egovframe.cloud.portalservice.domain.banner.BannerRepository;
import org.egovframe.cloud.portalservice.domain.menu.Site;
import org.egovframe.cloud.portalservice.domain.menu.SiteRepository;
import org.egovframe.cloud.portalservice.service.banner.BannerService;
import org.egovframe.cloud.portalservice.util.RestResponsePage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
 * org.egovframe.cloud.portalservice.api.banner.BannerApiControllerTest
 * <p>
 * 배너 Rest API 컨트롤러 테스트 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/08/18
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *    수정일       수정자              수정내용
 *  ----------    --------    ---------------------------
 *  2021/08/18    jooho       최초 생성
 * </pre>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles(profiles = "test")
class BannerApiControllerTest {

    /**
     * test rest template
     */
    @Autowired
    TestRestTemplate restTemplate;

    /**
     * 배너 레파지토리 인터페이스
     */
    @Autowired
    BannerRepository bannerRepository;

    /**
     * 사이트 레파지토리 인터페이스
     */
    @Autowired
    SiteRepository siteRepository;

    /**
     * 배너 API 경로
     */
    private static final String URL = "/api/v1/banners";

    /**
     * 테스트 데이터 등록 횟수
     */
    private final Integer GIVEN_DATA_COUNT = 10;

    /**
     * 테스트 데이터
     */
    private final String BANNER_TYPE_CODE_PREFIX = "000";
    private final String BANNER_TITLE_PREFIX = "배너 제목";
    private final String ATTACHMENT_CODE_PREFIX = "000000000";
    private final String URL_ADDR_PREFIX = "http://localhost:8000";
    private final String BANNER_CONTENT_PREFIX = "배너 내용";

    private final Integer BANNER_NO = GIVEN_DATA_COUNT + 1;
    private final String INSERT_BANNER_TYPE_CODE = BANNER_TYPE_CODE_PREFIX + BANNER_NO;
    private final String INSERT_BANNER_TITLE = BANNER_TITLE_PREFIX + "_" + BANNER_NO;
    private final String INSERT_ATTACHMENT_CODE = ATTACHMENT_CODE_PREFIX + BANNER_NO;
    private final String INSERT_URL_ADDR = URL_ADDR_PREFIX + BANNER_NO;
    private final String INSERT_BANNER_CONTENT = BANNER_CONTENT_PREFIX + "_" + BANNER_NO;
    private final Boolean INSERT_NEW_WINDOW_AT = true;
    private final Integer INSERT_SORT_SEQ = 1;

    private final String UPDATE_BANNER_TYPE_CODE = BANNER_TYPE_CODE_PREFIX + (BANNER_NO + 1);
    private final String UPDATE_BANNER_TITLE = BANNER_TITLE_PREFIX + "_" + (BANNER_NO + 1);
    private final String UPDATE_ATTACHMENT_CODE = ATTACHMENT_CODE_PREFIX + (BANNER_NO + 1);
    private final String UPDATE_URL_ADDR = URL_ADDR_PREFIX + (BANNER_NO + 1);
    private final String UPDATE_BANNER_CONTENT = BANNER_CONTENT_PREFIX + "_" + (BANNER_NO + 1);
    private final Boolean UPDATE_NEW_WINDOW_AT = true;
    private final Integer UPDATE_SORT_SEQ = 2;

    /**
     * 테스트 데이터
     */
    private Site site;
    private final List<Banner> banners = new ArrayList<>();

    /**
     * 테스트 시작 전 수행
     */
    @BeforeEach
    void setUp() {
    	// 사이트 등록
    	site = siteRepository.save(Site.builder()
    			.name("TEST_SITE")
    			.isUse(true)
    			.sortSeq(1)
    			.build());
    }

    /**
     * 테스트 종료 후 수행
     */
    @AfterEach
    void tearDown() {
        // 배너 삭제
        bannerRepository.deleteAll();
        banners.clear();

        // 사이트 삭제
        siteRepository.deleteAll();
    }

    /**
     * 배너 페이지 목록 조회 테스트
     */
    @Test
    void 배너_페이지_목록_조회() {
        // given
        insertBanners();

        String queryString = "?keywordType=bannerName&keyword=" + BANNER_TITLE_PREFIX; // 검색 조건
        queryString += "&page=0&size=" + GIVEN_DATA_COUNT; // 페이지 정보

        // when
        ResponseEntity<RestResponsePage<BannerListResponseDto>> responseEntity = restTemplate.exchange(
                URL + queryString,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<RestResponsePage<BannerListResponseDto>>() {
                }
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        RestResponsePage<BannerListResponseDto> page = responseEntity.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getNumberOfElements()).isEqualTo(GIVEN_DATA_COUNT);
        assertThat(page.getContent())
                .isNotEmpty()
                .has(new Condition<>(l -> (BANNER_TITLE_PREFIX + "_1").equals(l.get(0).getBannerTitle()), "BannerApiControllerTest.findPage contains " + BANNER_TITLE_PREFIX + "_1"))
                .has(new Condition<>(l -> (BANNER_TITLE_PREFIX + "_2").equals(l.get(1).getBannerTitle()), "BannerApiControllerTest.findPage contains " + BANNER_TITLE_PREFIX + "_2"));
    }

    /**
     * 유형별 배너 목록 조회 테스트
     */
    @Test
    void 유형별_배너_목록_조회() {
        // given
        insertBanners();

        // when
        ResponseEntity<Map<String, List<BannerImageResponseDto>>> responseEntity = restTemplate.exchange(
                "/api/v1/" + site.getId() + "/banners/0001,0002/3",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, List<BannerImageResponseDto>>>() {
                }
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map<String, List<BannerImageResponseDto>> data = responseEntity.getBody();
        assertThat(data).isNotNull();
        assertThat(data.get("0001").size()).isEqualTo(1); // 사용중인 0001타입 배너 1개
    }

    /**
     * 배너 상세 조회 테스트
     */
    @Test
    void 배너_상세_조회() {
        // given
        Banner entity = insertBanner();

        final Integer bannerNo = entity.getBannerNo();

        String url = URL + "/" + bannerNo;

        // when
        ResponseEntity<BannerResponseDto> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<BannerResponseDto>() {
                }
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        BannerResponseDto dto = responseEntity.getBody();
        assertThat(dto).isNotNull();
        assertThat(dto.getSiteId()).isEqualTo(site.getId());
        assertThat(dto.getBannerNo()).isEqualTo(bannerNo);
        assertThat(dto.getBannerTypeCode()).isEqualTo(INSERT_BANNER_TYPE_CODE);
        assertThat(dto.getBannerTitle()).isEqualTo(INSERT_BANNER_TITLE);
        assertThat(dto.getAttachmentCode()).isEqualTo(INSERT_ATTACHMENT_CODE);
        assertThat(dto.getUrlAddr()).isEqualTo(INSERT_URL_ADDR);
        assertThat(dto.getBannerContent()).isEqualTo(INSERT_BANNER_CONTENT);
    }

    /**
     * 배너 다음 정렬 순서 조회 테스트
     */
    @Test
    void 배너_다음_정렬_순서_조회() {
        // given
        Banner entity = insertBanner();

        String url = URL + "/" + site.getId() + "/sort-seq/next";

        // when
        ResponseEntity<Integer> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Integer>() {
                }
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        Integer nextSortSeq = responseEntity.getBody();
        assertThat(nextSortSeq).isNotNull();
        assertThat(nextSortSeq).isEqualTo(entity.getSortSeq() + 1);
    }

    /**
     * 배너 등록 테스트
     */
    @Test
    @Disabled
    void 배너_등록() {
        // given
        Map<String, Object> params = new HashMap<>();
        params.put("siteId", site.getId());
        params.put("bannerTypeCode", INSERT_BANNER_TYPE_CODE);
        params.put("bannerTitle", INSERT_BANNER_TITLE);
        params.put("attachmentCode", INSERT_ATTACHMENT_CODE);
        params.put("urlAddr", INSERT_URL_ADDR);
        params.put("bannerContent", INSERT_BANNER_CONTENT);
        params.put("newWindowAt", INSERT_NEW_WINDOW_AT);
        params.put("sortSeq", INSERT_SORT_SEQ);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(params);

        // when
        //ResponseEntity<BoardResponseDto> responseEntity = restTemplate.postForEntity(URL, requestDto, BoardResponseDto.class);
        ResponseEntity<BannerResponseDto> responseEntity = restTemplate.exchange(
                URL,
                HttpMethod.POST,
                httpEntity,
                new ParameterizedTypeReference<BannerResponseDto>() {
                }
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        BannerResponseDto dto = responseEntity.getBody();
        assertThat(dto).isNotNull();

        final Integer bannerNo = dto.getBannerNo();

        Optional<Banner> banner = selectData(bannerNo);
        assertThat(banner).isPresent();

        Banner entity = banner.get();
        assertThat(entity.getSite().getId()).isEqualTo(site.getId());
        assertThat(entity.getBannerNo()).isEqualTo(bannerNo);
        assertThat(entity.getBannerTypeCode()).isEqualTo(INSERT_BANNER_TYPE_CODE);
        assertThat(entity.getBannerTitle()).isEqualTo(INSERT_BANNER_TITLE);
        assertThat(entity.getAttachmentCode()).isEqualTo(INSERT_ATTACHMENT_CODE);
        assertThat(entity.getUrlAddr()).isEqualTo(INSERT_URL_ADDR);
        assertThat(entity.getBannerContent()).isEqualTo(INSERT_BANNER_CONTENT);
        assertThat(entity.getNewWindowAt()).isEqualTo(INSERT_NEW_WINDOW_AT);
        assertThat(entity.getSortSeq()).isEqualTo(INSERT_SORT_SEQ);
    }

    /**
     * 배너 수정 테스트
     */
    @Test
    void 배너_수정() {
        // given
        Banner entity = insertBanner();

        final Integer bannerNo = entity.getBannerNo();

        Map<String, Object> params = new HashMap<>();
        params.put("siteId", site.getId());
        params.put("bannerTypeCode", UPDATE_BANNER_TYPE_CODE);
        params.put("bannerTitle", UPDATE_BANNER_TITLE);
        params.put("attachmentCode", UPDATE_ATTACHMENT_CODE);
        params.put("urlAddr", UPDATE_URL_ADDR);
        params.put("bannerContent", UPDATE_BANNER_CONTENT);
        params.put("newWindowAt", UPDATE_NEW_WINDOW_AT);
        params.put("sortSeq", UPDATE_SORT_SEQ);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(params);

        String url = URL + "/" + bannerNo;

        // when
        ResponseEntity<BannerResponseDto> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                httpEntity,
                new ParameterizedTypeReference<BannerResponseDto>() {
                }
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        BannerResponseDto dto = responseEntity.getBody();
        assertThat(dto).isNotNull();

        Optional<Banner> banner = selectData(bannerNo);
        assertThat(banner).isPresent();

        Banner updatedBanner = banner.get();
        assertThat(updatedBanner.getSite().getId()).isEqualTo(site.getId());
        assertThat(updatedBanner.getBannerNo()).isEqualTo(bannerNo);
        assertThat(updatedBanner.getBannerTypeCode()).isEqualTo(UPDATE_BANNER_TYPE_CODE);
        assertThat(updatedBanner.getBannerTitle()).isEqualTo(UPDATE_BANNER_TITLE);
        assertThat(updatedBanner.getAttachmentCode()).isEqualTo(UPDATE_ATTACHMENT_CODE);
        assertThat(updatedBanner.getUrlAddr()).isEqualTo(UPDATE_URL_ADDR);
        assertThat(updatedBanner.getBannerContent()).isEqualTo(UPDATE_BANNER_CONTENT);
        assertThat(updatedBanner.getNewWindowAt()).isEqualTo(UPDATE_NEW_WINDOW_AT);
        assertThat(updatedBanner.getUseAt()).isTrue();
        assertThat(updatedBanner.getSortSeq()).isEqualTo(UPDATE_SORT_SEQ);
    }

    /**
     * 배너 삭제 테스트
     */
    @Test
    void 배너_삭제() {
        // given
        Banner entity = insertBanner();

        final Integer bannerNo = entity.getBannerNo();

        String url = URL + "/" + bannerNo;

        // when
        ResponseEntity<BannerResponseDto> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                null,
                BannerResponseDto.class
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Optional<Banner> banner = selectData(bannerNo);
        assertThat(banner).isNotPresent();
    }

    /**
     * 테스트 데이터 등록
     */
    private void insertBanners() {
        for (int i = 1; i <= GIVEN_DATA_COUNT; i++) {
            banners.add(bannerRepository.save(Banner.builder()
            		.site(site)
                    .bannerTypeCode(BANNER_TYPE_CODE_PREFIX + (i % 3 + 1))
                    .bannerTitle(BANNER_TITLE_PREFIX + "_" + i)
                    .attachmentCode(StringUtils.leftPad(String.valueOf(i), 10, '0'))
                    .urlAddr(URL_ADDR_PREFIX + i)
                    .bannerContent(BANNER_CONTENT_PREFIX + "_" + i)
                    .useAt(i % 2 == 0)
                    .newWindowAt(i % 2 == 0)
                    .sortSeq(i)
                    .build()));
        }
    }

    /**
     * 테스트 데이터 삭제
     */
    /*private void deleteBanners() {
        bannerRepository.deleteAll(banners);

        banners.clear();
    }*/

    /**
     * 테스트 데이터 단건 등록
     *
     * @return Banner 배너 엔티티
     */
    private Banner insertBanner() {
        return bannerRepository.save(Banner.builder()
        		.site(site)
                .bannerTypeCode(INSERT_BANNER_TYPE_CODE)
                .bannerTitle(INSERT_BANNER_TITLE)
                .attachmentCode(INSERT_ATTACHMENT_CODE)
                .urlAddr(INSERT_URL_ADDR)
                .bannerContent(INSERT_BANNER_CONTENT)
                .useAt(true)
                .newWindowAt(INSERT_NEW_WINDOW_AT)
                .sortSeq(INSERT_SORT_SEQ)
                .build());
    }

    /**
     * 테스트 데이터 단건 삭제
     */
    /*private void deleteBanner(Integer bannerNo) {
        bannerRepository.deleteById(bannerNo);
    }*/

    /**
     * 테스트 데이터 단건 조회
     *
     * @param bannerNo 배너 번호
     * @return Optional<Banner> 배너 엔티티
     */
    private Optional<Banner> selectData(Integer bannerNo) {
        return bannerRepository.findById(bannerNo);
    }

}