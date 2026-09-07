package org.egovframe.cloud.portalservice.api.menu;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.egovframe.cloud.portalservice.domain.menu.SiteRepository;
import org.egovframe.cloud.portalservice.service.menu.MenuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class MenuApiControllerValidationTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new MenuApiController(mock(MenuService.class), mock(SiteRepository.class)))
                .build();
    }

    @DisplayName("메뉴명이 공백 문자뿐이면 수정 요청이 거부된다")
    @Test
    void update_rejects_blank_menu_name() throws Exception {
        mockMvc.perform(put("/api/v1/menus/{menuId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"menuKorName\":\"  \",\"menuEngName\":\"  \"}"))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("메뉴명이 있으면 수정 요청이 통과한다")
    @Test
    void update_accepts_menu_name() throws Exception {
        mockMvc.perform(put("/api/v1/menus/{menuId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"menuKorName\":\"메뉴\",\"menuEngName\":\"menu\"}"))
                .andExpect(status().isOk());
    }
}
