package org.egovframe.cloud.reservechecksevice.domain;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import org.egovframe.cloud.reservechecksevice.client.ReserveItemServiceClient;

/**
 * org.egovframe.cloud.reservechecksevice.domain.ReserveValidatorCountMaxTest
 *
 * 예약 일자 겹침 기준 최대 사용량(countMax) 판정 단위 테스트.
 * 조회 날짜와 겹치지 않는 예약이 사용량에 합산되지 않아야 한다.
 *
 * @author 표준프레임워크센터
 * @version 1.0
 */
class ReserveValidatorCountMaxTest {

    private ReserveRepository reserveRepository;
    private ReserveValidator reserveValidator;

    private static final LocalDateTime QUERY_DATE = LocalDateTime.of(2026, 6, 1, 10, 0);

    @BeforeEach
    void setUp() {
        reserveRepository = mock(ReserveRepository.class);
        ReserveItemServiceClient reserveItemServiceClient = mock(ReserveItemServiceClient.class);
        CircuitBreakerRegistry circuitBreakerRegistry = mock(CircuitBreakerRegistry.class);
        reserveValidator = new ReserveValidator(reserveRepository, reserveItemServiceClient, circuitBreakerRegistry);
    }

    private Reserve reserve(LocalDateTime start, LocalDateTime end, int qty) {
        return Reserve.builder()
            .reserveItemId(1L)
            .reserveStartDate(start)
            .reserveEndDate(end)
            .reserveQty(qty)
            .build();
    }

    @Test
    @DisplayName("조회 날짜와 겹치지 않는 예약은 사용량에 합산되지 않는다")
    void nonOverlappingReservationIsNotCounted() {
        // 예약 기간(5/1~5/2)이 조회 날짜(6/1)보다 모두 과거라 겹치지 않는다.
        when(reserveRepository.findAllByReserveDate(anyLong(), any(), any()))
            .thenReturn(Flux.just(reserve(
                LocalDateTime.of(2026, 5, 1, 0, 0),
                LocalDateTime.of(2026, 5, 2, 0, 0),
                5)));

        StepVerifier.create(reserveValidator.getMaxByReserveDate(1L, QUERY_DATE, QUERY_DATE))
            .expectNext(0)
            .verifyComplete();
    }

    @Test
    @DisplayName("조회 날짜와 겹치는 예약은 사용량에 합산된다")
    void overlappingReservationIsCounted() {
        // 예약 기간(5/30~6/2)이 조회 날짜(6/1)를 포함하여 겹친다.
        when(reserveRepository.findAllByReserveDate(anyLong(), any(), any()))
            .thenReturn(Flux.just(reserve(
                LocalDateTime.of(2026, 5, 30, 0, 0),
                LocalDateTime.of(2026, 6, 2, 0, 0),
                5)));

        StepVerifier.create(reserveValidator.getMaxByReserveDate(1L, QUERY_DATE, QUERY_DATE))
            .expectNext(5)
            .verifyComplete();
    }

    @Test
    @DisplayName("예약이 없으면 사용량은 0이다")
    void emptyReservationsCountsZero() {
        when(reserveRepository.findAllByReserveDate(anyLong(), any(), any()))
            .thenReturn(Flux.empty());

        StepVerifier.create(reserveValidator.getMaxByReserveDate(1L, QUERY_DATE, QUERY_DATE))
            .expectNext(0)
            .verifyComplete();
    }
}
