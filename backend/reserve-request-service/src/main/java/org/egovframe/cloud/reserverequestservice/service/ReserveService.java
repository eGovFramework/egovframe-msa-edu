package org.egovframe.cloud.reserverequestservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.cloud.common.config.GlobalConstant;
import org.egovframe.cloud.common.dto.AttachmentEntityMessage;
import org.egovframe.cloud.common.exception.BusinessMessageException;
import org.egovframe.cloud.reactive.service.ReactiveAbstractService;
import org.egovframe.cloud.reserverequestservice.api.dto.ReserveResponseDto;
import org.egovframe.cloud.reserverequestservice.api.dto.ReserveSaveRequestDto;
import org.egovframe.cloud.reserverequestservice.domain.Category;
import org.egovframe.cloud.reserverequestservice.domain.Reserve;
import org.egovframe.cloud.reserverequestservice.domain.ReserveRepository;
import org.egovframe.cloud.reserverequestservice.domain.ReserveStatus;
import org.springframework.amqp.core.*;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.stream.IntStream;

/**
 * org.egovframe.cloud.reserverequestservice.service.ReserveService
 * <p>
 * 예약 신청 service class
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/09/17
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/17    shinmj      최초 생성
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ReserveService extends ReactiveAbstractService {
    private final ReserveRepository reserveRepository;
    private final StreamBridge streamBridge;
    private final AmqpAdmin amqpAdmin;

    /**
     * entity -> dto 변환
     *
     * @param reserve
     * @return
     */
    private Mono<ReserveResponseDto> convertReserveResponseDto(Reserve reserve) {
        return Mono.just(ReserveResponseDto.builder()
                .entity(reserve)
                .build());
    }

    /**
     * 현재 로그인 사용자 id
     *
     * @return
     */
    private Mono<String> getUserId() {
        return ReactiveSecurityContextHolder.getContext()
            .map(SecurityContext::getAuthentication)
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getPrincipal)
            .map(String.class::cast);
    }

    /**
     * 예약 신청 저장
     *
     * @param saveRequestDto
     * @return
     */
    public Mono<ReserveResponseDto> create(ReserveSaveRequestDto saveRequestDto) {
        return Mono.just(saveRequestDto)
            .flatMap(dto -> {
                String uuid = UUID.randomUUID().toString();
                dto.setReserveId(uuid);
                dto.setReserveStatusId(ReserveStatus.REQUEST.getKey());
                return Mono.just(dto.toEntity());
            })
            .zipWith(getUserId())
            .flatMap(tuple -> {
                tuple.getT1().setCreatedInfo(LocalDateTime.now(), tuple.getT2());
                return Mono.just(tuple.getT1());
            })
            .flatMap(reserveRepository::insert)
            .doOnNext(reserve -> sendAttachmentEntityInfo(streamBridge,
                AttachmentEntityMessage.builder()
                    .attachmentCode(reserve.getAttachmentCode())
                    .entityName(reserve.getClass().getName())
                    .entityId(reserve.getReserveId())
                    .build()))
            .flatMap(this::convertReserveResponseDto);
    }

    /**
     * 예약 신청 - 실시간
     * 예약 정보 저장 후 재고 변경을 위해 이벤트 publish
     *
     * @param saveRequestDto
     * @return
     */
    public Mono<ReserveResponseDto> saveForEvent(ReserveSaveRequestDto saveRequestDto) {
        return create(saveRequestDto)
                .flatMap(reserveResponseDto ->
                                Mono.fromCallable(() -> {
                                    //예약 저장 후 해당 id로 queue 생성
                                    Exchange ex = ExchangeBuilder.directExchange(GlobalConstant.SUCCESS_OR_NOT_EX_NAME)
                                            .durable(true).build();
                                    amqpAdmin.declareExchange(ex);

                                    Queue queue = QueueBuilder.durable(reserveResponseDto.getReserveId()).build();
                                    amqpAdmin.declareQueue(queue);

                                    Binding binding = BindingBuilder.bind(queue)
                                            .to(ex)
                                            .with(reserveResponseDto.getReserveId())
                                            .noargs();
                                    amqpAdmin.declareBinding(binding);

                                    log.info("Biding successfully created");

                                    streamBridge.send("reserveRequest-out-0", reserveResponseDto);

                                    return reserveResponseDto;
                                }).subscribeOn(Schedulers.boundedElastic())
                        );
    }

    /**
     * 예약 신청 - 실시간
     * 이벤트 스트림을 타지 않는 경우 (재고 변경 이벤트가 없는 경우: 공간, 장비)
     *
     * @param saveRequestDto
     * @return
     */
    public Mono<ReserveResponseDto> save(ReserveSaveRequestDto saveRequestDto) {
        return Mono.just(saveRequestDto)
            .flatMap(this::checkValidation)
            .onErrorResume(throwable -> Mono.error(throwable))
            .flatMap(dto -> {
                String uuid = UUID.randomUUID().toString();
                dto.setReserveId(uuid);
                dto.setReserveStatusId(ReserveStatus.APPROVE.getKey());
                return Mono.just(dto.toEntity());
            }).zipWith(getUserId())
            .flatMap(tuple -> Mono.just(tuple.getT1().setCreatedInfo(LocalDateTime.now(), tuple.getT2())))
            .flatMap(reserveRepository::insert)
            .flatMap(this::convertReserveResponseDto);
    }

    private Mono<ReserveSaveRequestDto> checkValidation(ReserveSaveRequestDto saveRequestDto) {
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
    private Mono<ReserveSaveRequestDto> checkReserveDate(ReserveSaveRequestDto saveRequestDto) {
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
    private Mono<ReserveSaveRequestDto> checkSpace(ReserveSaveRequestDto saveRequestDto) {
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
    private Mono<ReserveSaveRequestDto> checkEquipment(ReserveSaveRequestDto saveRequestDto) {
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

    /**
     * 예약 신청 후 예약 물품 재고 변경 성공 시 예약승인으로 상태 변경
     *
     * @param reserveId
     * @param reserveStatus
     * @return
     */
    public Mono<Void> updateStatus(String reserveId, ReserveStatus reserveStatus) {
        log.info("update : {} , {}", reserveId, reserveStatus);
        return reserveRepository.findById(reserveId)
                .map(reserve -> reserve.updateStatus(reserveStatus.getKey()))
                .flatMap(reserveRepository::save)
                .then();
    }

    /**
     * 예약 신청 후 예약 물품 재고 변경 실패 시 해당 예약 건 삭제
     *
     * @param reserveId
     * @return
     */
    public Mono<Void> delete(String reserveId) {
        log.info("delete {}", reserveId);
        return reserveRepository.findById(reserveId)
                .flatMap(reserveRepository::delete)
                .then();
    }

}
