package org.egovframe.cloud.userservice.service.role;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;

import org.egovframe.cloud.common.util.MessageUtil;
import org.egovframe.cloud.userservice.api.role.dto.RoleAuthorizationDeleteRequestDto;
import org.egovframe.cloud.userservice.api.role.dto.RoleAuthorizationListRequestDto;
import org.egovframe.cloud.userservice.api.role.dto.RoleAuthorizationListResponseDto;
import org.egovframe.cloud.userservice.api.role.dto.RoleAuthorizationSaveRequestDto;
import org.egovframe.cloud.userservice.domain.role.RoleAuthorization;
import org.egovframe.cloud.userservice.domain.role.RoleAuthorizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * org.egovframe.cloud.userservice.service.role.RoleAuthorizationServiceTest
 * <p>
 * 권한 인가 서비스 단위 테스트 클래스
 *
 * @author 표준프레임워크센터
 * @version 1.0
 * @since 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RoleAuthorizationServiceTest {

    @Mock
    private RoleAuthorizationRepository roleAuthorizationRepository;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache useridCache;

    @Mock
    private Cache rolesCache;

    @Mock
    private MessageUtil messageUtil;

    @InjectMocks
    private RoleAuthorizationService roleAuthorizationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(roleAuthorizationService, "messageUtil", messageUtil);
        given(messageUtil.getMessage(anyString())).willReturn("message");
        given(messageUtil.getMessage(anyString(), any(Object[].class))).willReturn("message");

        given(cacheManager.getCache("cache-user-authorization-by-userid")).willReturn(useridCache);
        given(cacheManager.getCache("cache-user-authorization-by-roles")).willReturn(rolesCache);
    }

    @DisplayName("조회 조건에 일치하는 권한 인가 페이지 목록을 조회한다")
    @Test
    void should_returnRoleAuthorizationPage_when_validRequestDto() {
        // given
        RoleAuthorizationListRequestDto requestDto = new RoleAuthorizationListRequestDto();
        ReflectionTestUtils.setField(requestDto, "roleId", "ROLE_USER");

        Pageable pageable = PageRequest.of(0, 10);
        RoleAuthorizationListResponseDto responseDto = RoleAuthorizationListResponseDto.builder()
                .roleId("ROLE_USER")
                .authorizationNo(1)
                .build();
        Page<RoleAuthorizationListResponseDto> expectedPage = new PageImpl<>(Arrays.asList(responseDto), pageable, 1);
        given(roleAuthorizationRepository.findPageAuthorizationList(requestDto, pageable)).willReturn(expectedPage);

        // when
        Page<RoleAuthorizationListResponseDto> result = roleAuthorizationService.findPageAuthorizationList(requestDto, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getRoleId()).isEqualTo("ROLE_USER");
        assertThat(result.getContent().get(0).getAuthorizationNo()).isEqualTo(1);
        verify(roleAuthorizationRepository).findPageAuthorizationList(requestDto, pageable);
    }

    @DisplayName("roleId가 null이거나 빈 문자열이면 빈 페이지 목록을 반환한다")
    @Test
    void should_returnEmptyPage_when_roleIdIsNullOrEmpty() {
        // given 1: null case
        RoleAuthorizationListRequestDto requestDtoNull = new RoleAuthorizationListRequestDto();
        ReflectionTestUtils.setField(requestDtoNull, "roleId", null);

        Pageable pageable = PageRequest.of(0, 10);

        // when 1
        Page<RoleAuthorizationListResponseDto> resultNull = roleAuthorizationService.findPageAuthorizationList(requestDtoNull, pageable);

        // then 1
        assertThat(resultNull).isNotNull();
        assertThat(resultNull.getContent()).isEmpty();
        verify(roleAuthorizationRepository, never()).findPageAuthorizationList(any(), any());

        // given 2: empty string case
        RoleAuthorizationListRequestDto requestDtoEmpty = new RoleAuthorizationListRequestDto();
        ReflectionTestUtils.setField(requestDtoEmpty, "roleId", "");

        // when 2
        Page<RoleAuthorizationListResponseDto> resultEmpty = roleAuthorizationService.findPageAuthorizationList(requestDtoEmpty, pageable);

        // then 2
        assertThat(resultEmpty).isNotNull();
        assertThat(resultEmpty.getContent()).isEmpty();
        verify(roleAuthorizationRepository, never()).findPageAuthorizationList(any(), any());
    }

    @DisplayName("권한 인가 목록을 다건 등록하고 관련 캐시를 클리어한다")
    @Test
    void should_saveRoleAuthorizations_and_clearCache() {
        // given
        RoleAuthorizationSaveRequestDto saveRequestDto = new RoleAuthorizationSaveRequestDto();
        ReflectionTestUtils.setField(saveRequestDto, "roleId", "ROLE_USER");
        ReflectionTestUtils.setField(saveRequestDto, "authorizationNo", 1);

        List<RoleAuthorizationSaveRequestDto> requestDtoList = Arrays.asList(saveRequestDto);

        RoleAuthorization savedEntity = RoleAuthorization.builder()
                .roleId("ROLE_USER")
                .authorizationNo(1)
                .build();
        given(roleAuthorizationRepository.saveAll(anyList())).willReturn(Arrays.asList(savedEntity));

        // when
        List<RoleAuthorizationListResponseDto> result = roleAuthorizationService.save(requestDtoList);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRoleId()).isEqualTo("ROLE_USER");
        assertThat(result.get(0).getAuthorizationNo()).isEqualTo(1);
        verify(roleAuthorizationRepository).saveAll(anyList());
        verify(useridCache).clear();
        verify(rolesCache).clear();
    }

    @DisplayName("권한 인가 목록을 다건 삭제하고 관련 캐시를 클리어한다")
    @Test
    void should_deleteRoleAuthorizations_and_clearCache() {
        // given
        RoleAuthorizationDeleteRequestDto deleteRequestDto = RoleAuthorizationDeleteRequestDto.builder()
                .roleId("ROLE_USER")
                .authorizationNo(1)
                .build();
        List<RoleAuthorizationDeleteRequestDto> requestDtoList = Arrays.asList(deleteRequestDto);

        // when
        roleAuthorizationService.delete(requestDtoList);

        // then
        verify(roleAuthorizationRepository).deleteAll(anyList());
        verify(useridCache).clear();
        verify(rolesCache).clear();
    }
}
