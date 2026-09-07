package org.egovframe.cloud.portalservice.api.policy;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.egovframe.cloud.portalservice.service.policy.PolicyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class PolicyApiControllerValidationTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new PolicyApiController(mock(PolicyService.class)))
                .build();
    }

    @DisplayName("유형이 없으면 등록 요청이 거부된다")
    @Test
    void save_rejects_blank_type() throws Exception {
        mockMvc.perform(post("/api/v1/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"이용약관\"}"))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("유형과 제목이 있으면 등록 요청이 통과한다")
    @Test
    void save_accepts_type_and_title() throws Exception {
        mockMvc.perform(post("/api/v1/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"TOS\",\"title\":\"이용약관\"}"))
                .andExpect(status().isCreated());
    }
}
