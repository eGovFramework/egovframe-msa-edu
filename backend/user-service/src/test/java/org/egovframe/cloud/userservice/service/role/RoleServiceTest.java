package org.egovframe.cloud.userservice.service.role;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.common.util.MessageUtil;
import org.egovframe.cloud.userservice.api.role.dto.RoleListResponseDto;
import org.egovframe.cloud.userservice.domain.role.Role;
import org.egovframe.cloud.userservice.domain.role.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * org.egovframe.cloud.userservice.service.role.RoleServiceTest
 * <p>
 * 권한 서비스 단위 테스트 클래스
 *
 * @author 표준프레임워크센터
 * @version 1.0
 * @since 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private MessageUtil messageUtil;

    @InjectMocks
    private RoleService roleService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(roleService, "messageUtil", messageUtil);
        given(messageUtil.getMessage(anyString())).willReturn("message");
        given(messageUtil.getMessage(anyString(), any(Object[].class))).willReturn("message");
    }

    @DisplayName("조회 조건과 페이지 정보가 주어지면 권한 페이지 목록을 반환한다")
    @Test
    void should_returnRolePage_when_requestDtoAndPageable() {
        // given
        RequestDto requestDto = new RequestDto();
        Pageable pageable = PageRequest.of(0, 10);
        RoleListResponseDto responseDto = RoleListResponseDto.builder()
                .roleId("ROLE_USER")
                .roleName("일반사용자")
                .roleContent("일반 사용자 권한")
                .build();
        Page<RoleListResponseDto> expectedPage = new PageImpl<>(Arrays.asList(responseDto), pageable, 1);
        given(roleRepository.findPage(requestDto, pageable)).willReturn(expectedPage);

        // when
        Page<RoleListResponseDto> result = roleService.findPage(requestDto, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getRoleId()).isEqualTo("ROLE_USER");
        assertThat(result.getContent().get(0).getRoleName()).isEqualTo("일반사용자");
        verify(roleRepository).findPage(requestDto, pageable);
    }

    @DisplayName("정렬 조건이 주어지면 권한 전체 목록을 반환한다")
    @Test
    void should_returnRoleList_when_sortBySortOrder() {
        // given
        Sort sort = Sort.by(Sort.Direction.ASC, "sortSeq");
        Role role = Role.builder()
                .roleId("ROLE_ADMIN")
                .roleName("관리자")
                .roleContent("시스템 관리자 권한")
                .sortSeq(1)
                .build();
        LocalDateTime now = LocalDateTime.now();
        ReflectionTestUtils.setField(role, "createdDate", now);

        given(roleRepository.findAll(sort)).willReturn(Arrays.asList(role));

        // when
        List<RoleListResponseDto> result = roleService.findAllBySort(sort);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRoleId()).isEqualTo("ROLE_ADMIN");
        assertThat(result.get(0).getRoleName()).isEqualTo("관리자");
        assertThat(result.get(0).getRoleContent()).isEqualTo("시스템 관리자 권한");
        assertThat(result.get(0).getCreatedDate()).isEqualTo(now);
        verify(roleRepository).findAll(sort);
    }
}
