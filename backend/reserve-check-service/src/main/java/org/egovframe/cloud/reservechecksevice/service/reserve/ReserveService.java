package org.egovframe.cloud.reservechecksevice.service.reserve;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import org.egovframe.cloud.common.domain.Role;
import org.egovframe.cloud.common.dto.AttachmentEntityMessage;
import org.egovframe.cloud.common.exception.BusinessMessageException;
import org.egovframe.cloud.reactive.service.ReactiveAbstractService;
import org.egovframe.cloud.reservechecksevice.api.reserve.dto.ReserveCancelRequestDto;
import org.egovframe.cloud.reservechecksevice.api.reserve.dto.ReserveListResponseDto;
import org.egovframe.cloud.reservechecksevice.api.reserve.dto.ReserveRequestDto;
import org.egovframe.cloud.reservechecksevice.api.reserve.dto.ReserveResponseDto;
import org.egovframe.cloud.reservechecksevice.api.reserve.dto.ReserveSaveRequestDto;
import org.egovframe.cloud.reservechecksevice.api.reserve.dto.ReserveUpdateRequestDto;
import org.egovframe.cloud.reservechecksevice.client.ReserveItemServiceClient;
import org.egovframe.cloud.reservechecksevice.client.dto.ReserveItemResponseDto;
import org.egovframe.cloud.reservechecksevice.domain.reserve.Category;
import org.egovframe.cloud.reservechecksevice.domain.reserve.Reserve;
import org.egovframe.cloud.reservechecksevice.domain.reserve.ReserveRepository;
import org.egovframe.cloud.reservechecksevice.domain.reserve.ReserveStatus;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * org.egovframe.cloud.reservechecksevice.service.reserve.ReserveService
 *
 * 예약 service 클래스
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/09/15
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/15    shinmj       최초 생성
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ReserveService extends ReactiveAbstractService {
    private static final String RESERVE_ITEM_CIRCUIT_BREAKER_NAME = "reserve-item";
    private static final String CHECK_RESERVE_MEANS = "realtime";

    private final ReserveRepository reserveRepository;
    private final ReserveItemServiceClient reserveItemServiceClient;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final StreamBridge streamBridge;

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
     * entity -> 목록 dto 변환
     *
     * @param reserve
     * @return
     */
    private Mono<ReserveListResponseDto> convertReserveListResponseDto(Reserve reserve) {
        return Mono.just(ReserveListResponseDto.builder()
                .entity(reserve)
                .build());
    }

    /**
     * 현재 로그인 사용자가 관리자인지 체크
     *
     * @return
     */
    private Mono<Boolean> getIsAdmin() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getAuthorities)
                .map(grantedAuthorities -> {
                    List<SimpleGrantedAuthority> authorities =
                            new ArrayList<>((Collection<? extends SimpleGrantedAuthority>) grantedAuthorities);
                    SimpleGrantedAuthority adminRole = new SimpleGrantedAuthority(Role.ADMIN.getKey());
                    return authorities.contains(adminRole);
                });
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
     * 목록 조회
     *
     * @param requestDto
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public Mono<Page<ReserveListResponseDto>> search(ReserveRequestDto requestDto, Pageable pageable) {
        return reserveRepository.search(requestDto, pageable)
                .switchIfEmpty(Flux.empty())
                .flatMap(this::convertReserveListResponseDto)
                .collectList()
                .zipWith(reserveRepository.searchCount(requestDto, pageable))
                .flatMap(tuple -> Mono.just(new PageImpl<>(tuple.getT1(), pageable, tuple.getT2())));
    }

    /**
     * 한건 조회 dto return
     *
     * @param reserveId
     * @return
     */
    @Transactional(readOnly = true)
   public Mono<ReserveResponseDto> findReserveById(String reserveId) {
        return reserveRepository.findReserveById(reserveId)
                .switchIfEmpty(monoResponseStatusEntityNotFoundException(reserveId))
                .flatMap(this::convertReserveResponseDto);
   }

    /**
     * 사용자용 예약 목록 조회 (로그인 사용자의 예약정보만 조회)
     *
     * @param userId
     * @param requestDto
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public Mono<Page<ReserveListResponseDto>> searchForUser(String userId, ReserveRequestDto requestDto, Pageable pageable) {
        return reserveRepository.searchForUser(requestDto, pageable, userId)
                .switchIfEmpty(Flux.empty())
                .flatMap(this::convertReserveListResponseDto)
                .collectList()
                .zipWith(reserveRepository.searchCountForUser(requestDto, pageable, userId))
                .flatMap(tuple -> Mono.just(new PageImpl<>(tuple.getT1(), pageable, tuple.getT2())));
    }

    /**
     * 예약 정보 취소
     *
     * @param reserveId
     * @param cancelRequestDto
     * @return
     */
    public Mono<Void> cancel(String reserveId, ReserveCancelRequestDto cancelRequestDto) {
        return getIsAdmin().flatMap(isAdmin -> {
            if (isAdmin) {
                return reserveCancel(reserveId, cancelRequestDto);
            }
            return findById(reserveId)
                .zipWith(getUserId())
                .flatMap(tuple -> {
                    if (tuple.getT1().getUserId().equals(tuple.getT2())) {
                        return Mono.just(tuple.getT1());
                    }
                    //해당 예약은 취소할 수 없습니다.
                    return Mono.error(new BusinessMessageException(getMessage("valid.cant_cancel")));
                })
                .onErrorResume(throwable -> Mono.error(throwable))
                .flatMap(reserve -> reserveCancel(reserveId, cancelRequestDto));
        });

    }

    /**
     * 예약 상태 취소로 변경
     *
     * @param reserveId
     * @param cancelRequestDto
     * @return
     */
    private Mono<Void> reserveCancel(String reserveId, ReserveCancelRequestDto cancelRequestDto) {
        return findById(reserveId)
                .map(reserve -> {
                    if (ReserveStatus.DONE.isEquals(reserve.getReserveStatusId())) {
                        //해당 예약은 이미 실행되어 취소할 수 없습니다.
                        throw new BusinessMessageException(getMessage("valid.cant_cancel_because_done"));
                    }else {
                        return reserve.updateStatus(ReserveStatus.CANCEL.getKey())
                            .updateReasonCancel(cancelRequestDto.getReasonCancelContent());
                    }
                })
                .flatMap(reserve -> Mono.just(reserve.conversionReserveQty()))
                .flatMap(this::updateInventory)
                .onErrorResume(throwable -> Mono.error(throwable))
                .flatMap(reserve -> Mono.just(reserve.conversionReserveQty()))
                .flatMap(reserveRepository::save)
                .then();
    }

    /**
     * 예약 정보 승인
     *
     * @param reserveId
     * @return
     */
    public Mono<Void> approve(String reserveId) {
        return getIsAdmin()
            .flatMap(isAdmin -> {
                if (isAdmin) {
                    return Mono.just(reserveId);
                }
                //관리자만 승인할 수 있습니다.
                return Mono.error(new BusinessMessageException(getMessage("valid.manager_approve")));
            })
            .onErrorResume(throwable -> Mono.error(throwable))
            .flatMap(this::checkApprove)
            .onErrorResume(throwable -> Mono.error(throwable))
            .flatMap(reserveRepository::save).then();
    }

    /**
     * 승인 전 validate check 및 교육인 경우 재고 업데이트
     *
     * @param reserveId
     * @return
     */
    private Mono<Reserve> checkApprove(String reserveId) {
        return findById(reserveId)
            .flatMap(this::checkReserveItems)
            .onErrorResume(throwable -> Mono.error(throwable))
            .map(reserve -> reserve.updateStatus(ReserveStatus.APPROVE.getKey()))
            .flatMap(this::updateInventory);
    }

    /**
     * 예약 물품 재고 및 예약 일자 체크
     *
     * @param reserve
     * @return
     */
    private Mono<Reserve> checkReserveItems(Reserve reserve) {
        return reserveItemServiceClient.findById(reserve.getReserveItemId())
            .transform(CircuitBreakerOperator.of(circuitBreakerRegistry.circuitBreaker(RESERVE_ITEM_CIRCUIT_BREAKER_NAME)))
            .onErrorResume(throwable -> Mono.empty())
            .flatMap(reserveItemResponseDto -> {
                // validation check
                if (Category.SPACE.isEquals(reserveItemResponseDto.getCategoryId())) {
                    return this.checkSpace(reserveItemResponseDto, reserve);
                }else if (Category.EQUIPMENT.isEquals(reserveItemResponseDto.getCategoryId())) {
                    return this.checkEquipment(reserveItemResponseDto, reserve);
                }else if (Category.EDUCATION.isEquals(reserveItemResponseDto.getCategoryId())) {
                    return this.checkEducation(reserveItemResponseDto, reserve);
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
     * 공간 예약 시 예약 날짜에 다른 예약이 있는지 체크
     *
     * @param reserveItem
     * @param reserve
     * @return
     */
    private Mono<Reserve> checkSpace(ReserveItemResponseDto reserveItem, Reserve reserve) {
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
    private Mono<Reserve> checkEquipment(ReserveItemResponseDto reserveItem, Reserve reserve) {
        return this.checkReserveDate(reserveItem, reserve)
            .flatMap(entity -> this.getMaxByReserveDateWithoutSelf(
                entity.getReserveId(),
                reserveItem.getReserveItemId(),
                entity.getReserveStartDate(),
                entity.getReserveEndDate())
                .flatMap(max -> {
                    if ((reserveItem.getTotalQty() - max) < reserve.getReserveQty()) {
                        return Mono.just(false);
                    }
                    return Mono.just(true);
                })
                .flatMap(isValid -> {
                    if (!isValid) {
                        //해당 날짜에 예약할 수 있는 재고수량이 없습니다.
                        return Mono.error(new BusinessMessageException(getMessage("valid.reserve_count")));
                    }
                    return Mono.just(reserve);
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
    private Mono<Reserve> checkEducation(ReserveItemResponseDto reserveItem, Reserve reserve) {
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

                if (reserveItemResponseDto.getInventoryQty() <= 0) {
                    //"예약이 마감되었습니다."
                    return Mono.error(new BusinessMessageException(getMessage("valid.reserve_close")));
                }

                if (reserveItemResponseDto.getInventoryQty() < reserve.getReserveQty()) {
                    //예약가능한 인원이 부족합니다. (남은 인원 : {0})
                    return Mono.error(new BusinessMessageException(getMessage("valid.reserve_number_of_people", new Object[]{reserveItemResponseDto.getInventoryQty()})));
                }
                return Mono.just(reserve);
            });
    }

    /**
     * 예약 정보 수정
     *
     * @param reserveId
     * @return
     */
    public Mono<Reserve> update(String reserveId, ReserveUpdateRequestDto updateRequestDto) {
        return getIsAdmin().flatMap(isAdmin -> {
            if (isAdmin) {
                return updateReserve(reserveId, updateRequestDto);
            }
            return updateReserveForUser(reserveId, updateRequestDto);
        });
    }

    /**
     * 사용자 예약 수정
     *
     * @param reserveId
     * @param updateRequestDto
     * @return
     */
    private Mono<Reserve> updateReserveForUser(String reserveId, ReserveUpdateRequestDto updateRequestDto) {
        return findById(reserveId)
                .zipWith(getUserId())
                .map(tuple -> {
                    if (!tuple.getT1().getUserId().equals(tuple.getT2())) {
                        //"해당 예약은 수정할 수 없습니다."
                        throw new BusinessMessageException(getMessage("valid.reserve_not_update"));
                    }

                    if (!ReserveStatus.REQUEST.getKey().equals(tuple.getT1().getReserveStatusId())) {
                        //예약 신청 상태인 경우에만 수정 가능합니다.
                        throw new BusinessMessageException(getMessage("valid.reserve_not_update_status"));
                    }

                    return tuple.getT1().update(updateRequestDto);
                })
                .flatMap(this::checkReserveItems)
                .onErrorResume(throwable -> Mono.error(throwable))
                .flatMap(this::updateInventory)
                .onErrorResume(throwable -> Mono.error(throwable))
                .flatMap(reserveRepository::save);
    }

    /**
     * 관리자 예약 수정
     *
     * @param reserveId
     * @param updateRequestDto
     * @return
     */
    private Mono<Reserve> updateReserve(String reserveId, ReserveUpdateRequestDto updateRequestDto) {
        return findById(reserveId)
                .map(reserve -> {
                    if (!ReserveStatus.REQUEST.getKey().equals(reserve.getReserveStatusId())) {
                        //예약 신청 상태인 경우에만 수정 가능합니다.
                        throw new BusinessMessageException(getMessage("valid.reserve_not_update_status"));
                    }
                    return reserve.update(updateRequestDto);
                })
                .flatMap(this::checkReserveItems)
                .onErrorResume(throwable -> Mono.error(throwable))
                .flatMap(this::updateInventory)
                .onErrorResume(throwable -> Mono.error(throwable))
                .flatMap(reserveRepository::save);
    }

    /**
     * 한건 정보 조회 entity return
     *
     * @param reserveId
     * @return
     */
    private Mono<Reserve> findById(String reserveId) {
        return reserveRepository.findById(reserveId)
                .switchIfEmpty(monoResponseStatusEntityNotFoundException(reserveId));
    }

    /**
     * 관리자 예약 신청
     * 관리자의 경우 실시간이어도 이벤트 스트림 거치지 않고 바로 예약 처리
     *
     * @param saveRequestDto
     * @return
     */
    public Mono<ReserveResponseDto> create(ReserveSaveRequestDto saveRequestDto) {
        return Mono.just(saveRequestDto)
            .map(dto -> {
                String uuid = UUID.randomUUID().toString();
                dto.setReserveId(uuid);
                return dto.toEntity();
            })
            .zipWith(getUserId())
            .flatMap(tuple -> Mono.just(tuple.getT1().setCreatedInfo(LocalDateTime.now(), tuple.getT2())))
            .flatMap(this::checkReserveItems)
            .onErrorResume(throwable -> Mono.error(throwable))
            .flatMap(this::updateInventory)
            .onErrorResume(throwable -> Mono.error(throwable))
            .flatMap(reserveRepository::insert)
            .flatMap(reserveRepository::loadRelations)
            .doOnNext(reserve -> sendAttachmentEntityInfo(streamBridge,
                AttachmentEntityMessage.builder()
                    .attachmentCode(reserve.getAttachmentCode())
                    .entityName(reserve.getClass().getName())
                    .entityId(reserve.getReserveId())
                    .build()))
            .flatMap(this::convertReserveResponseDto);


    }

    /**
     * 예약 정보 저장 시 재고 변경
     *
     * @param reserve
     * @return
     */
    private Mono<Reserve> updateInventory(Reserve reserve) {
        return Mono.just(reserve)
            .flatMap(reserve1 -> {
                if (!Category.EDUCATION.isEquals(reserve1.getCategoryId())) {
                    return Mono.just(reserve1);
                }
//                return reserveItemServiceClient.updateInventory(reserve.getReserveItemId(), reserve.getReserveQty())
//                    .transform(CircuitBreakerOperator.of(circuitBreakerRegistry.circuitBreaker(RESERVE_ITEM_CIRCUIT_BREAKER_NAME)))
//                    .onErrorResume(throwable -> Mono.just(false))
//                    .flatMap(isSuccess -> {
//                        if (isSuccess) {
//                            return Mono.just(reserve);
//                        }
//                        //재고 업데이트에 실패했습니다.
//                        return Mono.error(new BusinessMessageException(getMessage("msg.inventory_failed")));
//                    });
                return null;
            });
    }

    /**
     * 예약 물품별 기간안에 있는 예약된 수량 max 조회
     *
     * @param reserveItemId
     * @param startDate
     * @param endDate
     * @return
     */
    public Mono<Integer> countInventory(Long reserveItemId, LocalDateTime startDate, LocalDateTime endDate) {
        return reserveItemServiceClient.findById(reserveItemId)
            .transform(CircuitBreakerOperator.of(circuitBreakerRegistry.circuitBreaker(RESERVE_ITEM_CIRCUIT_BREAKER_NAME)))
            .onErrorResume(throwable -> Mono.empty())
            .zipWith(getMaxByReserveDate(reserveItemId, startDate, endDate))
            .flatMap(tuple -> Mono.just(tuple.getT1().getTotalQty() - tuple.getT2()));
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
     * 예약물품에 대해 날짜별 예약된 수량 max 조회
     *
     * @param reserveItemId
     * @param startDate
     * @param endDate
     * @return
     */
    private Mono<Integer> getMaxByReserveDate(Long reserveItemId, LocalDateTime startDate, LocalDateTime endDate) {
        Flux<Reserve> reserveFlux = reserveRepository.findAllByReserveDate(reserveItemId, startDate, endDate)
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
        return Flux.fromStream(IntStream.iterate(0, i -> i + 1)
            .limit(between)
            .mapToObj(i -> startDate.plusDays(i)))
            .flatMap(localDateTime ->
                reserveFlux.map(findReserve -> {
                    if (localDateTime.isAfter(findReserve.getReserveStartDate())
                        || localDateTime.isBefore(findReserve.getReserveEndDate())) {
                        return findReserve.getReserveQty();
                    }
                    return 0;
                }).reduce(0, (x1, x2) -> x1 + x2))
            .groupBy(integer -> integer)
            .flatMap(group -> group.reduce((x1,x2) -> x1 > x2?x1:x2))
            .last();
    }

}
