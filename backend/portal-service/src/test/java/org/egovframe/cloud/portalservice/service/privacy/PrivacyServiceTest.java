package org.egovframe.cloud.portalservice.service.privacy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.egovframe.cloud.common.exception.EntityNotFoundException;
import org.egovframe.cloud.common.util.MessageUtil;
import org.egovframe.cloud.portalservice.api.privacy.dto.PrivacyResponseDto;
import org.egovframe.cloud.portalservice.api.privacy.dto.PrivacySaveRequestDto;
import org.egovframe.cloud.portalservice.domain.privacy.Privacy;
import org.egovframe.cloud.portalservice.domain.privacy.PrivacyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * org.egovframe.cloud.portalservice.service.privacy.PrivacyServiceTest
 * <p>
 * 개인정보처리방침 서비스 단위 테스트 클래스
 *
 * @author 표준프레임워크센터
 * @version 1.0
 * @since 2021/07/23
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/23    표준프레임워크센터  최초 생성
 * </pre>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PrivacyServiceTest {

    @Mock
    private PrivacyRepository privacyRepository;

    @Mock
    private MessageUtil messageUtil;

    @InjectMocks
    private PrivacyService privacyService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(privacyService, "messageUtil", messageUtil);
        given(messageUtil.getMessage(anyString())).willReturn("message");
        given(messageUtil.getMessage(anyString(), any(Object[].class))).willReturn("message");
    }

    @DisplayName("개인정보처리방침 번호가 주어지면 해당 방침을 반환한다")
    @Test
    void should_returnPrivacy_when_privacyNoIsGiven() {
        // given
        Integer privacyNo = 1;
        Privacy privacy = Privacy.builder()
                .privacyNo(privacyNo)
                .privacyTitle("2024년 개인정보처리방침")
                .privacyContent("개인정보처리방침 내용입니다.")
                .useAt(true)
                .build();
        given(privacyRepository.findById(privacyNo)).willReturn(Optional.of(privacy));

        // when
        PrivacyResponseDto result = privacyService.findById(privacyNo);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPrivacyNo()).isEqualTo(privacyNo);
        assertThat(result.getPrivacyTitle()).isEqualTo("2024년 개인정보처리방침");
        assertThat(result.getPrivacyContent()).isEqualTo("개인정보처리방침 내용입니다.");
        assertThat(result.getUseAt()).isTrue();
        verify(privacyRepository).findById(privacyNo);
    }

    @DisplayName("존재하지 않는 개인정보처리방침 번호가 주어지면 EntityNotFoundException을 던진다")
    @Test
    void should_throwEntityNotFoundException_when_privacyNotFound() {
        // given
        Integer privacyNo = 999;
        given(privacyRepository.findById(privacyNo)).willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> privacyService.findById(privacyNo))
                .isInstanceOf(EntityNotFoundException.class);
        verify(privacyRepository).findById(privacyNo);
    }

    @DisplayName("사용여부로 조회하면 해당 목록을 반환한다")
    @Test
    void should_returnPrivacyList_when_useAtIsGiven() {
        // given
        PrivacyResponseDto dto1 = new PrivacyResponseDto(1, "방침1", "내용1", true);
        PrivacyResponseDto dto2 = new PrivacyResponseDto(2, "방침2", "내용2", true);
        given(privacyRepository.findAllByUseAt(true)).willReturn(Arrays.asList(dto1, dto2));

        // when
        List<PrivacyResponseDto> result = privacyService.findAllByUseAt(true);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getPrivacyTitle()).isEqualTo("방침1");
        assertThat(result.get(1).getPrivacyTitle()).isEqualTo("방침2");
        verify(privacyRepository).findAllByUseAt(true);
    }

    @DisplayName("개인정보처리방침 등록 요청이 주어지면 저장 후 응답 DTO를 반환한다")
    @Test
    void should_returnSavedPrivacy_when_saveRequestDtoIsGiven() {
        // given
        Privacy saved = Privacy.builder()
                .privacyNo(1)
                .privacyTitle("신규 방침")
                .privacyContent("신규 내용")
                .useAt(true)
                .build();
        given(privacyRepository.save(any(Privacy.class))).willReturn(saved);

        PrivacySaveRequestDto saveRequestDto = new PrivacySaveRequestDto();
        ReflectionTestUtils.setField(saveRequestDto, "privacyTitle", "신규 방침");
        ReflectionTestUtils.setField(saveRequestDto, "privacyContent", "신규 내용");
        ReflectionTestUtils.setField(saveRequestDto, "useAt", true);

        // when
        PrivacyResponseDto result = privacyService.save(saveRequestDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPrivacyTitle()).isEqualTo("신규 방침");
        assertThat(result.getUseAt()).isTrue();
        verify(privacyRepository).save(any(Privacy.class));
    }

    @DisplayName("사용여부 수정 시 존재하지 않는 번호이면 EntityNotFoundException을 던진다")
    @Test
    void should_throwEntityNotFoundException_when_updateUseAtForNonExistentPrivacy() {
        // given
        Integer privacyNo = 999;
        given(privacyRepository.findById(privacyNo)).willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> privacyService.updateUseAt(privacyNo, false))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
