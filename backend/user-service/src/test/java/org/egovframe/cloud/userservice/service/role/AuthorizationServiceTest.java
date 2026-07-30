package org.egovframe.cloud.userservice.service.role;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Optional;

import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.common.exception.EntityNotFoundException;
import org.egovframe.cloud.common.util.MessageUtil;
import org.egovframe.cloud.userservice.api.role.dto.AuthorizationListResponseDto;
import org.egovframe.cloud.userservice.api.role.dto.AuthorizationResponseDto;
import org.egovframe.cloud.userservice.api.role.dto.AuthorizationSaveRequestDto;
import org.egovframe.cloud.userservice.api.role.dto.AuthorizationUpdateRequestDto;
import org.egovframe.cloud.userservice.domain.role.Authorization;
import org.egovframe.cloud.userservice.domain.role.AuthorizationRepository;
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
 * org.egovframe.cloud.userservice.service.role.AuthorizationServiceTest
 * <p>
 * 인가 서비스 단위 테스트 클래스
 *
 * @author 표준프레임워크센터
 * @version 1.0
 * @since 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthorizationServiceTest {

    @Mock
    private AuthorizationRepository authorizationRepository;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache useridCache;

    @Mock
    private Cache rolesCache;

    @Mock
    private MessageUtil messageUtil;

    @InjectMocks
    private AuthorizationService authorizationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authorizationService, "messageUtil", messageUtil);
        given(messageUtil.getMessage(anyString())).willReturn("message");
        given(messageUtil.getMessage(anyString(), any(Object[].class))).willReturn("message");

        given(cacheManager.getCache("cache-user-authorization-by-userid")).willReturn(useridCache);
        given(cacheManager.getCache("cache-user-authorization-by-roles")).willReturn(rolesCache);
    }

    @DisplayName("조회 조건에 일치하는 인가 페이지 목록을 조회한다")
    @Test
    void should_returnAuthorizationPage_when_validRequestDto() {
        // given
        RequestDto requestDto = new RequestDto();
        Pageable pageable = PageRequest.of(0, 10);
        AuthorizationListResponseDto responseDto = new AuthorizationListResponseDto(
                1, "사용자 조회", "/api/v1/users", "GET", 1
        );
        Page<AuthorizationListResponseDto> expectedPage = new PageImpl<>(Arrays.asList(responseDto), pageable, 1);
        given(authorizationRepository.findPage(requestDto, pageable)).willReturn(expectedPage);

        // when
        Page<AuthorizationListResponseDto> result = authorizationService.findPage(requestDto, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getAuthorizationNo()).isEqualTo(1);
        verify(authorizationRepository).findPage(requestDto, pageable);
    }

    @DisplayName("존재하는 인가 번호로 단건 조회 시 인가 정보 DTO를 반환한다")
    @Test
    void should_returnAuthorizationResponseDto_when_existsAuthorizationNo() {
        // given
        Integer authorizationNo = 1;
        Authorization authorization = Authorization.builder()
                .authorizationNo(authorizationNo)
                .authorizationName("사용자 조회")
                .urlPatternValue("/api/v1/users")
                .httpMethodCode("GET")
                .sortSeq(1)
                .build();
        given(authorizationRepository.findById(authorizationNo)).willReturn(Optional.of(authorization));

        // when
        AuthorizationResponseDto result = authorizationService.findById(authorizationNo);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAuthorizationNo()).isEqualTo(authorizationNo);
        assertThat(result.getAuthorizationName()).isEqualTo("사용자 조회");
    }

    @DisplayName("존재하지 않는 인가 번호로 조회 시 EntityNotFoundException이 발생한다")
    @Test
    void should_throwEntityNotFoundException_when_notExistsAuthorizationNo() {
        // given
        Integer authorizationNo = 999;
        given(authorizationRepository.findById(authorizationNo)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authorizationService.findById(authorizationNo))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("인가 다음 정렬 순서를 조회한다")
    @Test
    void should_returnNextSortSeq_when_requested() {
        // given
        given(authorizationRepository.findNextSortSeq()).willReturn(5);

        // when
        Integer result = authorizationService.findNextSortSeq();

        // then
        assertThat(result).isEqualTo(5);
        verify(authorizationRepository).findNextSortSeq();
    }

    @DisplayName("동일한 정렬 순서가 존재하지 않을 때 인가를 등록하고 캐시를 비운다")
    @Test
    void should_saveAuthorizationWithoutConflict_and_clearCache() {
        // given
        AuthorizationSaveRequestDto requestDto = new AuthorizationSaveRequestDto();
        ReflectionTestUtils.setField(requestDto, "authorizationName", "사용자 등록");
        ReflectionTestUtils.setField(requestDto, "urlPatternValue", "/api/v1/users");
        ReflectionTestUtils.setField(requestDto, "httpMethodCode", "POST");
        ReflectionTestUtils.setField(requestDto, "sortSeq", 2);

        given(authorizationRepository.findBySortSeq(2)).willReturn(Optional.empty());
        Authorization savedEntity = Authorization.builder()
                .authorizationNo(10)
                .authorizationName("사용자 등록")
                .urlPatternValue("/api/v1/users")
                .httpMethodCode("POST")
                .sortSeq(2)
                .build();
        given(authorizationRepository.save(any(Authorization.class))).willReturn(savedEntity);

        // when
        AuthorizationResponseDto result = authorizationService.save(requestDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAuthorizationNo()).isEqualTo(10);
        verify(authorizationRepository, never()).updateSortSeq(anyInt(), any(), anyInt());
        verify(authorizationRepository).save(any(Authorization.class));
        verify(useridCache).clear();
        verify(rolesCache).clear();
    }

    @DisplayName("동일한 정렬 순서가 존재할 때 정렬 순서를 1씩 미루고 인가를 등록한 뒤 캐시를 비운다")
    @Test
    void should_adjustSortSeqAndSaveAuthorization_and_clearCache() {
        // given
        AuthorizationSaveRequestDto requestDto = new AuthorizationSaveRequestDto();
        ReflectionTestUtils.setField(requestDto, "authorizationName", "사용자 등록");
        ReflectionTestUtils.setField(requestDto, "urlPatternValue", "/api/v1/users");
        ReflectionTestUtils.setField(requestDto, "httpMethodCode", "POST");
        ReflectionTestUtils.setField(requestDto, "sortSeq", 2);

        Authorization existingConflicting = Authorization.builder()
                .authorizationNo(5)
                .sortSeq(2)
                .build();
        given(authorizationRepository.findBySortSeq(2)).willReturn(Optional.of(existingConflicting));

        Authorization savedEntity = Authorization.builder()
                .authorizationNo(10)
                .authorizationName("사용자 등록")
                .urlPatternValue("/api/v1/users")
                .httpMethodCode("POST")
                .sortSeq(2)
                .build();
        given(authorizationRepository.save(any(Authorization.class))).willReturn(savedEntity);

        // when
        AuthorizationResponseDto result = authorizationService.save(requestDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAuthorizationNo()).isEqualTo(10);
        verify(authorizationRepository).updateSortSeq(2, null, 1);
        verify(authorizationRepository).save(any(Authorization.class));
        verify(useridCache).clear();
        verify(rolesCache).clear();
    }

    @DisplayName("인가 수정 시 정렬 순서가 앞당겨지면 사이 구간 정렬 순서를 1씩 늘린다")
    @Test
    void should_increaseSortSeqBetweenRange_when_sortSeqIsAdvanced() {
        // given
        Integer authorizationNo = 1;
        Authorization existingEntity = Authorization.builder()
                .authorizationNo(authorizationNo)
                .authorizationName("사용자 조회")
                .urlPatternValue("/api/v1/users")
                .httpMethodCode("GET")
                .sortSeq(5)
                .build();
        given(authorizationRepository.findById(authorizationNo)).willReturn(Optional.of(existingEntity));

        AuthorizationUpdateRequestDto requestDto = AuthorizationUpdateRequestDto.builder()
                .authorizationName("사용자 조회 수정")
                .urlPatternValue("/api/v1/users")
                .httpMethodCode("GET")
                .sortSeq(3)
                .build();

        // when
        authorizationService.update(authorizationNo, requestDto);

        // then
        verify(authorizationRepository).updateSortSeq(3, 4, 1);
        verify(useridCache).clear();
        verify(rolesCache).clear();
    }

    @DisplayName("인가 수정 시 정렬 순서가 밀려나면 사이 구간 정렬 순서를 1씩 줄인다")
    @Test
    void should_decreaseSortSeqBetweenRange_when_sortSeqIsPostponed() {
        // given
        Integer authorizationNo = 1;
        Authorization existingEntity = Authorization.builder()
                .authorizationNo(authorizationNo)
                .authorizationName("사용자 조회")
                .urlPatternValue("/api/v1/users")
                .httpMethodCode("GET")
                .sortSeq(3)
                .build();
        given(authorizationRepository.findById(authorizationNo)).willReturn(Optional.of(existingEntity));

        AuthorizationUpdateRequestDto requestDto = AuthorizationUpdateRequestDto.builder()
                .authorizationName("사용자 조회 수정")
                .urlPatternValue("/api/v1/users")
                .httpMethodCode("GET")
                .sortSeq(5)
                .build();

        // when
        authorizationService.update(authorizationNo, requestDto);

        // then
        verify(authorizationRepository).updateSortSeq(4, 5, -1);
        verify(useridCache).clear();
        verify(rolesCache).clear();
    }

    @DisplayName("인가 삭제 시 삭제 데이터보다 큰 정렬 순서를 1씩 줄이고 캐시를 비운다")
    @Test
    void should_decreaseLargerSortSeq_and_clearCache_when_deleted() {
        // given
        Integer authorizationNo = 1;
        Authorization existingEntity = Authorization.builder()
                .authorizationNo(authorizationNo)
                .authorizationName("사용자 조회")
                .urlPatternValue("/api/v1/users")
                .httpMethodCode("GET")
                .sortSeq(3)
                .build();
        given(authorizationRepository.findById(authorizationNo)).willReturn(Optional.of(existingEntity));

        // when
        authorizationService.delete(authorizationNo);

        // then
        verify(authorizationRepository).delete(existingEntity);
        verify(authorizationRepository).updateSortSeq(4, null, -1);
        verify(useridCache).clear();
        verify(rolesCache).clear();
    }
}
