package org.egovframe.cloud.reserverequestservice.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.IntStream;
import javax.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.egovframe.cloud.common.exception.BusinessMessageException;
import org.egovframe.cloud.common.util.MessageUtil;
import org.egovframe.cloud.reserverequestservice.api.dto.ReserveSaveRequestDto;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class ReserveValidator {

    @Resource(
        name = "messageUtil"
    )
    protected MessageUtil messageUtil;

    private final ReserveRepository reserveRepository;

    public Mono<ReserveSaveRequestDto> checkValidation(ReserveSaveRequestDto saveRequestDto) {
        if (Category.EQUIPMENT.isEquals(saveRequestDto.getCategoryId())) {
            return checkEquipment(saveRequestDto);
        }else if (Category.SPACE.isEquals(saveRequestDto.getCategoryId())) {
            return checkSpace(saveRequestDto);
        }
        //해당 날짜에는 예약할 수 없습니다.
        return Mono.error(new BusinessMessageException(getMessage("valid.reserve_date")));
    }

    /**
     * 예약 날자 validation
     *
     * @param saveRequestDto
     * @return
     */
    public Mono<ReserveSaveRequestDto> checkReserveDate(ReserveSaveRequestDto saveRequestDto) {
        LocalDateTime startDate = saveRequestDto.getReserveMeansId().equals("realtime") ?
            saveRequestDto.getRequestStartDate() : saveRequestDto.getOperationStartDate();
        LocalDateTime endDate = saveRequestDto.getReserveMeansId().equals("realtime") ?
            saveRequestDto.getRequestEndDate() : saveRequestDto.getOperationEndDate();

        if (saveRequestDto.getReserveStartDate().isBefore(startDate)) {
            //{0}이 {1} 보다 빠릅니다. 시작일, 운영/예약 시작일
            return Mono.error(new BusinessMessageException(getMessage("valid.to_be_fast.format", new Object[]{getMessage("common.start_date"),
                getMessage("reserve_item.operation")+getMessage("reserve")+" "+getMessage("common.start_date")})));
        }

        if (saveRequestDto.getReserveEndDate().isAfter(endDate)) {
            //{0}이 {1} 보다 늦습니다. 종료일, 운영/예약 종료일
            return Mono.error(new BusinessMessageException(getMessage("valid.to_be_slow.format", new Object[]{getMessage("common.end_date"),
                getMessage("reserve_item.operation")+getMessage("reserve")+" "+getMessage("common.end_date")})));
        }

        if (saveRequestDto.getIsPeriod()) {
            long between = ChronoUnit.DAYS.between(saveRequestDto.getReserveStartDate(),
                saveRequestDto.getReserveEndDate());
            if (saveRequestDto.getPeriodMaxCount() < between) {
                //최대 예약 가능 일수보다 예약기간이 깁니다. (최대 예약 가능일 수 : {0})
                return Mono.error(new BusinessMessageException(getMessage("valid.reserve_period", new Object[]{saveRequestDto.getPeriodMaxCount()})));
            }
        }
        return Mono.just(saveRequestDto);
    }

    /**
     * 공간 예약 시 예약 날짜에 다른 예약이 있는지 체크
     *
     * @param saveRequestDto
     * @return
     */
    public Mono<ReserveSaveRequestDto> checkSpace(ReserveSaveRequestDto saveRequestDto) {
        return this.checkReserveDate(saveRequestDto)
            .flatMap(result -> reserveRepository.findAllByReserveDateCount(
                result.getReserveItemId(),
                result.getReserveStartDate(),
                result.getReserveEndDate())
                .flatMap(count -> {
                    if (count > 0) {
                        //해당 날짜에는 예약할 수 없습니다.
                        return Mono.error(new BusinessMessageException(getMessage("valid.reserve_date")));
                    }
                    return Mono.just(result);
                })
            );
    }

    /**
     * 장비 예약 시 예약 날짜에 예약 가능한 재고 체크
     *
     * @param saveRequestDto
     * @return
     */
    public Mono<ReserveSaveRequestDto> checkEquipment(ReserveSaveRequestDto saveRequestDto) {
        return this.checkReserveDate(saveRequestDto)
            .flatMap(result -> this.getMaxByReserveDate(
                result.getReserveItemId(),
                result.getReserveStartDate(),
                result.getReserveEndDate())
                .flatMap(max -> {
                    if ((result.getTotalQty() - max) < result.getReserveQty()) {
                        return Mono.just(false);
                    }
                    return Mono.just(true);
                })
                .flatMap(isValid -> {
                    if (!isValid) {
                        //해당 날짜에 예약할 수 있는 재고수량이 없습니다.
                        return Mono.error(new BusinessMessageException(getMessage("valid.reserve_count")));
                    }
                    return Mono.just(saveRequestDto);
                })
            );
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
    private Mono<Integer> getMaxByReserveDate( Long reserveItemId, LocalDateTime startDate, LocalDateTime endDate) {
        Flux<Reserve> reserveFlux = reserveRepository.findAllByReserveDate(reserveItemId, startDate, endDate)
            .switchIfEmpty(Flux.empty());

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
                        || localDateTime.isEqual(findReserve.getReserveStartDate()) || localDateTime.isEqual(findReserve.getReserveEndDate())
                    ) {
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
