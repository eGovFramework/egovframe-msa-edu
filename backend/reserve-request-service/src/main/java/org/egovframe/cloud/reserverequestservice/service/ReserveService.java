package org.egovframe.cloud.reserverequestservice.service;

import java.time.LocalDateTime;
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
import org.egovframe.cloud.reserverequestservice.domain.ReserveValidator;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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
    private final ReserveValidator reserveValidator;
    private final StreamBridge streamBridge;
    private final AmqpAdmin amqpAdmin;


    /**
     * 예약 신청 저장
     *
     * @param saveRequestDto
     * @return
     */
    public Mono<ReserveResponseDto> create(ReserveSaveRequestDto saveRequestDto) {
        return Mono.just(saveRequestDto)
            .flatMap(dto -> Mono.just(dto.createRequestReserve()))
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
            .flatMap(this::validate)
            .onErrorResume(throwable -> Mono.error(throwable))
            .flatMap(dto -> Mono.just(dto.createApproveReserve())).zipWith(getUserId())
            .flatMap(tuple -> Mono.just(tuple.getT1().setCreatedInfo(LocalDateTime.now(), tuple.getT2())))
            .flatMap(reserveRepository::insert)
            .flatMap(this::convertReserveResponseDto);
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
     * 저장 시 정합성 체크
     *
     * @param saveRequestDto
     * @return
     */
    private Mono<ReserveSaveRequestDto> validate(ReserveSaveRequestDto saveRequestDto) {
        if (Category.EQUIPMENT.isEquals(saveRequestDto.getCategoryId())) {
            return reserveValidator.checkEquipment(saveRequestDto);
        }

        if (Category.SPACE.isEquals(saveRequestDto.getCategoryId())) {
            return reserveValidator.checkSpace(saveRequestDto);
        }
        //해당 날짜에는 예약할 수 없습니다.
        return Mono.error(new BusinessMessageException(getMessage("valid.reserve_date")));
    }

}
