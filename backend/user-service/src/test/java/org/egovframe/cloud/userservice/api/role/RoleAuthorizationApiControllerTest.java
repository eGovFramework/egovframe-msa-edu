package org.egovframe.cloud.userservice.api.role;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.assertj.core.api.Condition;
import org.egovframe.cloud.userservice.domain.role.Authorization;
import org.egovframe.cloud.userservice.domain.role.AuthorizationRepository;
import org.egovframe.cloud.userservice.domain.role.Role;
import org.egovframe.cloud.userservice.domain.role.RoleAuthorization;
import org.egovframe.cloud.userservice.domain.role.RoleAuthorizationId;
import org.egovframe.cloud.userservice.domain.role.RoleAuthorizationRepository;
import org.egovframe.cloud.userservice.domain.role.RoleRepository;
import org.json.JSONArray;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
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

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * org.egovframe.cloud.userservice.api.role.RoleAuthorizationApiControllerTest
 * <p>
 * 권한 인가 Rest API 컨트롤러 테스트 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/12
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/12    jooho       최초 생성
 * </pre>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles(profiles = "test")
class RoleAuthorizationApiControllerTest {

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
     * 권한 레파지토리 인터페이스
     */
    @Autowired
    RoleRepository roleRepository;

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
    private static final String URL = "/api/v1/role-authorizations";

    /**
     * 테스트 데이터 등록 횟수
     */
    private final Integer GIVEN_AUTHORIZATION_COUNT = 5;

    private final String ROLE_ID = "_ROLE_1";
    private final String ROLE_NAME = "권한 명_1";
    private final String ROLE_CONTENT = "권한 내용_1";

    private final String AUTHORIZATION_NAME_PREFIX = "인가 명";
    private final String URL_PATTERN_VALUE_PREFIX = "/api/v1/test";
    private final String HTTP_METHOD_VALUE_PREFIX = "GET";

    /**
     * 테스트 데이터
     */
    private Role role = null;
    private final List<Authorization> authorizations = new ArrayList<>();
    private final List<RoleAuthorization> testDatas = new ArrayList<>();

    /**
     * 테스트 시작 전 수행
     */
    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(new CharacterEncodingFilter("UTF-8"))
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        // 권한 등록
        role = roleRepository.save(Role.builder()
                .roleId(ROLE_ID)
                .roleName(ROLE_NAME)
                .roleContent(ROLE_CONTENT)
                .sortSeq(1)
                .build());

        // 인가 등록
        for (int i = 1; i <= GIVEN_AUTHORIZATION_COUNT; i++) {
            authorizations.add(authorizationRepository.save(Authorization.builder()
                    .authorizationName(AUTHORIZATION_NAME_PREFIX + "_" + i)
                    .urlPatternValue(URL_PATTERN_VALUE_PREFIX + "_" + i)
                    .httpMethodCode(HTTP_METHOD_VALUE_PREFIX + "_" + i)
                    .sortSeq(i)
                    .build()));
        }
    }

    /**
     * 테스트 종료 후 수행
     */
    @AfterEach
    void tearDown() {
        // 인가 삭제
        authorizationRepository.deleteAll(authorizations);
        authorizations.clear();

        // 권한 삭제
        roleRepository.delete(role);
    }

    /**
     * 권한 인가 페이지 목록 조회
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void 권한_인가_페이지_목록_조회() throws Exception {
        // given
        insertTestDatas();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("roleId", role.getRoleId());
        params.add("keywordType", "urlPatternValue");
        params.add("keyword", URL_PATTERN_VALUE_PREFIX);
        params.add("page", "0");
        params.add("size", "10");

        // when
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get(URL)
                .params(params));

        // then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfElements").value(authorizations.size()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].roleId").value(role.getRoleId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].authorizationNo").value(authorizations.get(0).getAuthorizationNo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].createdAt").value(true));

        deleteTestDatas();
    }

    /**
     * 권한 인가 다건 등록
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void 권한_인가_다건_등록() throws Exception {
        // given
        List<Map<String, Object>> requestDtoList = new ArrayList<>();
        for (int i = 1; i <= authorizations.size(); i++) {
            if (i % 2 == 0) continue; //홀수만 등록

            Map<String, Object> params = new HashMap<>();
            params.put("roleId", role.getRoleId());
            params.put("authorizationNo", authorizations.get(i - 1).getAuthorizationNo());

            requestDtoList.add(params);
        }

        // when
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(URL)
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/json;charset=UTF-8")
                .content(objectMapper.writeValueAsString(requestDtoList)));

        // then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated());

        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        JSONArray jsonArray = new JSONArray(responseData);

        assertThat(jsonArray.length()).isEqualTo(requestDtoList.size());

        List<RoleAuthorization> entityList = roleAuthorizationRepository.findAll(Sort.by(Sort.Direction.ASC, "roleAuthorizationId.authorizationNo"));
        for (int i = entityList.size() - 1; i >= 0; i--) {
            if (!entityList.get(i).getRoleAuthorizationId().getRoleId().equals(role.getRoleId())) {
                entityList.remove(i);
            }
        }
        assertThat(entityList).isNotNull();
        assertThat(entityList.size()).isEqualTo(requestDtoList.size());
        assertThat(entityList)
                .isNotEmpty()
                .has(new Condition<>(l -> l.get(0).getRoleAuthorizationId().getRoleId().equals(role.getRoleId()) && l.get(0).getRoleAuthorizationId().getAuthorizationNo().compareTo(authorizations.get(0).getAuthorizationNo()) == 0,
                        "RoleAuthorizationApiControllerTest.saveList authorizationNo eq 1"))
                .has(new Condition<>(l -> l.get(1).getRoleAuthorizationId().getRoleId().equals(role.getRoleId()) && l.get(1).getRoleAuthorizationId().getAuthorizationNo().compareTo(authorizations.get(2).getAuthorizationNo()) == 0,
                        "RoleAuthorizationApiControllerTest.saveList authorizationNo eq 3"));

        for (int i = entityList.size() - 1; i >= 0; i--) {
            deleteTestData(entityList.get(i).getRoleAuthorizationId().getRoleId(), entityList.get(i).getRoleAuthorizationId().getAuthorizationNo());
        }
    }

    /**
     * 권한 인가 다건 삭제
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void 권한_인가_다건_삭제() throws Exception {
        // given
        insertTestDatas();

        List<Map<String, Object>> requestDtoList = new ArrayList<>();
        for (RoleAuthorization testData : testDatas) {
            Map<String, Object> params = new HashMap<>();
            params.put("roleId", testData.getRoleAuthorizationId().getRoleId());
            params.put("authorizationNo", testData.getRoleAuthorizationId().getAuthorizationNo());

            requestDtoList.add(params);
        }

        // when
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.put(URL)
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/json;charset=UTF-8")
                .content(objectMapper.writeValueAsString(requestDtoList)));

        // then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        List<RoleAuthorization> entityList = roleAuthorizationRepository.findAll(Sort.by(Sort.Direction.ASC, "roleAuthorizationId.authorizationNo"));
        for (int i = entityList.size() - 1; i >= 0; i--) {
            if (!entityList.get(i).getRoleAuthorizationId().getRoleId().equals(role.getRoleId())) {
                entityList.remove(i);
            }
        }
        assertThat(entityList).isNotNull();
        assertThat(entityList.size()).isZero();
    }

    /**
     * 권한 인가 레파지토리 등록/조회 테스트
     */
    @Test
    @Disabled
    void 권한_인가_등록_조회() {
        // given
        final Integer authorizationNo = authorizations.get(0).getAuthorizationNo();

        roleAuthorizationRepository.save(RoleAuthorization.builder()
                .roleId(role.getRoleId())
                .authorizationNo(authorizationNo)
                .build());

        // when
        Optional<RoleAuthorization> roleAuthorization = selectData(role.getRoleId(), authorizationNo);

        // then
        assertThat(roleAuthorization).isPresent();

        RoleAuthorization entity = roleAuthorization.get();
        assertThat(entity.getRoleAuthorizationId().getRoleId()).isEqualTo(role.getRoleId());
        assertThat(entity.getRoleAuthorizationId().getAuthorizationNo()).isEqualTo(authorizationNo);
    }

    /**
     * 테스트 데이터 등록
     */
    private void insertTestDatas() {
        // 권한 인가 등록
        for (int i = 1; i <= authorizations.size(); i++) {
            if (i % 2 == 0) continue; //인가 번호 홀수만 등록

            testDatas.add(roleAuthorizationRepository.save(RoleAuthorization.builder()
                    .roleId(role.getRoleId())
                    .authorizationNo(authorizations.get(i - 1).getAuthorizationNo())
                    .build()));
        }
    }

    /**
     * 테스트 데이터 삭제
     */
    private void deleteTestDatas() {
        // 권한 인가 삭제
        roleAuthorizationRepository.deleteAll(testDatas);
        testDatas.clear();
    }

    /**
     * 테스트 데이터 단건 삭제
     */
    private void deleteTestData(String roleId, Integer authorizationNo) {
        roleAuthorizationRepository.deleteById(RoleAuthorizationId.builder()
                .roleId(roleId)
                .authorizationNo(authorizationNo)
                .build());
    }

    /**
     * 테스트 데이터 단건 조회
     *
     * @param roleId          권한 id
     * @param authorizationNo 인가 번호
     * @return Optional<RoleAuthorization> 권한 인가 엔티티
     */
    private Optional<RoleAuthorization> selectData(String roleId, Integer authorizationNo) {
        return roleAuthorizationRepository.findById(RoleAuthorizationId.builder()
                .roleId(roleId)
                .authorizationNo(authorizationNo)
                .build());
    }

}