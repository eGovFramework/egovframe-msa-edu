package org.egovframe.cloud.reservechecksevice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.egovframe.cloud.common.domain.Role;
import org.egovframe.cloud.common.util.MessageUtil;
import org.egovframe.cloud.reservechecksevice.api.dto.ReserveUpdateRequestDto;
import org.egovframe.cloud.reservechecksevice.client.ReserveItemServiceClient;
import org.egovframe.cloud.reservechecksevice.domain.Reserve;
import org.egovframe.cloud.reservechecksevice.domain.ReserveRepository;
import org.egovframe.cloud.reservechecksevice.domain.ReserveStatus;
import org.egovframe.cloud.reservechecksevice.domain.ReserveValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * 예약 수정이 재고를 다시 차감하지 않는지 검증한다.
 * <p>
 * 수정 경로는 예약 신청 상태만 통과시키는데, 그 상태는 아직 재고가 차감되지 않은 상태다.
 * 심사가 필요한 신청은 reserve-request-service 가 재고를 건드리지 않고 신청 상태로 저장하고,
 * 차감은 승인 시 한 번 일어난다. 수정에서 다시 차감하면 수정 횟수만큼 재고가 더 깎인다.
 */
class ReserveServiceUpdateInventoryTest {

    private static final String RESERVE_ID = "r1";
    private static final Long RESERVE_ITEM_ID = 1L;
    private static final String USER_ID = "user";

    private ReserveRepository reserveRepository;
    private ReserveItemServiceClient reserveItemServiceClient;
    private ReserveValidator validator;
    private ReserveService service;

    @BeforeEach
    void setUp() {
        reserveRepository = mock(ReserveRepository.class);
        reserveItemServiceClient = mock(ReserveItemServiceClient.class);
        validator = mock(ReserveValidator.class);
        StreamBridge streamBridge = mock(StreamBridge.class);
        service = new ReserveService(reserveRepository, reserveItemServiceClient,
            CircuitBreakerRegistry.ofDefaults(), streamBridge, validator);

        MessageUtil messageUtil = mock(MessageUtil.class);
        when(messageUtil.getMessage(anyString())).thenAnswer(i -> i.getArgument(0));
        when(messageUtil.getMessage(anyString(), any(Object[].class))).thenAnswer(i -> i.getArgument(0));
        ReflectionTestUtils.setField(service, "messageUtil", messageUtil);

        when(validator.checkReserveItems(any())).thenAnswer(i -> Mono.just(i.getArgument(0)));
        when(reserveRepository.save(any())).thenAnswer(i -> Mono.just(i.getArgument(0)));
        // 재고 차감이 성공하도록 미리 응답을 준다. 이렇게 해야 수정 경로가 재고를 건드릴 때
        // 흐름이 끝까지 진행되고, 호출 여부만으로 판정이 갈린다.
        when(reserveItemServiceClient.updateInventory(anyLong(), anyInt())).thenReturn(Mono.just(true));
    }

    @Test
    @DisplayName("신청자가 예약을 수정해도 재고를 차감하지 않는다")
    void userUpdateDoesNotTouchInventory() {
        when(reserveRepository.findById(RESERVE_ID))
            .thenReturn(Mono.just(educationReserve(ReserveStatus.REQUEST.getKey(), 4)));

        StepVerifier.create(service.update(RESERVE_ID, updateRequest(6))
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(userAuthentication())))
            .expectNextCount(1)
            .verifyComplete();

        verify(reserveItemServiceClient, never()).updateInventory(anyLong(), anyInt());
    }

    @Test
    @DisplayName("관리자가 예약을 수정해도 재고를 차감하지 않는다")
    void adminUpdateDoesNotTouchInventory() {
        when(reserveRepository.findById(RESERVE_ID))
            .thenReturn(Mono.just(educationReserve(ReserveStatus.REQUEST.getKey(), 4)));

        StepVerifier.create(service.update(RESERVE_ID, updateRequest(6))
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(adminAuthentication())))
            .expectNextCount(1)
            .verifyComplete();

        verify(reserveItemServiceClient, never()).updateInventory(anyLong(), anyInt());
    }

    @Test
    @DisplayName("여러 번 수정한 뒤 승인하면 재고는 마지막 인원으로 한 번만 차감된다")
    void inventoryIsDeductedOnceOnApprovalAfterRepeatedUpdates() {
        when(reserveRepository.findById(RESERVE_ID))
            .thenReturn(Mono.just(educationReserve(ReserveStatus.REQUEST.getKey(), 4)));
        StepVerifier.create(service.update(RESERVE_ID, updateRequest(6))
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(userAuthentication())))
            .expectNextCount(1)
            .verifyComplete();
        StepVerifier.create(service.update(RESERVE_ID, updateRequest(5))
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(userAuthentication())))
            .expectNextCount(1)
            .verifyComplete();

        Reserve approved = educationReserve(ReserveStatus.REQUEST.getKey(), 5);
        when(reserveRepository.updateStatusIfCurrentStatusIn(eq(RESERVE_ID), any(), anyString()))
            .thenReturn(Mono.just(1L));
        when(reserveRepository.findById(RESERVE_ID)).thenReturn(Mono.just(approved));

        StepVerifier.create(service.approve(RESERVE_ID)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(adminAuthentication())))
            .verifyComplete();

        verify(reserveItemServiceClient, times(1)).updateInventory(RESERVE_ITEM_ID, 5);
    }

    private ReserveUpdateRequestDto updateRequest(Integer reserveQty) {
        return ReserveUpdateRequestDto.builder()
            .reserveItemId(RESERVE_ITEM_ID)
            .categoryId("education")
            .reserveQty(reserveQty)
            .reservePurposeContent("test")
            .reserveStartDate(LocalDateTime.of(2026, 3, 1, 0, 0))
            .reserveEndDate(LocalDateTime.of(2026, 3, 2, 0, 0))
            .userId(USER_ID)
            .userContactNo("01000000000")
            .userEmail("user@example.com")
            .build();
    }

    private Reserve educationReserve(String status, Integer reserveQty) {
        return Reserve.builder()
            .reserveId(RESERVE_ID)
            .reserveItemId(RESERVE_ITEM_ID)
            .categoryId("education")
            .reserveQty(reserveQty)
            .reservePurposeContent("test")
            .reserveStartDate(LocalDateTime.of(2026, 3, 1, 0, 0))
            .reserveEndDate(LocalDateTime.of(2026, 3, 2, 0, 0))
            .reserveStatusId(status)
            .userId(USER_ID)
            .userContactNo("01000000000")
            .userEmail("user@example.com")
            .build();
    }

    private Authentication userAuthentication() {
        return new UsernamePasswordAuthenticationToken(USER_ID, "",
            List.of(new SimpleGrantedAuthority(Role.USER.getKey())));
    }

    private Authentication adminAuthentication() {
        return new UsernamePasswordAuthenticationToken("admin", "",
            List.of(new SimpleGrantedAuthority(Role.ADMIN.getKey())));
    }
}
