package org.egovframe.cloud.userservice.api.role;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.egovframe.cloud.userservice.api.role.dto.AuthorizationUpdateRequestDto;
import org.egovframe.cloud.userservice.domain.role.Authorization;
import org.egovframe.cloud.userservice.domain.role.AuthorizationRepository;
import org.egovframe.cloud.userservice.domain.role.RoleAuthorization;
import org.egovframe.cloud.userservice.domain.role.RoleAuthorizationRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * org.egovframe.cloud.userservice.api.role.AuthorizationApiControllerTest
 * <p>
 * 인가 Rest API 컨트롤러 테스트 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/08
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/08    jooho       최초 생성
 * </pre>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles(profiles = "test")
class AuthorizationApiControllerTest {

    /**
     * WebApplicationContext
     */
    @Autowired
    private WebApplicationContext context;

    /**
     * MockMvc
     */
    private MockMvc mvc;

    /**
     * ObjectMapper
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * test rest template
     */
    @Autowired
    TestRestTemplate restTemplate;

    /**
     * 인가 레파지토리 인터페이스
     */
    @Autowired
    AuthorizationRepository authorizationRepository;

    /**
     * 권한 인가 레파지토리 인터페이스
     */
    @Autowired
    RoleAuthorizationRepository roleAuthorizationRepository;

    /**
     * 인가 API 경로
     */
    private static final String URL = "/api/v1/authorizations";

    /**
     * 테스트 데이터 등록 횟수
     */
    private final Integer GIVEN_DATA_COUNT = 10;

    /**
     * 테스트 데이터
     */
    private final String AUTHORIZATION_NAME_PREFIX = "인가 명";
    //private final String URL_PATTERN_VALUE_PREFIX = "URL 패턴 값";
    private final String URL_PATTERN_VALUE_PREFIX = "/api/v1/authorizations";
    //private final String HTTP_METHOD_VALUE_PREFIX = "Http Method 코드";
    private final String HTTP_METHOD_VALUE_PREFIX = "GET";

    private final String INSERT_AUTHORIZATION_NAME = AUTHORIZATION_NAME_PREFIX + "_1";
    private final String INSERT_URL_PATTERN_VALUE = URL_PATTERN_VALUE_PREFIX + "_1";
    private final String INSERT_HTTP_METHOD_VALUE = HTTP_METHOD_VALUE_PREFIX + "_1";
    private final Integer INSERT_SORT_SEQ = 2;

    private final String UPDATE_AUTHORIZATION_NAME = AUTHORIZATION_NAME_PREFIX + "_2";
    private final String UPDATE_URL_PATTERN_VALUE = URL_PATTERN_VALUE_PREFIX + "_";
    private final String UPDATE_HTTP_METHOD_VALUE = HTTP_METHOD_VALUE_PREFIX + "_";
    private final Integer UPDATE_SORT_SEQ = 2;

    /**
     * 테스트 데이터
     */
    private List<Authorization> testDatas = new ArrayList<>();

    /**
     * 테스트 시작 전 수행
     */
    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(new CharacterEncodingFilter("UTF-8"))
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    /**
     * 테스트 종료 후 수행
     */
    @AfterEach
    void tearDown() {
    }

    /**
     * 인가 페이지 목록 조회 테스트
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void 인가_페이지_목록_조회() throws Exception {
        // given
        insertTestDatas();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("keywordType", "authorizationName");
        params.add("keyword", AUTHORIZATION_NAME_PREFIX);
        params.add("page", "0");
        params.add("size", "10");

        // when
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get(URL)
                .params(params));

        // then
        resultActions
                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfElements").value(GIVEN_DATA_COUNT))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].authorizationName").value(AUTHORIZATION_NAME_PREFIX + "_1"));

    	deleteTestDatas();
    }

    /**
     * 인가 상세 조회 테스트
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void 인가_상세_조회() throws Exception {
        // given
        Authorization entity = insertTestData();

        final Integer authorizationNo = entity.getAuthorizationNo();

        // when
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get(URL + "/" + authorizationNo));

        // then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.authorizationNo").value(authorizationNo))
                .andExpect(MockMvcResultMatchers.jsonPath("$.authorizationName").value(INSERT_AUTHORIZATION_NAME))
                .andExpect(MockMvcResultMatchers.jsonPath("$.urlPatternValue").value(INSERT_URL_PATTERN_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpMethodCode").value(INSERT_HTTP_METHOD_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sortSeq").value(INSERT_SORT_SEQ));

        deleteTestData(authorizationNo);
    }

    /**
     * 인가 다음 정렬 순서 조회
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void 인가_다음정렬순서_조회() throws Exception {
        // given
        insertTestDatas();

        // when
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get(URL + "/sort-seq/next"));

        // then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                // .andExpect(MockMvcResultMatchers.content().string("11"));
                .andExpect(MockMvcResultMatchers.content().string("130")); // /src/test/resources/h2/data.sql 초기화 데이터의 마지막 순번 + 1

        deleteTestDatas();
    }

    /**
     * 인가 등록 테스트
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void 인가_등록() throws Exception {
        // given
        Map<String, Object> params = new HashMap<>();
        params.put("authorizationName", INSERT_AUTHORIZATION_NAME);
        params.put("urlPatternValue", INSERT_URL_PATTERN_VALUE);
        params.put("httpMethodCode", INSERT_HTTP_METHOD_VALUE);
        params.put("sortSeq", INSERT_SORT_SEQ);

        // when
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(URL)
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/json;charset=UTF-8")
                .content(objectMapper.writeValueAsString(params)));

        // then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated());

        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        JSONObject jsonObject = new JSONObject(responseData);

        final Integer authorizationNo = Integer.parseInt(jsonObject.get("authorizationNo").toString());

        Optional<Authorization> authorization = selectData(authorizationNo);
        assertThat(authorization).isPresent();

        Authorization entity = authorization.get();
        assertThat(entity.getAuthorizationNo()).isEqualTo(authorizationNo);
        assertThat(entity.getAuthorizationName()).isEqualTo(INSERT_AUTHORIZATION_NAME);
        assertThat(entity.getUrlPatternValue()).isEqualTo(INSERT_URL_PATTERN_VALUE);
        assertThat(entity.getHttpMethodCode()).isEqualTo(INSERT_HTTP_METHOD_VALUE);
        assertThat(entity.getSortSeq()).isEqualTo(INSERT_SORT_SEQ);

        deleteTestData(authorizationNo);
    }

    /**
     * 인가 수정 테스트
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void 인가_수정() throws Exception {
        // given
        Authorization entity = insertTestData();

        final Integer authorizationNo = entity.getAuthorizationNo();

        Map<String, Object> params = new HashMap<>();
        params.put("authorizationName", UPDATE_AUTHORIZATION_NAME);
        params.put("urlPatternValue", UPDATE_URL_PATTERN_VALUE);
        params.put("httpMethodCode", UPDATE_HTTP_METHOD_VALUE);
        params.put("sortSeq", UPDATE_SORT_SEQ);

        // when
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.put(URL + "/" + authorizationNo)
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/json;charset=UTF-8")
                .content(objectMapper.writeValueAsString(params)));

        // then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Optional<Authorization> authorization = selectData(authorizationNo);
        assertThat(authorization).isPresent();

        Authorization updatedAuthorization = authorization.get();
        assertThat(updatedAuthorization.getAuthorizationNo()).isEqualTo(authorizationNo);
        assertThat(updatedAuthorization.getAuthorizationName()).isEqualTo(UPDATE_AUTHORIZATION_NAME);
        assertThat(updatedAuthorization.getUrlPatternValue()).isEqualTo(UPDATE_URL_PATTERN_VALUE);
        assertThat(updatedAuthorization.getHttpMethodCode()).isEqualTo(UPDATE_HTTP_METHOD_VALUE);
        assertThat(updatedAuthorization.getSortSeq()).isEqualTo(UPDATE_SORT_SEQ);

        deleteTestData(authorizationNo);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 인가_정렬순서_변경() throws Exception {
        // given
        insertTestDatas();

        testDatas.stream().forEach(System.out::println);

        Authorization authorization = testDatas.get(4);

        assertThat(authorization.getSortSeq()).isEqualTo(5);

        AuthorizationUpdateRequestDto requestDto = AuthorizationUpdateRequestDto.builder()
            .authorizationName(authorization.getAuthorizationName())
            .httpMethodCode(authorization.getHttpMethodCode())
            .urlPatternValue(authorization.getUrlPatternValue())
            .sortSeq(7)
            .build();

        // when
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.put(URL + "/" + authorization.getAuthorizationNo())
            .accept(MediaType.APPLICATION_JSON)
            .contentType("application/json;charset=UTF-8")
            .content(objectMapper.writeValueAsString(requestDto)));

        // then
        resultActions
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk());

        Optional<Authorization> optional = selectData(authorization.getAuthorizationNo());
        assertThat(optional.isPresent()).isTrue();
        Authorization updateAuthorization = optional.get();

        assertThat(updateAuthorization.getSortSeq()).isEqualTo(7);

        List<Authorization> all = authorizationRepository.findAll();
        all.stream().forEach(System.out::println);

        deleteTestDatas();

    }

    /**
     * 인가 삭제 테스트
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void 인가_삭제() throws Exception {
        // given
        Authorization entity = insertTestData();

        final Integer authorizationNo = entity.getAuthorizationNo();

        // 권한 인가 2건 등록 후 같이 삭제
        roleAuthorizationRepository.save(RoleAuthorization.builder()
                .roleId("ROLE_1")
                .authorizationNo(authorizationNo)
                .build());
        roleAuthorizationRepository.save(RoleAuthorization.builder()
                .roleId("ROLE_2")
                .authorizationNo(authorizationNo)
                .build());

        // when
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.delete(URL + "/" + authorizationNo));

        // then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Optional<Authorization> authorization = selectData(authorizationNo);
        assertThat(authorization).isNotPresent();
    }

    /**
     * 테스트 데이터 등록
     */
    private void insertTestDatas() {
        for (int i = 1; i <= GIVEN_DATA_COUNT; i++) {
            testDatas.add(authorizationRepository.save(Authorization.builder()
                    .authorizationName(AUTHORIZATION_NAME_PREFIX + "_" + i)
                    .urlPatternValue(URL_PATTERN_VALUE_PREFIX + "_" + i)
                    .httpMethodCode(HTTP_METHOD_VALUE_PREFIX + "_" + i)
                    .sortSeq(i)
                    .build()));
        }
    }

    /**
     * 테스트 데이터 삭제
     */
    private void deleteTestDatas() {
    	if (testDatas != null) {
            if (!testDatas.isEmpty()) authorizationRepository.deleteAll(testDatas);

            testDatas.clear();
    	}
    }

    /**
     * 테스트 데이터 단건 등록
     *
     * @return Authorization 인가 엔티티
     */
    private Authorization insertTestData() {
        return authorizationRepository.save(Authorization.builder()
                .authorizationName(INSERT_AUTHORIZATION_NAME)
                .urlPatternValue(INSERT_URL_PATTERN_VALUE)
                .httpMethodCode(INSERT_HTTP_METHOD_VALUE)
                .sortSeq(INSERT_SORT_SEQ)
                .build());
    }

    /**
     * 테스트 데이터 단건 삭제
     */
    private void deleteTestData(Integer authorizationNo) {
        authorizationRepository.deleteById(authorizationNo);
    }

    /**
     * 테스트 데이터 단건 조회
     *
     * @param authorizationNo 인가 번호
     * @return Optional<Authorization> 인가 엔티티
     */
    private Optional<Authorization> selectData(Integer authorizationNo) {
        return authorizationRepository.findById(authorizationNo);
    }

}