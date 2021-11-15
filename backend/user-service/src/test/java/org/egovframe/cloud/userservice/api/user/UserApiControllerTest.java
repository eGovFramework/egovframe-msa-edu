package org.egovframe.cloud.userservice.api.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egovframe.cloud.common.domain.Role;
import org.egovframe.cloud.userservice.api.user.dto.UserResponseDto;
import org.egovframe.cloud.userservice.api.user.dto.UserSaveRequestDto;
import org.egovframe.cloud.userservice.api.user.dto.UserUpdateRequestDto;
import org.egovframe.cloud.userservice.domain.user.User;
import org.egovframe.cloud.userservice.domain.user.UserRepository;
import org.egovframe.cloud.userservice.domain.user.UserStateCode;
import org.egovframe.cloud.userservice.service.user.UserService;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles(profiles = "test")
class UserApiControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TestRestTemplate restTemplate;

    //    private static final String USER_SERVICE_URL = "http://localhost:8000/user-service";
    private static final String TEST_COM = "@test.com";
    private static final String TEST_EMAIL = System.currentTimeMillis() + TEST_COM;
    private static final String TEST_PASSWORD = "test1234!";

    /**
     * API 경로
     */
    private static final String URL = "/api/v1/users";

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
     * 테스트 데이터
     */
    private List<User> datas = new ArrayList<>();
    private final Integer GIVEN_DATA_COUNT = 10;
    private final String USER_NAME_PREFIX = "USER";
    private final String TOKEN = "1234567890";
    private final String DECRYPTED_PASSWORD = "test1234!";
    private final String ENCRYPTED_PASSWORD = "$2a$10$Xf9rt9ziTa3AXCuxG2TTruCC0RKCG62ukI6cHrptHnTMgCrviC8j.";

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

    @Test
    @Order(Integer.MAX_VALUE)
    public void cleanup() {
        // 테스트 후 데이터 삭제
        List<User> users = userRepository.findByEmailContains("test.com");
        users.forEach(user -> userRepository.deleteById(user.getId()));
    }

    @Test
    @Order(Integer.MIN_VALUE)
    public void 사용자_등록된다() {
        // given
        UserSaveRequestDto userSaveRequestDto = UserSaveRequestDto.builder()
                .userName("사용자")
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .roleId(Role.USER.getKey())
                .userStateCode("01")
                .build();
        userService.save(userSaveRequestDto);

        // when
        UserResponseDto findUser = userService.findByEmail(TEST_EMAIL);

        // then
        assertThat(findUser.getEmail()).isEqualTo(TEST_EMAIL);
    }

    @Test
    @Order(2)
    public void 사용자_수정된다() {
        // given
        UserResponseDto findUser = userService.findByEmail(TEST_EMAIL);
        UserUpdateRequestDto userUpdateRequestDto = UserUpdateRequestDto.builder()
                .userName("사용자수정")
                .email(TEST_EMAIL)
                .roleId(Role.USER.getKey())
                .userStateCode("01")
                .build();

        // when
        userService.update(findUser.getUserId(), userUpdateRequestDto);
        UserResponseDto updatedUser = userService.findByEmail(TEST_EMAIL);

        // then
        assertThat(updatedUser.getUserName()).isEqualTo("사용자수정");
    }

    @Test
    public void 사용자_등록오류() {
        // given
        UserSaveRequestDto userSaveRequestDto = UserSaveRequestDto.builder()
                .userName("사용자")
                .email("email")
                .password("test")
                .build();

        String url = "/api/v1/users";

        RestClientException restClientException = Assertions.assertThrows(RestClientException.class, () -> {
            restTemplate.postForEntity(url, userSaveRequestDto, Long.class);
        });
        System.out.println("restClientException.getMessage() = " + restClientException.getMessage());
    }

    @Test
    public void 사용자_로그인된다() throws Exception {
        // given
        JSONObject loginJson = new JSONObject();
        loginJson.put("email", TEST_EMAIL);
        loginJson.put("password", TEST_PASSWORD);

        String url = "/login";

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, loginJson.toString(), String.class);
        responseEntity.getHeaders().entrySet().forEach(System.out::println);
        assertThat(responseEntity.getHeaders().containsKey("access-token")).isTrue();
    }

    @Test
    public void 사용자_로그인_오류발생한다() throws Exception {
        // given
        JSONObject loginJson = new JSONObject();
        loginJson.put("email", TEST_EMAIL);
        loginJson.put("password", "test");

        String url = "/login";

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, loginJson.toString(), String.class);
        System.out.println("responseEntity = " + responseEntity);
        assertThat(responseEntity.getHeaders().containsKey("access-token")).isFalse();
    }

    /**
     * 사용자 페이지 목록 조회 테스트
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void 사용자_페이지_목록_조회() throws Exception {
        // given
        insertUsers();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("keywordType", "userName");
        params.add("keyword", USER_NAME_PREFIX);
        params.add("page", "0");
        params.add("size", "10");

        // when
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get(URL)
                .params(params));

        // then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfElements").value(GIVEN_DATA_COUNT))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].userName").value(USER_NAME_PREFIX + "1"));

        deleteUsers();
    }

    /**
     * 사용자 상세 조회 테스트
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void 사용자_상세_조회() throws Exception {
        // given
        User entity = insertUser();

        final String userId = entity.getUserId();

        // when
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get(URL + "/" + userId));

        // then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userName").value(entity.getUserName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(entity.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roleId").value(entity.getRole().getKey()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userStateCode").value(entity.getUserStateCode()));

        deleteUser(entity.getId());
    }

    /**
     * 사용자 소셜 정보 조회 테스트
     * 많이 시도하면 구글에서 블락 할수도..
     */
    @Test
    void 사용자_소셜_정보_조회_테스트() throws Exception {
        // given
        Map<String, Object> params = new HashMap<>();
        params.put("provider", "google");
        params.put("token", TOKEN);

        // when
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(URL + "/social")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/json;charset=UTF-8")
                .content(objectMapper.writeValueAsString(params)));

        // then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value())); // org.egovframe.cloud.common.exception.BusinessMessageException: 공급사에서 회원 정보를 확인할 수 없습니다.
    }

    /**
     * 이메일 중복 확인 테스트
     */
    @Test
    void 이메일_중복_확인_테스트() throws Exception {
        // given
        User entity = insertUser();

        Map<String, Object> params = new HashMap<>();
        params.put("email", "1" + TEST_COM);

        // when
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(URL + "/exists")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/json;charset=UTF-8")
                .content(objectMapper.writeValueAsString(params)));

        // then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("true"));

        deleteUser(entity.getId());
    }

    /**
     * 사용자 회원 가입 테스트
     */
    @Test
    void 사용자_회원_가입_테스트() throws Exception {
        // given
        final String email = "test_join" + TEST_COM;

        Map<String, Object> params = new HashMap<>();
        params.put("userName", USER_NAME_PREFIX + "1");
        params.put("email", email);
        params.put("password", DECRYPTED_PASSWORD);

        // when
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(URL + "/join")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/json;charset=UTF-8")
                .content(objectMapper.writeValueAsString(params)));

        // then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("true"));

//        userRepository.findByEmail("test_join" + TEST_COM).ifPresent(u -> deleteUser(u.getId()));

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(String.format("[%s]사용자가 없습니다.", email)));
        assertThat(user.getUserName()).isEqualTo(USER_NAME_PREFIX + "1");
        assertThat(user.getEmail()).isEqualTo(email);

        deleteUser(user.getId());
    }

    /**
     * 사용자 비밀번호 찾기 테스트
     */
    @Test
    void 사용자_비밀번호_찾기_테스트() throws Exception {
        // given
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", "give928@gmail.com");

        User user = insertUser(userData);

        Map<String, Object> params = new HashMap<>();
        params.put("userName", user.getUserName());
        params.put("emailAddr", user.getEmail());
        params.put("mainUrl", "http://localhost:4000");
        params.put("changePasswordUrl", "http://localhost:4000/user/password/change");

        // when
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(URL + "/password/find")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/json;charset=UTF-8")
                .content(objectMapper.writeValueAsString(params)));

        // then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest()); // 계정 설정 안되어 있으므로 연결하지 못함.

        deleteUser(user.getId());
    }

    /**
     * 사용자 비밀번호 찾기 유효성 확인 테스트
     */
    @Test
    void 사용자_비밀번호_찾기_유효성_확인_테스트() throws Exception {
        // given
        final String token = TOKEN;

        // when
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get(URL + "/password/valid/" + token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/json;charset=UTF-8"));

        // then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("false"));
    }

    /**
     * 사용자 비밀번호 찾기 변경 테스트
     */
    @Test
    void 사용자_비밀번호_찾기_변경_테스트() throws Exception {
        // given
        Map<String, Object> params = new HashMap<>();
        params.put("tokenValue", TOKEN);
        params.put("password", DECRYPTED_PASSWORD);

        // when
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.put(URL + "/password/change")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/json;charset=UTF-8")
                .content(objectMapper.writeValueAsString(params)));

        // then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value())); // org.egovframe.cloud.common.exception.BusinessMessageException: 인증시간이 만료되었습니다. 처음부터 다시 진행해주시기 바랍니다.
    }

    /**
     * 사용자 비밀번호 변경 테스트
     */
    @Test
    @WithMockUser(roles = "USER", username = "test-user")
    void 사용자_비밀번호_변경_테스트() throws Exception {
        // given
        final String userId = "test-user";

        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", userId);

        User entity = insertUser(userData);

        final Long userNo = entity.getId();

        Map<String, Object> params = new HashMap<>();
        params.put("provider", "password");
        params.put("password", DECRYPTED_PASSWORD);
        params.put("newPassword", "P@ssw0rd1");

        // when
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.put(URL + "/password/update")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/json;charset=UTF-8")
                .content(objectMapper.writeValueAsString(params)));

        // then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("true"));

        User user = selectUser(userNo).orElseThrow(() -> new UsernameNotFoundException(String.format("[%d]사용자가 없습니다.", userNo)));
        assertThat(user.getEncryptedPassword()).isNotEqualTo(entity.getEncryptedPassword());

        deleteUser(entity.getId());
    }

    /**
     * 사용자 비밀번호 확인 테스트
     */
    @Test
    @WithMockUser(roles = "USER", username = "test-user")
    void 사용자_비밀번호_확인_테스트() throws Exception {
        // given
        final String userId = "test-user";

        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", userId);

        User entity = insertUser(userData);

        Map<String, Object> params = new HashMap<>();
        params.put("password", DECRYPTED_PASSWORD);

        // when
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(URL + "/password/match")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/json;charset=UTF-8")
                .content(objectMapper.writeValueAsString(params)));

        // then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("true"));

        deleteUser(entity.getId());
    }

    /**
     * 사용자 회원정보 변경 테스트
     */
    @Test
    @WithMockUser(roles = "USER", username = "test-user")
    void 사용자_회원정보_변경_테스트() throws Exception {
        // given
        final String userId = "test-user";

        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", userId);

        User entity = insertUser(userData);

        final Long userNo = entity.getId();
        final String userName = "TEST-USER";
        final String email = "2" + TEST_COM;

        Map<String, Object> params = new HashMap<>();
        params.put("provider", "password");
        params.put("password", DECRYPTED_PASSWORD);
        params.put("userName", userName);
        params.put("email", email);

        // when
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.put(URL + "/info/" + userId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/json;charset=UTF-8")
                .content(objectMapper.writeValueAsString(params)));

        // then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(userId));

        User user = selectUser(userNo).orElseThrow(() -> new UsernameNotFoundException(String.format("[%d]사용자가 없습니다.", userNo)));
        assertThat(user.getUserName()).isEqualTo(userName);
        assertThat(user.getEmail()).isEqualTo(email);

        deleteUser(entity.getId());
    }

    /**
     * 사용자 회원 탈퇴 테스트
     */
    @Test
    @WithMockUser(roles = "USER", username = "test-user")
    void 사용자_회원_탈퇴_테스트() throws Exception {
        // given
        final String userId = "test-user";

        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", userId);

        User entity = insertUser(userData);

        final Long userNo = entity.getId();

        Map<String, Object> params = new HashMap<>();
        params.put("provider", "password");
        params.put("password", DECRYPTED_PASSWORD);

        // when
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(URL + "/leave")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/json;charset=UTF-8")
                .content(objectMapper.writeValueAsString(params)));

        // then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("true"));

        User user = selectUser(userNo).orElseThrow(() -> new UsernameNotFoundException(String.format("[%d]사용자가 없습니다.", userNo)));
        assertThat(user.getUserStateCode()).isEqualTo(UserStateCode.LEAVE.getKey());

        deleteUser(entity.getId());
    }

    /**
     * 사용자 삭제 테스트
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void 사용자_삭제_테스트() throws Exception {
        // given
        final String userId = "test-user";

        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", userId);

        User entity = insertUser(userData);

        final Long userNo = entity.getId();

        // when
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.delete(URL + "/delete/" + userId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/json;charset=UTF-8"));

        // then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("true"));

        User user = selectUser(userNo).orElseThrow(() -> new UsernameNotFoundException(String.format("[%d]사용자가 없습니다.", userNo)));
        assertThat(user.getUserStateCode()).isEqualTo(UserStateCode.DELETE.getKey());

        deleteUser(entity.getId());
    }

    /**
     * 테스트 데이터 등록
     */
    private void insertUsers() {
        for (int i = 1; i <= GIVEN_DATA_COUNT; i++) {
            datas.add(userRepository.save(User.builder()
                    .userId(UUID.randomUUID().toString())
                    .encryptedPassword(ENCRYPTED_PASSWORD)
                    .userName(USER_NAME_PREFIX + i)
                    .email(i + TEST_COM)
                    .role(Role.USER)
                    .userStateCode(UserStateCode.NORMAL.getKey())
                    .build()));
        }
    }

    /**
     * 테스트 데이터 삭제
     */
    private void deleteUsers() {
        if (datas != null) {
            if (!datas.isEmpty()) userRepository.deleteAll(datas);

            datas.clear();
        }
    }

    /**
     * 테스트 데이터 단건 등록
     *
     * @return User 사용자 엔티티
     */
    private User insertUser() {
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", "1" + TEST_COM);

        return insertUser(userData);
    }
    private User insertUser(Map<String, Object> userData) {
        return userRepository.save(User.builder()
                .userId(userData.get("userId") != null ? (String) userData.get("userId") : UUID.randomUUID().toString())
                .encryptedPassword(ENCRYPTED_PASSWORD)
                .userName(USER_NAME_PREFIX + "1")
                .email(userData.get("email") != null ? (String) userData.get("email") : "1" + TEST_COM)
                .role(Role.USER)
                .userStateCode(UserStateCode.NORMAL.getKey())
                .build());
    }

    /**
     * 테스트 데이터 단건 삭제
     */
    private void deleteUser(Long userNo) {
        userRepository.deleteById(userNo);
    }

    /**
     * 테스트 데이터 단건 조회
     *
     * @param userNo 사용자 번호
     * @return Optional<User> 사용자
     */
    private Optional<User> selectUser(Long userNo) {
        return userRepository.findById(userNo);
    }

}