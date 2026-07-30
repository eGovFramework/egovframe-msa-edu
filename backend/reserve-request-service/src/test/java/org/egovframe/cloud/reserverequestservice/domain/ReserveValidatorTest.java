package org.egovframe.cloud.reserverequestservice.domain;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.egovframe.cloud.common.exception.BusinessMessageException;
import org.egovframe.cloud.common.util.MessageUtil;
import org.egovframe.cloud.reserverequestservice.api.dto.ReserveSaveRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

/**
 * ReserveValidator 단위 테스트
 *
 * 장비 예약 재고 검증(checkEquipment)의 날짜 겹침 판정을 검증한다.
 */
@ExtendWith(MockitoExtension.class)
class ReserveValidatorTest {

    @Mock
    private ReserveRepository reserveRepository;

    @Mock
    private MessageUtil messageUtil;

    private ReserveValidator reserveValidator;

    private static final LocalDateTime QUERY_DATE = LocalDateTime.of(2026, 6, 1, 10, 0);

    @BeforeEach
    void setUp() {
        reserveValidator = new ReserveValidator(reserveRepository);
        setField(reserveValidator, "messageUtil", messageUtil);
        lenient().when(messageUtil.getMessage(anyString())).thenReturn("message");
        lenient().when(messageUtil.getMessage(anyString(), any())).thenReturn("message");
    }

    /** 순수 리플렉션으로 비공개 필드를 주입한다(테스트 인프라 로깅 초기화 의존 회피). */
    private static void setField(Object target, String name, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    private ReserveSaveRequestDto equipmentDto(int totalQty, int reserveQty) {
        // 조회 날짜를 단일 시점(reserveStartDate == reserveEndDate)으로 두어
        // getMaxByReserveDate의 between == 0 분기를 태운다.
        return ReserveSaveRequestDto.builder()
            .categoryId("equipment")
            .reserveItemId(1L)
            .reserveMeansId("realtime")
            .requestStartDate(LocalDateTime.of(2026, 1, 1, 0, 0))
            .requestEndDate(LocalDateTime.of(2026, 12, 31, 0, 0))
            .reserveStartDate(QUERY_DATE)
            .reserveEndDate(QUERY_DATE)
            .isPeriod(false)
            .totalQty(totalQty)
            .reserveQty(reserveQty)
            .build();
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
    void 조회날짜와_겹치지_않는_예약은_수량에_합산되지_않는다() {
        // 예약 기간(5/1~5/2)이 조회 날짜(6/1)보다 모두 과거라 겹치지 않는다.
        when(reserveRepository.findAllByReserveDate(anyLong(), any(), any()))
            .thenReturn(Flux.just(reserve(
                LocalDateTime.of(2026, 5, 1, 0, 0),
                LocalDateTime.of(2026, 5, 2, 0, 0),
                5)));

        // max가 0이어야 totalQty(10) - 0 >= reserveQty(8)로 검증을 통과한다.
        ReserveSaveRequestDto dto = equipmentDto(10, 8);

        StepVerifier.create(reserveValidator.checkEquipment(dto))
            .expectNext(dto)
            .verifyComplete();
    }

    @Test
    void 조회날짜와_겹치는_예약은_수량에_합산된다() {
        // 예약 기간(5/30~6/2)이 조회 날짜(6/1)를 포함하여 겹친다.
        when(reserveRepository.findAllByReserveDate(anyLong(), any(), any()))
            .thenReturn(Flux.just(reserve(
                LocalDateTime.of(2026, 5, 30, 0, 0),
                LocalDateTime.of(2026, 6, 2, 0, 0),
                5)));

        // max가 5이므로 totalQty(10) - 5 = 5 < reserveQty(8)로 재고 부족 오류가 발생한다.
        ReserveSaveRequestDto dto = equipmentDto(10, 8);

        StepVerifier.create(reserveValidator.checkEquipment(dto))
            .expectError(BusinessMessageException.class)
            .verify();
    }

    @Test
    void 예약이_없으면_재고_전체가_가용하다() {
        when(reserveRepository.findAllByReserveDate(anyLong(), any(), any()))
            .thenReturn(Flux.empty());

        ReserveSaveRequestDto dto = equipmentDto(10, 8);

        StepVerifier.create(reserveValidator.checkEquipment(dto))
            .expectNext(dto)
            .verifyComplete();
    }
}
