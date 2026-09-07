package org.egovframe.cloud.userservice.api.user;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.egovframe.cloud.common.util.MessageUtil;
import org.egovframe.cloud.userservice.config.TokenProvider;
import org.egovframe.cloud.userservice.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * 이메일 중복확인 요청에 UserEmailRequestDto 의 제약이 적용되는지 확인한다.
 */
class UserApiControllerExistsEmailValidationTest {

    private final MockMvc mvc = MockMvcBuilders
            .standaloneSetup(new UserApiController(mock(UserService.class), mock(Environment.class),
                    mock(TokenProvider.class), mock(MessageUtil.class)))
            .build();

    @Test
    void 이메일_형식이_아니면_400() throws Exception {
        mvc.perform(post("/api/v1/users/exists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"not-an-email\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 공백만_있는_이메일이면_400() throws Exception {
        mvc.perform(post("/api/v1/users/exists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"   \"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 대조군_회원가입은_이메일_형식이_아니면_400() throws Exception {
        mvc.perform(post("/api/v1/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"not-an-email\",\"userName\":\"홍길동\",\"password\":\"Abcd123!xyz\"}"))
                .andExpect(status().isBadRequest());
    }
}
