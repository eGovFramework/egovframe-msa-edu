package org.egovframe.cloud.reservechecksevice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.egovframe.cloud.common.exception.BusinessMessageException;
import org.egovframe.cloud.common.util.MessageUtil;
import org.egovframe.cloud.reservechecksevice.api.dto.ReserveCancelRequestDto;
import org.egovframe.cloud.reservechecksevice.client.ReserveItemServiceClient;
import org.egovframe.cloud.reservechecksevice.domain.Reserve;
import org.egovframe.cloud.reservechecksevice.domain.ReserveRepository;
import org.egovframe.cloud.reservechecksevice.domain.ReserveStatus;
import org.egovframe.cloud.reservechecksevice.domain.ReserveValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.test.util.ReflectionTestUtils;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * 승인·취소 경로가 "findById 로 읽은 값을 메모리에서 확인"하는 대신
 * {@link ReserveRepository#updateStatusIfCurrentStatusIn}가 돌려주는 반영 행 수로 판단하는지를 검증한다.
 * <p>
 * 여기서 검증하는 것은 서비스 계층의 분기(0건이면 차단하고 원격 재고 호출을 절대 실행하지 않는다)이다.
 * DB가 실제로 동시 호출 중 한쪽에만 1을, 다른 쪽에 0을 돌려준다는 원자성 자체는 Mockito로는 증명할 수
 * 없다 - 그 부분은 {@code ReserveRepositoryImplConcurrencyTest}가 실제 H2 DB로 별도로 증명한다.
 * 이 테스트는 그 결과(0건 반영)를 서비스가 받았을 때 재고 반영을 차단하는지를 확인한다.
 */
class ReserveServiceStatusGuardTest {

    private static final List<String> CANCELLABLE_STATUSES =
        List.of(ReserveStatus.REQUEST.getKey(), ReserveStatus.APPROVE.getKey());

    private ReserveRepository reserveRepository;
    private ReserveItemServiceClient reserveItemServiceClient;
    private ReserveValidator validator;
    private ReserveService service;

    @BeforeEach
    void setUp() {
        reserveRepository = mock(ReserveRepository.class);
        reserveItemServiceClient = mock(ReserveItemServiceClient.class);
        validator = mock(ReserveValidator.class);
        CircuitBreakerRegistry circuitBreakerRegistry = CircuitBreakerRegistry.ofDefaults();
        StreamBridge streamBridge = mock(StreamBridge.class);

        service = new ReserveService(reserveRepository, reserveItemServiceClient, circuitBreakerRegistry, streamBridge, validator);

        MessageUtil messageUtil = mock(MessageUtil.class);
        when(messageUtil.getMessage(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        when(messageUtil.getMessage(anyString(), any(Object[].class))).thenAnswer(invocation -> invocation.getArgument(0));
        ReflectionTestUtils.setField(service, "messageUtil", messageUtil);
    }

    private Reserve educationReserve(String status) {
        return Reserve.builder()
            .reserveId("r1")
            .reserveItemId(1L)
            .categoryId("education")
            .reserveQty(4)
            .reservePurposeContent("test")
            .reserveStartDate(LocalDateTime.of(2026, 3, 1, 0, 0))
            .reserveEndDate(LocalDateTime.of(2026, 3, 2, 0, 0))
            .reserveStatusId(status)
            .userId("user")
            .userEmail("user@email.com")
            .userContactNo("contact")
            .build();
    }

    @SuppressWarnings("unchecked")
    private Mono<Reserve> invokeCheckApprove(String reserveId) {
        return (Mono<Reserve>) ReflectionTestUtils.invokeMethod(service, "checkApprove", reserveId);
    }

    @SuppressWarnings("unchecked")
    private Mono<Void> invokeReserveCancel(String reserveId, ReserveCancelRequestDto dto) {
        return (Mono<Void>) ReflectionTestUtils.invokeMethod(service, "reserveCancel", reserveId, dto);
    }

    @Test
    @DisplayName("승인 - 조건부 업데이트가 1건 반영되면 재고를 반영하고 상태를 승인으로 바꾼다")
    void checkApprove_updatesInventory_whenConditionalUpdateAffectsOneRow() {
        when(reserveRepository.findById("r1")).thenReturn(Mono.just(educationReserve(ReserveStatus.REQUEST.getKey())));
        when(validator.checkReserveItems(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(reserveRepository.updateStatusIfCurrentStatusIn("r1", List.of(ReserveStatus.REQUEST.getKey()), ReserveStatus.APPROVE.getKey()))
            .thenReturn(Mono.just(1L));
        when(reserveItemServiceClient.updateInventory(anyLong(), anyInt())).thenReturn(Mono.just(true));

        StepVerifier.create(invokeCheckApprove("r1"))
            .assertNext(reserve -> assertThat(reserve.getReserveStatusId())
                .isEqualTo(ReserveStatus.APPROVE.getKey()))
            .verifyComplete();

        verify(reserveItemServiceClient, times(1)).updateInventory(anyLong(), anyInt());
    }

    @Test
    @DisplayName("승인 - 조건부 업데이트가 0건이면(이미 승인된 예약) 재고를 반영하지 않고 예외를 던진다")
    void checkApprove_neverTouchesInventory_whenConditionalUpdateAffectsNoRow() {
        when(reserveRepository.findById("r1")).thenReturn(Mono.just(educationReserve(ReserveStatus.REQUEST.getKey())));
        when(validator.checkReserveItems(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // 실제 DB에서라면 첫 승인 호출이 이미 반영해 두 번째 호출은 0건을 돌려주는 상황을 흉내낸다.
        when(reserveRepository.updateStatusIfCurrentStatusIn("r1", List.of(ReserveStatus.REQUEST.getKey()), ReserveStatus.APPROVE.getKey()))
            .thenReturn(Mono.just(0L));

        StepVerifier.create(invokeCheckApprove("r1"))
            .expectErrorMatches(throwable -> throwable instanceof BusinessMessageException
                && "valid.reserve_not_approve_status".equals(throwable.getMessage()))
            .verify();

        verify(reserveItemServiceClient, never()).updateInventory(anyLong(), anyInt());
    }

    @Test
    @DisplayName("취소 - 조건부 업데이트가 1건 반영되면 재고를 되돌리고 취소 사유를 반영한다")
    void reserveCancel_updatesInventory_whenConditionalUpdateAffectsOneRow() {
        Reserve stored = educationReserve(ReserveStatus.APPROVE.getKey());
        when(reserveRepository.findById("r1")).thenReturn(Mono.just(stored));
        when(reserveRepository.updateStatusIfCurrentStatusIn("r1", CANCELLABLE_STATUSES, ReserveStatus.CANCEL.getKey()))
            .thenReturn(Mono.just(1L));
        when(reserveItemServiceClient.updateInventory(anyLong(), anyInt())).thenReturn(Mono.just(true));
        when(reserveRepository.save(any(Reserve.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        ReserveCancelRequestDto cancelRequestDto = ReserveCancelRequestDto.builder()
            .reasonCancelContent("more urgent conflict")
            .build();

        StepVerifier.create(invokeReserveCancel("r1", cancelRequestDto))
            .verifyComplete();

        verify(reserveItemServiceClient, times(1)).updateInventory(anyLong(), anyInt());
        verify(reserveRepository, times(1)).save(any(Reserve.class));
    }

    @Test
    @DisplayName("취소 - 조건부 업데이트가 0건이고 완료 상태면 완료 사유로, 그 외에는 이미 취소된 사유로 구분해 예외를 던진다")
    void reserveCancel_neverTouchesInventory_whenConditionalUpdateAffectsNoRow() {
        // 완료 상태
        when(reserveRepository.findById("r1")).thenReturn(Mono.just(educationReserve(ReserveStatus.DONE.getKey())));
        when(reserveRepository.updateStatusIfCurrentStatusIn("r1", CANCELLABLE_STATUSES, ReserveStatus.CANCEL.getKey()))
            .thenReturn(Mono.just(0L));

        StepVerifier.create(invokeReserveCancel("r1", ReserveCancelRequestDto.builder().reasonCancelContent("x").build()))
            .expectErrorMatches(throwable -> throwable instanceof BusinessMessageException
                && "valid.cant_cancel_because_done".equals(throwable.getMessage()))
            .verify();

        // 이미 취소된 상태(혹은 동시 요청으로 취소 가능 상태를 벗어난 경우)
        when(reserveRepository.findById("r1")).thenReturn(Mono.just(educationReserve(ReserveStatus.CANCEL.getKey())));

        StepVerifier.create(invokeReserveCancel("r1", ReserveCancelRequestDto.builder().reasonCancelContent("x").build()))
            .expectErrorMatches(throwable -> throwable instanceof BusinessMessageException
                && "valid.cant_cancel_because_cancel".equals(throwable.getMessage()))
            .verify();

        verify(reserveItemServiceClient, never()).updateInventory(anyLong(), anyInt());
    }
}
