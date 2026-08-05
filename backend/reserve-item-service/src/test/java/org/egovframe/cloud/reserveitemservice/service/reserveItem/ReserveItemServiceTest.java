package org.egovframe.cloud.reserveitemservice.service.reserveItem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.egovframe.cloud.reserveitemservice.domain.reserveItem.ReserveItem;
import org.egovframe.cloud.reserveitemservice.domain.reserveItem.ReserveItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * org.egovframe.cloud.reserveitemservice.service.reserveItem.ReserveItemServiceTest
 *
 * 예약 물품 service 재고 변경 회귀 테스트
 *
 * <pre>
 * updateInventory 가 재고를 이중으로 차감하던 결함(같은 reserveQty 만큼만 차감되어야 함)에 대한 회귀 검증.
 * </pre>
 */
@ExtendWith(MockitoExtension.class)
class ReserveItemServiceTest {

    @Mock
    private ReserveItemRepository reserveItemRepository;

    @Mock
    private StreamBridge streamBridge;

    @InjectMocks
    private ReserveItemService reserveItemService;

    private ReserveItem reserveItemWithInventory(int inventoryQty) {
        return ReserveItem.builder()
            .reserveItemId(1L)
            .reserveItemName("test")
            .categoryId("education")
            .totalQty(100)
            .inventoryQty(inventoryQty)
            .build();
    }

    @Test
    void updateInventory_재고는_reserveQty_만큼만_차감된다() {
        ReserveItem reserveItem = reserveItemWithInventory(100);
        when(reserveItemRepository.findById(anyLong())).thenReturn(Mono.just(reserveItem));
        ArgumentCaptor<ReserveItem> captor = ArgumentCaptor.forClass(ReserveItem.class);
        when(reserveItemRepository.save(captor.capture()))
            .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(reserveItemService.updateInventory(1L, 10))
            .expectNext(true)
            .verifyComplete();

        // 100 - 10 = 90 이어야 한다. 결함 시에는 100 - (100-10) = 10 으로 덮어써진다.
        assertThat(captor.getValue().getInventoryQty()).isEqualTo(90);
    }

    @Test
    void updateInventory_재고가_부족하면_차감하지_않고_false() {
        ReserveItem reserveItem = reserveItemWithInventory(5);
        when(reserveItemRepository.findById(anyLong())).thenReturn(Mono.just(reserveItem));

        StepVerifier.create(reserveItemService.updateInventory(1L, 10))
            .expectNext(false)
            .verifyComplete();

        // oversell 방지: 재고 부족 시 저장이 일어나지 않아야 한다.
        verify(reserveItemRepository, never()).save(any(ReserveItem.class));
        assertThat(reserveItem.getInventoryQty()).isEqualTo(5);
    }

    @Test
    void updateInventory_재고와_요청수량이_같으면_0으로_차감된다() {
        ReserveItem reserveItem = reserveItemWithInventory(10);
        when(reserveItemRepository.findById(anyLong())).thenReturn(Mono.just(reserveItem));
        ArgumentCaptor<ReserveItem> captor = ArgumentCaptor.forClass(ReserveItem.class);
        when(reserveItemRepository.save(captor.capture()))
            .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(reserveItemService.updateInventory(1L, 10))
            .expectNext(true)
            .verifyComplete();

        assertThat(captor.getValue().getInventoryQty()).isEqualTo(0);
    }
}
