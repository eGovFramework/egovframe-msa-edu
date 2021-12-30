package org.egovframe.cloud.reservechecksevice.service;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.cloud.common.domain.Role;
import org.egovframe.cloud.common.dto.AttachmentEntityMessage;
import org.egovframe.cloud.common.exception.BusinessMessageException;
import org.egovframe.cloud.reactive.service.ReactiveAbstractService;
import org.egovframe.cloud.reservechecksevice.api.dto.ReserveCancelRequestDto;
import org.egovframe.cloud.reservechecksevice.api.dto.ReserveListResponseDto;
import org.egovframe.cloud.reservechecksevice.api.dto.ReserveRequestDto;
import org.egovframe.cloud.reservechecksevice.api.dto.ReserveResponseDto;
import org.egovframe.cloud.reservechecksevice.api.dto.ReserveSaveRequestDto;
import org.egovframe.cloud.reservechecksevice.api.dto.ReserveUpdateRequestDto;
import org.egovframe.cloud.reservechecksevice.client.ReserveItemServiceClient;
import org.egovframe.cloud.reservechecksevice.domain.Reserve;
import org.egovframe.cloud.reservechecksevice.domain.ReserveRepository;
import org.egovframe.cloud.reservechecksevice.domain.ReserveStatus;
import org.egovframe.cloud.reservechecksevice.domain.ReserveValidator;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * org.egovframe.cloud.reservechecksevice.service.ReserveService
 * <p>
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

    private final ReserveRepository reserveRepository;
    private final ReserveItemServiceClient reserveItemServiceClient;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final StreamBridge streamBridge;
    private final ReserveValidator validator;

    /**
     * 목록 조회
     *
     * @param requestDto
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public Mono<Page<ReserveListResponseDto>> search(ReserveRequestDto requestDto,
        Pageable pageable) {
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
    public Mono<Page<ReserveListResponseDto>> searchForUser(String userId,
        ReserveRequestDto requestDto, Pageable pageable) {
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
                    if (tuple.getT1().isReserveUser(tuple.getT2())) {
                        return Mono.just(tuple.getT1());
                    }
                    //해당 예약은 취소할 수 없습니다.
                    return Mono
                        .error(new BusinessMessageException(getMessage("valid.cant_cancel")));
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
            .map(reserve ->
                reserve.updateStatusCancel(cancelRequestDto.getReasonCancelContent(), getMessage("valid.cant_cancel_because_done")))
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
            .onErrorResume(Mono::error)
            .flatMap(this::checkApprove)
            .onErrorResume(Mono::error)
            .flatMap(reserveRepository::save).then();
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
     * 관리자 예약 신청 관리자의 경우 실시간이어도 이벤트 스트림 거치지 않고 바로 예약 처리
     *
     * @param saveRequestDto
     * @return
     */
    public Mono<ReserveResponseDto> create(ReserveSaveRequestDto saveRequestDto) {
        return Mono.just(saveRequestDto)
            .map(ReserveSaveRequestDto::createNewReserve)
            .zipWith(getUserId())
            .flatMap(tuple -> Mono.just(tuple.getT1().setCreatedInfo(LocalDateTime.now(), tuple.getT2())))
            .flatMap(validator::checkReserveItems)
            .onErrorResume(Mono::error)
            .flatMap(this::updateInventory)
            .onErrorResume(Mono::error)
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
     * 예약 물품별 기간안에 있는 예약된 수량 max 조회
     *
     * @param reserveItemId
     * @param startDate
     * @param endDate
     * @return
     */
    public Mono<Integer> countInventory(Long reserveItemId, LocalDateTime startDate,
        LocalDateTime endDate) {
        return reserveItemServiceClient.findById(reserveItemId)
            .transform(CircuitBreakerOperator.of(circuitBreakerRegistry.circuitBreaker(RESERVE_ITEM_CIRCUIT_BREAKER_NAME)))
            .onErrorResume(throwable -> Mono.empty())
            .zipWith(validator.getMaxByReserveDate(reserveItemId, startDate, endDate))
            .flatMap(tuple -> Mono.just(tuple.getT1().getTotalQty() - tuple.getT2()));
    }

    /**
     * 승인 전 validate check 및 교육인 경우 재고 업데이트
     *
     * @param reserveId
     * @return
     */
    private Mono<Reserve> checkApprove(String reserveId) {
        return findById(reserveId)
            .flatMap(validator::checkReserveItems)
            .onErrorResume(Mono::error)
            .map(reserve -> reserve.updateStatus(ReserveStatus.APPROVE.getKey()))
            .flatMap(this::updateInventory);
    }

    /**
     * 사용자 예약 수정
     *
     * @param reserveId
     * @param updateRequestDto
     * @return
     */
    private Mono<Reserve> updateReserveForUser(String reserveId,
        ReserveUpdateRequestDto updateRequestDto) {
        return findById(reserveId)
            .zipWith(getUserId())
            .map(tuple -> {
                if (!tuple.getT1().isReserveUser(tuple.getT2())) {
                    //"해당 예약은 수정할 수 없습니다."
                    throw new BusinessMessageException(getMessage("valid.reserve_not_update"));
                }

                if (!tuple.getT1().isRequest()) {
                    //예약 신청 상태인 경우에만 수정 가능합니다.
                    throw new BusinessMessageException(getMessage("valid.reserve_not_update_status"));
                }

                return tuple.getT1().update(updateRequestDto);
            })
            .flatMap(validator::checkReserveItems)
            .onErrorResume(Mono::error)
            .flatMap(this::updateInventory)
            .onErrorResume(Mono::error)
            .flatMap(reserveRepository::save);
    }

    /**
     * 관리자 예약 수정
     *
     * @param reserveId
     * @param updateRequestDto
     * @return
     */
    private Mono<Reserve> updateReserve(String reserveId,
        ReserveUpdateRequestDto updateRequestDto) {
        return findById(reserveId)
            .map(reserve -> {
                if (!reserve.isRequest()) {
                    //예약 신청 상태인 경우에만 수정 가능합니다.
                    throw new BusinessMessageException(
                        getMessage("valid.reserve_not_update_status"));
                }
                return reserve.update(updateRequestDto);
            })
            .flatMap(validator::checkReserveItems)
            .onErrorResume(Mono::error)
            .flatMap(this::updateInventory)
            .onErrorResume(Mono::error)
            .flatMap(reserveRepository::save);
    }

    /**
     * 예약 정보 저장 시 재고 변경
     *
     * @param reserve
     * @return
     */
    private Mono<Reserve> updateInventory(Reserve reserve) {
        return Mono.just(reserve)
            .flatMap(it -> {
                if (it.isEducation()) {
                    return reserveItemServiceClient
                        .updateInventory(reserve.getReserveItemId(), reserve.getReserveQty())
                        .transform(CircuitBreakerOperator.of(circuitBreakerRegistry
                            .circuitBreaker(RESERVE_ITEM_CIRCUIT_BREAKER_NAME)))
                        .onErrorResume(throwable -> Mono.just(false))
                        .flatMap(isSuccess -> {
                            if (isSuccess) {
                                return Mono.just(reserve);
                            }
                            //재고 업데이트에 실패했습니다.
                            return Mono.error(new BusinessMessageException(getMessage("msg.inventory_failed")));
                        });
                }
                return Mono.just(it);
            });
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

}
