package org.egovframe.cloud.portalservice.service.policy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.egovframe.cloud.common.exception.EntityNotFoundException;
import org.egovframe.cloud.common.util.MessageUtil;
import org.egovframe.cloud.portalservice.api.policy.dto.PolicyResponseDto;
import org.egovframe.cloud.portalservice.api.policy.dto.PolicySaveRequestDto;
import org.egovframe.cloud.portalservice.api.policy.dto.PolicyUpdateRequestDto;
import org.egovframe.cloud.portalservice.domain.policy.Policy;
import org.egovframe.cloud.portalservice.domain.policy.PolicyRepository;
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
 * org.egovframe.cloud.portalservice.service.policy.PolicyServiceTest
 * <p>
 * 이용약관/개인정보수집동의 서비스 단위 테스트 클래스
 *
 * @author 표준프레임워크센터
 * @version 1.0
 * @since 2021/07/06
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/06    표준프레임워크센터  최초 생성
 * </pre>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PolicyServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private MessageUtil messageUtil;

    @InjectMocks
    private PolicyService policyService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(policyService, "messageUtil", messageUtil);
        given(messageUtil.getMessage(anyString())).willReturn("message");
        given(messageUtil.getMessage(anyString(), any(Object[].class))).willReturn("message");
    }

    @DisplayName("이용약관 ID가 주어지면 해당 이용약관을 반환한다")
    @Test
    void should_returnPolicy_when_policyIdIsGiven() {
        // given
        Long id = 1L;
        Policy policy = Policy.builder()
                .type("terms")
                .title("이용약관")
                .isUse(true)
                .regDate(ZonedDateTime.now())
                .contents("이용약관 내용입니다.")
                .build();
        ReflectionTestUtils.setField(policy, "id", id);
        given(policyRepository.findById(id)).willReturn(Optional.of(policy));

        // when
        PolicyResponseDto result = policyService.findById(id);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo("terms");
        assertThat(result.getTitle()).isEqualTo("이용약관");
        assertThat(result.getIsUse()).isTrue();
        verify(policyRepository).findById(id);
    }

    @DisplayName("존재하지 않는 이용약관 ID가 주어지면 EntityNotFoundException을 던진다")
    @Test
    void should_throwEntityNotFoundException_when_policyNotFound() {
        // given
        Long id = 999L;
        given(policyRepository.findById(id)).willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> policyService.findById(id))
                .isInstanceOf(EntityNotFoundException.class);
        verify(policyRepository).findById(id);
    }

    @DisplayName("이용약관 등록 요청이 주어지면 저장 후 ID를 반환한다")
    @Test
    void should_returnSavedId_when_saveRequestDtoIsGiven() {
        // given
        Policy saved = Policy.builder()
                .type("privacy")
                .title("개인정보수집동의")
                .isUse(true)
                .regDate(ZonedDateTime.now())
                .contents("동의 내용")
                .build();
        ReflectionTestUtils.setField(saved, "id", 2L);
        given(policyRepository.save(any(Policy.class))).willReturn(saved);

        PolicySaveRequestDto saveRequestDto = PolicySaveRequestDto.builder()
                .type("privacy")
                .title("개인정보수집동의")
                .isUse(true)
                .regDate(ZonedDateTime.now())
                .contents("동의 내용")
                .build();

        // when
        Long result = policyService.save(saveRequestDto);

        // then
        assertThat(result).isEqualTo(2L);
        verify(policyRepository).save(any(Policy.class));
    }

    @DisplayName("이용약관 수정 요청이 주어지면 수정 후 ID를 반환한다")
    @Test
    void should_returnId_when_updateRequestIsGiven() {
        // given
        Long id = 1L;
        Policy policy = Policy.builder()
                .type("terms")
                .title("기존 이용약관")
                .isUse(true)
                .regDate(ZonedDateTime.now())
                .contents("기존 내용")
                .build();
        ReflectionTestUtils.setField(policy, "id", id);
        given(policyRepository.findById(id)).willReturn(Optional.of(policy));

        PolicyUpdateRequestDto updateRequestDto = PolicyUpdateRequestDto.builder()
                .title("수정된 이용약관")
                .isUse(false)
                .contents("수정된 내용")
                .build();

        // when
        Long result = policyService.update(id, updateRequestDto);

        // then
        assertThat(result).isEqualTo(id);
        assertThat(policy.getTitle()).isEqualTo("수정된 이용약관");
        assertThat(policy.getIsUse()).isFalse();
    }

    @DisplayName("사용여부 toggle 시 정상적으로 변경된다")
    @Test
    void should_toggleIsUse_when_updateIsUseIsRequested() {
        // given
        Long id = 1L;
        Policy policy = Policy.builder()
                .type("terms")
                .title("이용약관")
                .isUse(true)
                .regDate(ZonedDateTime.now())
                .contents("내용")
                .build();
        ReflectionTestUtils.setField(policy, "id", id);
        given(policyRepository.findById(id)).willReturn(Optional.of(policy));

        // when
        Long result = policyService.updateIsUse(id, false);

        // then
        assertThat(result).isEqualTo(id);
        assertThat(policy.getIsUse()).isFalse();
    }

    @DisplayName("이용약관 삭제 시 존재하지 않는 ID이면 EntityNotFoundException을 던진다")
    @Test
    void should_throwEntityNotFoundException_when_deleteNonExistentPolicy() {
        // given
        Long id = 999L;
        given(policyRepository.findById(id)).willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> policyService.delete(id))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
