package org.egovframe.cloud.reservechecksevice.domain;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.IntStream;
import javax.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.egovframe.cloud.common.exception.BusinessMessageException;
import org.egovframe.cloud.common.util.MessageUtil;
import org.egovframe.cloud.reservechecksevice.client.ReserveItemServiceClient;
import org.egovframe.cloud.reservechecksevice.client.dto.ReserveItemResponseDto;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ReserveValidator {
    private static final String CHECK_RESERVE_MEANS = "realtime";
    private static final String RESERVE_ITEM_CIRCUIT_BREAKER_NAME = "reserve-item";

    @Resource(
        name = "messageUtil"
    )
    protected MessageUtil messageUtil;

    private final ReserveRepository reserveRepository;
    private final ReserveItemServiceClient reserveItemServiceClient;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    /**
     * 공간 예약 시 예약 날짜에 다른 예약이 있는지 체크
     *
     * @param reserveItem
     * @param reserve
     * @return
     */
    public Mono<Reserve> checkSpace(ReserveItemResponseDto reserveItem, Reserve reserve) {
        return this.checkReserveDate(reserveItem, reserve)
            .flatMap(isValid -> reserveRepository.findAllByReserveDateWithoutSelfCount(
                reserve.getReserveId(),
                reserveItem.getReserveItemId(),
                reserve.getReserveStartDate(),
                reserve.getReserveEndDate())
                .flatMap(count -> {
                    if (count > 0) {
                        //"해당 날짜에는 예약할 수 없습니다."
                        return Mono.error(new BusinessMessageException(getMessage("valid.reserve_date")));
                    }
                    return Mono.just(reserve);
                })
            );
    }

    /**
     * 장비 예약 시 예약 날짜에 예약 가능한 재고 체크
     *
     * @param reserveItem
     * @param reserve
     * @return
     */
    public Mono<Reserve> checkEquipment(ReserveItemResponseDto reserveItem, Reserve reserve) {
        return this.checkReserveDate(reserveItem, reserve)
            .flatMap(entity -> this.getMaxByReserveDateWithoutSelf(
                entity.getReserveId(),
                reserveItem.getReserveItemId(),
                entity.getReserveStartDate(),
                entity.getReserveEndDate())
                .flatMap(max -> Mono.just((reserveItem.isPossibleQty(max, reserve.getReserveQty()))))
                .flatMap(isValid -> {
                    if (isValid) {
                        return Mono.just(reserve);
                    }
                    //해당 날짜에 예약할 수 있는 재고수량이 없습니다.
                    return Mono.error(new BusinessMessageException(getMessage("valid.reserve_count")));
                })
            );
    }

    /**
     * 교육 예약 시 재고 체크
     *
     * @param reserveItem
     * @param reserve
     * @return
     */
    public Mono<Reserve> checkEducation(ReserveItemResponseDto reserveItem, Reserve reserve) {
        return Mono.just(reserveItem)
            .flatMap(reserveItemResponseDto -> {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime startDate = reserveItemResponseDto.getReserveMeansId().equals(CHECK_RESERVE_MEANS) ?
                    reserveItemResponseDto.getRequestStartDate() : reserveItemResponseDto.getOperationStartDate();
                LocalDateTime endDate = reserveItemResponseDto.getReserveMeansId().equals(CHECK_RESERVE_MEANS) ?
                    reserveItemResponseDto.getRequestEndDate() : reserveItemResponseDto.getOperationEndDate();

                if (!(now.isAfter(startDate) && now.isBefore(endDate))) {
                    //해당 날짜에는 예약할 수 없습니다.
                    return Mono.error(new BusinessMessageException(getMessage("valid.reserve_date")));
                }

                if (!reserveItemResponseDto.isPositiveInventory()) {
                    //"예약이 마감되었습니다."
                    return Mono.error(new BusinessMessageException(getMessage("valid.reserve_close")));
                }

                if (reserveItemResponseDto.isPossibleInventoryQty(reserve.getReserveQty())) {
                    //예약가능한 인원이 부족합니다. (남은 인원 : {0})
                    return Mono.error(new BusinessMessageException(getMessage("valid.reserve_number_of_people", new Object[]{reserveItemResponseDto.getInventoryQty()})));
                }
                return Mono.just(reserve);
            });
    }

    /**
     * 예약물품에 대해 날짜별 예약된 수량 max 조회
     *
     * @param reserveItemId
     * @param startDate
     * @param endDate
     * @return
     */
    public Mono<Integer> getMaxByReserveDate(Long reserveItemId, LocalDateTime startDate, LocalDateTime endDate) {
        Flux<Reserve> reserveFlux = reserveRepository.findAllByReserveDate(reserveItemId, startDate, endDate)
            .switchIfEmpty(Flux.empty());
        return countMax(reserveFlux, startDate, endDate);
    }

    /**
     * 예약 물품 재고 및 예약 일자 체크
     *
     * @param reserve
     * @return
     */
    public Mono<Reserve> checkReserveItems(Reserve reserve) {
        return reserveItemServiceClient.findById(reserve.getReserveItemId())
            .transform(CircuitBreakerOperator
                .of(circuitBreakerRegistry.circuitBreaker(RESERVE_ITEM_CIRCUIT_BREAKER_NAME)))
            .onErrorResume(throwable -> Mono.empty())
            .flatMap(reserveItemResponseDto -> {
                // validation check
                if (Category.SPACE.isEquals(reserveItemResponseDto.getCategoryId())) {
                    return checkSpace(reserveItemResponseDto, reserve);
                }
                if (Category.EQUIPMENT.isEquals(reserveItemResponseDto.getCategoryId())) {
                    return checkEquipment(reserveItemResponseDto, reserve);
                }
                if (Category.EDUCATION.isEquals(reserveItemResponseDto.getCategoryId())) {
                    return checkEducation(reserveItemResponseDto, reserve);
                }
                return Mono.just(reserve);
            });
    }


    /**
     * 예약 날짜 validation
     *
     * @param reserveItem
     * @param reserve
     * @return
     */
    private Mono<Reserve> checkReserveDate(ReserveItemResponseDto reserveItem, Reserve reserve) {
        LocalDateTime startDate = reserveItem.getReserveMeansId().equals(CHECK_RESERVE_MEANS) ?
            reserveItem.getRequestStartDate() : reserveItem.getOperationStartDate();
        LocalDateTime endDate = reserveItem.getReserveMeansId().equals(CHECK_RESERVE_MEANS) ?
            reserveItem.getRequestEndDate() : reserveItem.getOperationEndDate();

        if (reserve.getReserveStartDate().isBefore(startDate)) {
            //{0}이 {1} 보다 빠릅니다. 시작일, 운영/예약 시작일
            return Mono.error(new BusinessMessageException(getMessage("valid.to_be_fast.format", new Object[]{getMessage("common.start_date"),
                getMessage("reserve_item.operation")+getMessage("reserve")+" "+getMessage("common.start_date")})));
        }

        if (reserve.getReserveEndDate().isAfter(endDate)) {
            //{0}이 {1} 보다 늦습니다. 종료일, 운영/예약 종료일
            return Mono.error(new BusinessMessageException(getMessage("valid.to_be_slow.format", new Object[]{getMessage("common.end_date"),
                getMessage("reserve_item.operation")+getMessage("reserve")+" "+getMessage("common.end_date")})));
        }

        if (reserveItem.getIsPeriod()) {
            long between = ChronoUnit.DAYS.between(reserve.getReserveStartDate(), reserve.getReserveEndDate());
            if (reserveItem.getPeriodMaxCount() < between) {
                //최대 예약 가능 일수보다 예약기간이 깁니다. (최대 예약 가능일 수 : {0})
                return Mono.error(new BusinessMessageException(getMessage("valid.reserve_period", new Object[]{reserveItem.getPeriodMaxCount()})));
            }
        }

        return Mono.just(reserve);
    }

    /**
     * 예약물품에 대해 날짜별 예약된 수량 max 조회
     * 현 예약 건 제외
     *
     * @param reserveItemId
     * @param startDate
     * @param endDate
     * @return
     */
    private Mono<Integer> getMaxByReserveDateWithoutSelf(String reserveId, Long reserveItemId, LocalDateTime startDate, LocalDateTime endDate) {
        Flux<Reserve> reserveFlux = reserveRepository.findAllByReserveDateWithoutSelf(reserveId, reserveItemId, startDate, endDate)
            .switchIfEmpty(Flux.empty());
        return countMax(reserveFlux, startDate, endDate);
    }

    /**
     * get max
     *
     * @param reserveFlux
     * @param startDate
     * @param endDate
     * @return
     */
    private Mono<Integer> countMax(Flux<Reserve> reserveFlux, LocalDateTime startDate, LocalDateTime endDate) {
        if (reserveFlux.equals(Flux.empty())) {
            return Mono.just(0);
        }

        long between = ChronoUnit.DAYS.between(startDate, endDate);

        if (between == 0) {
            return reserveFlux.map(reserve -> {
                if (startDate.isAfter(reserve.getReserveStartDate())
                    || startDate.isBefore(reserve.getReserveEndDate())
                    || startDate.isEqual(reserve.getReserveStartDate()) || startDate.isEqual(reserve.getReserveEndDate())) {
                    return reserve.getReserveQty();
                }
                return 0;
            }).reduce(0, (x1, x2) -> x1 + x2);
        }

        return Flux.fromStream(IntStream.iterate(0, i -> i + 1)
            .limit(between)
            .mapToObj(i -> startDate.plusDays(i)))
            .flatMap(localDateTime ->
                reserveFlux.map(findReserve -> {
                    if (localDateTime.isAfter(findReserve.getReserveStartDate())
                        || localDateTime.isBefore(findReserve.getReserveEndDate())
                        || localDateTime.isEqual(findReserve.getReserveStartDate()) || localDateTime.isEqual(findReserve.getReserveEndDate())) {
                        return findReserve.getReserveQty();
                    }
                    return 0;
                }).reduce(0, (x1, x2) -> x1 + x2))
            .groupBy(integer -> integer)
            .flatMap(group -> group.reduce((x1,x2) -> x1 > x2?x1:x2))
            .last(0);
    }

    private String getMessage(String code) {
        return this.messageUtil.getMessage(code);
    }

    private String getMessage(String code, Object[] args) {
        return this.messageUtil.getMessage(code, args);
    }

}
