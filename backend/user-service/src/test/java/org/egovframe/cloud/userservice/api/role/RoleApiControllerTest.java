package org.egovframe.cloud.userservice.api.role;

import java.util.ArrayList;
import java.util.List;

import org.egovframe.cloud.userservice.domain.role.Role;
import org.egovframe.cloud.userservice.domain.role.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
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

/**
 * org.egovframe.cloud.userservice.api.role.RoleApiControllerTest
 * <p>
 * 권한 Rest API 컨트롤러 테스트 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/07
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/07    jooho       최초 생성
 * </pre>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles(profiles = "test")
class RoleApiControllerTest {

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
     * 권한 레파지토리 인터페이스
     */
    @Autowired
    private RoleRepository roleRepository;

    /**
     * 권한 API 경로
     */
    private static final String URL = "/api/v1/roles";

    private static final Integer GIVEN_DATA_COUNT = 4;

    private static final String ROLE_ID_PREFIX = "_ROLE_";
    private static final String ROLE_NAME_PREFIX = "권한 명 테스트";
    private static final String ROLE_CONTENT_PREFIX = "권한 내용";

    /**
     * 테스트 데이터
     */
    private List<Role> testDatas = new ArrayList<>();

    /**
     * 테스트 시작 전
     */
    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(new CharacterEncodingFilter("UTF-8"))
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    /**
     * 테스트 종료 후
     */
    @BeforeEach
    void tearDown() {
    }

    /**
     * 권한 페이지 목록 조회 테스트
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void 권한_페이지_목록_조회() throws Exception {
        // given
        insertTestDatas();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("keywordType", "roleName");
        params.add("keyword", ROLE_NAME_PREFIX);
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfElements").value(GIVEN_DATA_COUNT))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].roleId").value(ROLE_ID_PREFIX + "_1"));

        deleteTestDatas();
    }

    /**
     * 권한 전체 목록 조회 테스트
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void 권한_전체_목록_조회() throws Exception {
        // given
        insertTestDatas();

        // when
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get(URL + "/all"));

        // then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].roleId").value(ROLE_ID_PREFIX + "_1"));

        deleteTestDatas();
    }

    /**
     * 테스트 데이터 등록
     */
    private void insertTestDatas() {
        for (int i = 1; i <= GIVEN_DATA_COUNT; i++) {
            String roleId = ROLE_ID_PREFIX + "_" + i;
            String roleName = ROLE_NAME_PREFIX + "_" + i;
            String roleContent = ROLE_CONTENT_PREFIX + "_" + i;

            testDatas.add(roleRepository.save(Role.builder()
                    .roleId(roleId)
                    .roleName(roleName)
                    .roleContent(roleContent)
                    .sortSeq(i)
                    .build()));
        }
    }

    /**
     * 테스트 데이터 삭제
     */
    private void deleteTestDatas() {
        roleRepository.deleteAll(testDatas);

        testDatas.clear();
    }

}