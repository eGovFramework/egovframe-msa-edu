package org.egovframe.cloud.reservechecksevice.domain;

import static org.mockito.Mockito.mock;

import java.time.LocalDateTime;

import org.egovframe.cloud.common.exception.BusinessMessageException;
import org.egovframe.cloud.common.util.MessageUtil;
import org.egovframe.cloud.reservechecksevice.client.ReserveItemServiceClient;
import org.egovframe.cloud.reservechecksevice.client.dto.ReserveItemResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import reactor.test.StepVerifier;

/**
 * org.egovframe.cloud.reservechecksevice.domain.ReserveValidatorEducationTest
 *
 * 교육 예약 재고 검증(checkEducation) 단위 테스트
 *
 * @author 표준프레임워크센터
 * @version 1.0
 */
class ReserveValidatorEducationTest {

    private ReserveValidator reserveValidator;

    @BeforeEach
    void setUp() {
        MessageUtil messageUtil = mock(MessageUtil.class);
        BDDMockito.given(messageUtil.getMessage(BDDMockito.anyString()))
            .willReturn("message");
        BDDMockito.given(messageUtil.getMessage(BDDMockito.anyString(), BDDMockito.any()))
            .willReturn("message");

        ReserveRepository reserveRepository = mock(ReserveRepository.class);
        ReserveItemServiceClient reserveItemServiceClient = mock(ReserveItemServiceClient.class);
        CircuitBreakerRegistry circuitBreakerRegistry = mock(CircuitBreakerRegistry.class);

        reserveValidator = new ReserveValidator(reserveRepository, reserveItemServiceClient, circuitBreakerRegistry);
        setField(reserveValidator, "messageUtil", messageUtil);
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

    private ReserveItemResponseDto educationItem(int inventoryQty) {
        LocalDateTime now = LocalDateTime.now();
        ReserveItem reserveItem = ReserveItem.builder()
            .reserveItemId(1L)
            .categoryId("education")
            .totalQty(100)
            .inventoryQty(inventoryQty)
            .reserveMeansId("realtime")
            .requestStartDate(now.minusDays(1))
            .requestEndDate(now.plusDays(1))
            .operationStartDate(now.minusDays(1))
            .operationEndDate(now.plusDays(1))
            .build();
        return ReserveItemResponseDto.builder().reserveItem(reserveItem).build();
    }

    private Reserve reserve(int reserveQty) {
        return Reserve.builder()
            .reserveId("reserve-1")
            .reserveItemId(1L)
            .categoryId("education")
            .reserveQty(reserveQty)
            .build();
    }

    @Test
    @DisplayName("재고(수용인원)가 신청 인원 이상이면 예약을 통과시킨다")
    void checkEducation_재고충분_통과() {
        ReserveItemResponseDto reserveItem = educationItem(10);
        Reserve reserve = reserve(5);

        StepVerifier.create(reserveValidator.checkEducation(reserveItem, reserve))
            .expectNext(reserve)
            .verifyComplete();
    }

    @Test
    @DisplayName("재고(수용인원)가 신청 인원과 같으면 예약을 통과시킨다")
    void checkEducation_재고동일_통과() {
        ReserveItemResponseDto reserveItem = educationItem(5);
        Reserve reserve = reserve(5);

        StepVerifier.create(reserveValidator.checkEducation(reserveItem, reserve))
            .expectNext(reserve)
            .verifyComplete();
    }

    @Test
    @DisplayName("재고(수용인원)가 신청 인원보다 적으면 인원 부족 오류가 발생한다")
    void checkEducation_재고부족_오류() {
        ReserveItemResponseDto reserveItem = educationItem(3);
        Reserve reserve = reserve(5);

        StepVerifier.create(reserveValidator.checkEducation(reserveItem, reserve))
            .expectError(BusinessMessageException.class)
            .verify();
    }
}
