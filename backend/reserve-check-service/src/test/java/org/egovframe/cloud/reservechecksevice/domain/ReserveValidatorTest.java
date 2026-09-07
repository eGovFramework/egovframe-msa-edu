package org.egovframe.cloud.reservechecksevice.domain;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.egovframe.cloud.common.exception.BusinessMessageException;
import org.egovframe.cloud.common.util.MessageUtil;
import org.egovframe.cloud.reservechecksevice.client.ReserveItemServiceClient;
import org.egovframe.cloud.reservechecksevice.client.dto.ReserveItemResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ReserveValidatorTest {

    @InjectMocks
    private ReserveValidator reserveValidator;

    @Mock
    private ReserveRepository reserveRepository;

    @Mock
    private ReserveItemServiceClient reserveItemServiceClient;

    @Mock
    private CircuitBreakerRegistry circuitBreakerRegistry;

    private CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("reserve-item");

    @Mock
    private MessageUtil messageUtil;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(reserveValidator, "messageUtil", messageUtil);
        
        when(messageUtil.getMessage(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        when(messageUtil.getMessage(anyString(), any(Object[].class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void checkSpace_성공() {
        ReserveItem item = ReserveItem.builder()
                .reserveItemId(1L)
                .categoryId("space")
                .reserveMeansId("realtime")
                .requestStartDate(LocalDateTime.now().minusDays(1))
                .requestEndDate(LocalDateTime.now().plusDays(5))
                .isPeriod(false)
                .build();

        ReserveItemResponseDto reserveItemDto = ReserveItemResponseDto.builder()
                .reserveItem(item)
                .build();

        Reserve reserve = Reserve.builder()
                .reserveId("reserve-1")
                .reserveItemId(1L)
                .reserveStartDate(LocalDateTime.now())
                .reserveEndDate(LocalDateTime.now().plusDays(1))
                .build();

        when(reserveRepository.findAllByReserveDateWithoutSelfCount(
                "reserve-1", 1L, reserve.getReserveStartDate(), reserve.getReserveEndDate()))
                .thenReturn(Mono.just(0L));

        Mono<Reserve> result = reserveValidator.checkSpace(reserveItemDto, reserve);

        StepVerifier.create(result)
                .expectNext(reserve)
                .verifyComplete();
    }

    @Test
    void checkSpace_실패_중복예약존재() {
        ReserveItem item = ReserveItem.builder()
                .reserveItemId(1L)
                .categoryId("space")
                .reserveMeansId("realtime")
                .requestStartDate(LocalDateTime.now().minusDays(1))
                .requestEndDate(LocalDateTime.now().plusDays(5))
                .isPeriod(false)
                .build();

        ReserveItemResponseDto reserveItemDto = ReserveItemResponseDto.builder()
                .reserveItem(item)
                .build();

        Reserve reserve = Reserve.builder()
                .reserveId("reserve-1")
                .reserveItemId(1L)
                .reserveStartDate(LocalDateTime.now())
                .reserveEndDate(LocalDateTime.now().plusDays(1))
                .build();

        when(reserveRepository.findAllByReserveDateWithoutSelfCount(
                "reserve-1", 1L, reserve.getReserveStartDate(), reserve.getReserveEndDate()))
                .thenReturn(Mono.just(1L));

        Mono<Reserve> result = reserveValidator.checkSpace(reserveItemDto, reserve);

        StepVerifier.create(result)
                .expectError(BusinessMessageException.class)
                .verify();
    }

    @Test
    void checkEquipment_성공() {
        ReserveItem item = ReserveItem.builder()
                .reserveItemId(1L)
                .categoryId("equipment")
                .reserveMeansId("realtime")
                .requestStartDate(LocalDateTime.now().minusDays(1))
                .requestEndDate(LocalDateTime.now().plusDays(5))
                .isPeriod(false)
                .totalQty(10)
                .build();

        ReserveItemResponseDto reserveItemDto = ReserveItemResponseDto.builder()
                .reserveItem(item)
                .build();

        Reserve reserve = Reserve.builder()
                .reserveId("reserve-1")
                .reserveItemId(1L)
                .reserveQty(3)
                .reserveStartDate(LocalDateTime.now())
                .reserveEndDate(LocalDateTime.now().plusDays(1))
                .build();

        Reserve existingReserve = Reserve.builder()
                .reserveQty(2)
                .reserveStartDate(LocalDateTime.now().minusHours(1))
                .reserveEndDate(LocalDateTime.now().plusHours(1))
                .build();

        when(reserveRepository.findAllByReserveDateWithoutSelf(
                "reserve-1", 1L, reserve.getReserveStartDate(), reserve.getReserveEndDate()))
                .thenReturn(Flux.just(existingReserve));

        Mono<Reserve> result = reserveValidator.checkEquipment(reserveItemDto, reserve);

        StepVerifier.create(result)
                .expectNext(reserve)
                .verifyComplete();
    }

    @Test
    void checkEquipment_실패_재고부족() {
        ReserveItem item = ReserveItem.builder()
                .reserveItemId(1L)
                .categoryId("equipment")
                .reserveMeansId("realtime")
                .requestStartDate(LocalDateTime.now().minusDays(1))
                .requestEndDate(LocalDateTime.now().plusDays(5))
                .isPeriod(false)
                .totalQty(10)
                .build();

        ReserveItemResponseDto reserveItemDto = ReserveItemResponseDto.builder()
                .reserveItem(item)
                .build();

        Reserve reserve = Reserve.builder()
                .reserveId("reserve-1")
                .reserveItemId(1L)
                .reserveQty(9)
                .reserveStartDate(LocalDateTime.now())
                .reserveEndDate(LocalDateTime.now().plusDays(1))
                .build();

        Reserve existingReserve = Reserve.builder()
                .reserveQty(2)
                .reserveStartDate(LocalDateTime.now().minusHours(1))
                .reserveEndDate(LocalDateTime.now().plusHours(1))
                .build();

        when(reserveRepository.findAllByReserveDateWithoutSelf(
                "reserve-1", 1L, reserve.getReserveStartDate(), reserve.getReserveEndDate()))
                .thenReturn(Flux.just(existingReserve));

        Mono<Reserve> result = reserveValidator.checkEquipment(reserveItemDto, reserve);

        StepVerifier.create(result)
                .expectError(BusinessMessageException.class)
                .verify();
    }

    @Test
    void checkEducation_성공() {
        LocalDateTime now = LocalDateTime.now();
        ReserveItem item = ReserveItem.builder()
                .reserveItemId(1L)
                .categoryId("education")
                .reserveMeansId("realtime")
                .requestStartDate(now.minusDays(1))
                .requestEndDate(now.plusDays(5))
                .inventoryQty(5)
                .build();

        ReserveItemResponseDto reserveItemDto = ReserveItemResponseDto.builder()
                .reserveItem(item)
                .build();

        Reserve reserve = Reserve.builder()
                .reserveId("reserve-1")
                .reserveItemId(1L)
                .reserveQty(5)
                .build();

        Mono<Reserve> result = reserveValidator.checkEducation(reserveItemDto, reserve);

        StepVerifier.create(result)
                .expectNext(reserve)
                .verifyComplete();
    }

    @Test
    void checkEducation_실패_기간아님() {
        LocalDateTime now = LocalDateTime.now();
        ReserveItem item = ReserveItem.builder()
                .reserveItemId(1L)
                .categoryId("education")
                .reserveMeansId("realtime")
                .requestStartDate(now.plusDays(1))
                .requestEndDate(now.plusDays(5))
                .inventoryQty(2)
                .build();

        ReserveItemResponseDto reserveItemDto = ReserveItemResponseDto.builder()
                .reserveItem(item)
                .build();

        Reserve reserve = Reserve.builder()
                .reserveId("reserve-1")
                .reserveItemId(1L)
                .reserveQty(1)
                .build();

        Mono<Reserve> result = reserveValidator.checkEducation(reserveItemDto, reserve);

        StepVerifier.create(result)
                .expectError(BusinessMessageException.class)
                .verify();
    }

    @Test
    void checkEducation_실패_마감됨() {
        LocalDateTime now = LocalDateTime.now();
        ReserveItem item = ReserveItem.builder()
                .reserveItemId(1L)
                .categoryId("education")
                .reserveMeansId("realtime")
                .requestStartDate(now.minusDays(1))
                .requestEndDate(now.plusDays(5))
                .inventoryQty(0)
                .build();

        ReserveItemResponseDto reserveItemDto = ReserveItemResponseDto.builder()
                .reserveItem(item)
                .build();

        Reserve reserve = Reserve.builder()
                .reserveId("reserve-1")
                .reserveItemId(1L)
                .reserveQty(1)
                .build();

        Mono<Reserve> result = reserveValidator.checkEducation(reserveItemDto, reserve);

        StepVerifier.create(result)
                .expectError(BusinessMessageException.class)
                .verify();
    }


    @Test
    void checkReserveItems_공간_라우팅_성공() {
        LocalDateTime now = LocalDateTime.now();
        ReserveItem item = ReserveItem.builder()
                .reserveItemId(1L)
                .categoryId("space")
                .reserveMeansId("realtime")
                .requestStartDate(now.minusDays(1))
                .requestEndDate(now.plusDays(5))
                .isPeriod(false)
                .build();

        ReserveItemResponseDto reserveItemDto = ReserveItemResponseDto.builder()
                .reserveItem(item)
                .build();

        Reserve reserve = Reserve.builder()
                .reserveId("reserve-1")
                .reserveItemId(1L)
                .reserveStartDate(now)
                .reserveEndDate(now.plusDays(1))
                .build();

        when(circuitBreakerRegistry.circuitBreaker(anyString())).thenReturn(circuitBreaker);
        when(reserveItemServiceClient.findById(1L)).thenReturn(Mono.just(reserveItemDto));
        when(reserveRepository.findAllByReserveDateWithoutSelfCount(
                "reserve-1", 1L, reserve.getReserveStartDate(), reserve.getReserveEndDate()))
                .thenReturn(Mono.just(0L));

        Mono<Reserve> result = reserveValidator.checkReserveItems(reserve);

        StepVerifier.create(result)
                .expectNext(reserve)
                .verifyComplete();
    }
}
