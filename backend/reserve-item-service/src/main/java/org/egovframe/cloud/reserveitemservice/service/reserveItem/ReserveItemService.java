package org.egovframe.cloud.reserveitemservice.service.reserveItem;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.cloud.common.exception.BusinessMessageException;
import org.egovframe.cloud.reactive.service.ReactiveAbstractService;
import org.egovframe.cloud.reserveitemservice.api.reserveItem.dto.ReserveItemListResponseDto;
import org.egovframe.cloud.reserveitemservice.api.reserveItem.dto.ReserveItemMainResponseDto;
import org.egovframe.cloud.reserveitemservice.api.reserveItem.dto.ReserveItemRelationResponseDto;
import org.egovframe.cloud.reserveitemservice.api.reserveItem.dto.ReserveItemRequestDto;
import org.egovframe.cloud.reserveitemservice.api.reserveItem.dto.ReserveItemResponseDto;
import org.egovframe.cloud.reserveitemservice.api.reserveItem.dto.ReserveItemSaveRequestDto;
import org.egovframe.cloud.reserveitemservice.api.reserveItem.dto.ReserveItemUpdateRequestDto;
import org.egovframe.cloud.reserveitemservice.config.RequestMessage;
import org.egovframe.cloud.reserveitemservice.domain.reserveItem.ReserveItem;
import org.egovframe.cloud.reserveitemservice.domain.reserveItem.ReserveItemRepository;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * org.egovframe.cloud.reserveitemservice.service.reserveItem.ReserveItemService
 *
 * 예약 물품 service class
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/09/13
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/13    shinmj       최초 생성
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ReserveItemService extends ReactiveAbstractService {

    private static final String RESERVE_CATEGORY_CODE = "reserve-category";
    private static final String RESERVE_CATEGORY_CODE_ALL = "all";
    private static final String INVENTORY_UPDATED_BINDING_NAME = "inventoryUpdated-out-0";
    private static final String EVENT_HEADER_NAME = "reserveUUID";

    private final ReserveItemRepository reserveItemRepository;
    private final StreamBridge streamBridge;


    /**
     * 목록 조회
     *
     * @param requestDto
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public Mono<Page<ReserveItemListResponseDto>> search(ReserveItemRequestDto requestDto, Pageable pageable) {
        return reserveItemRepository.search(requestDto, pageable)
                .flatMap(this::convertReserveItemListResponseDto)
                .collectList()
                .zipWith(reserveItemRepository.searchCount(requestDto, pageable))
                .flatMap(tuple -> Mono.just(new PageImpl<>(tuple.getT1(), pageable, tuple.getT2())));
    }

    /**
     * 목록 조회 - 사용자가 조회 시
     *
     * @param categoryId
     * @param requestDto
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public Mono<Page<ReserveItemListResponseDto>> searchForUser(String categoryId, ReserveItemRequestDto requestDto, Pageable pageable) {
        if (!RESERVE_CATEGORY_CODE_ALL.equals(categoryId)) {
            requestDto.setCategoryId(categoryId);
        }
        return reserveItemRepository.search(requestDto, pageable)
                .flatMap(this::convertReserveItemListResponseDto)
                .collectList()
                .zipWith(reserveItemRepository.searchCount(requestDto, pageable))
                .flatMap(tuple -> Mono.just(new PageImpl<>(tuple.getT1(), pageable, tuple.getT2())));
    }

    /**
     * 한건 조회
     *
     * @param reserveItemId
     * @return
     */
    @Transactional(readOnly = true)
    public Mono<ReserveItemResponseDto> findById(Long reserveItemId) {
        return reserveItemRepository.findById(reserveItemId)
                .switchIfEmpty(monoResponseStatusEntityNotFoundException(reserveItemId))
                .flatMap(this::convertReserveItemResponseDto);
    }

    /**
     * 저장
     *
     * @param saveRequestDto
     * @return
     */
    public Mono<ReserveItemResponseDto> save(ReserveItemSaveRequestDto saveRequestDto) {
        return reserveItemRepository.save(saveRequestDto.toEntity())
                .flatMap(this::convertReserveItemResponseDto);
    }

    /**
     * 수정
     *
     * @param id
     * @param updateRequestDto
     * @return
     */
    public Mono<Void> update(Long id, ReserveItemUpdateRequestDto updateRequestDto) {
        return reserveItemRepository.findById(id)
                .switchIfEmpty(monoResponseStatusEntityNotFoundException(id))
                .map(reserveItem -> reserveItem.update(updateRequestDto))
                .flatMap(reserveItemRepository::save)
                .then();
    }

    /**
     * 사용여부 업데이트
     *
     * @param reserveItemId
     * @param isUse
     * @return
     */
    public Mono<Void> updateIsUse(Long reserveItemId, Boolean isUse) {
        return reserveItemRepository.findById(reserveItemId)
                .switchIfEmpty(monoResponseStatusEntityNotFoundException(reserveItemId))
                .map(reserveItem -> reserveItem.updateIsUse(isUse))
                .flatMap(reserveItemRepository::save)
                .then();
    }

    /**
     * 예약 신청(관리자) 시 재고 변경
     *
     * @param reserveItemId
     * @param reserveQty
     * @return
     */
    public Mono<Boolean> updateInventory(Long reserveItemId, Integer reserveQty) {
        return reserveItemRepository.findById(reserveItemId)
                .switchIfEmpty(monoResponseStatusEntityNotFoundException(reserveItemId))
                .flatMap(reserveItem -> {
                    int qty = reserveItem.getInventoryQty() - reserveQty;
                    if (qty < 0) {
                        return Mono.just(false);
                    }
                    return reserveItemRepository.save(reserveItem.updateInventoryQty(qty)).thenReturn(true);
                });
    }

    /**
     * 예약 신청(사용자) 시 재고 변경
     *
     * @param reserveItemId
     * @param reserveQty
     * @return
     */
    public Mono<Void> updateInventoryThenSendMessage(Long reserveItemId, Integer reserveQty, String reserveId) {
        return reserveItemRepository.findById(reserveItemId)
                .switchIfEmpty(monoResponseStatusEntityNotFoundException(reserveItemId))
                .flatMap(reserveItem -> {
                    String validate = reserveItem.validate(reserveQty);
                    if (!"valid".equals(validate)) {
                        return Mono.error(new BusinessMessageException(getMessage(validate)));
                    }
                    return Mono.just(reserveItem.updateInventoryQty(reserveQty));
                })
                .flatMap(reserveItemRepository::save)
                .delayElement(Duration.ofSeconds(5))
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(reserveItem -> {
                    log.info("reserve item inventory updated success");
                    sendMessage(reserveId, true);
                })
                .doOnError(throwable -> {
                    log.info("reserve item inventory updated fail = {}", throwable.getMessage());
                    sendMessage(reserveId, false);
                }).then();

    }

    /**
     * 한건 조회 - 연관된 데이터도 같이 조회 (e.g. codename, location)
     *
     * @param reserveItemId
     * @return
     */
    public Mono<ReserveItemRelationResponseDto> findByIdWithRelations(Long reserveItemId) {
        return reserveItemRepository.findWithRelation(reserveItemId)
                .switchIfEmpty(monoResponseStatusEntityNotFoundException(reserveItemId))
                .flatMap(reserveItem ->
                        Mono.just(ReserveItemRelationResponseDto.builder().entity(reserveItem).build()));
    }

    /**
     * 각 카테고리별 최신 예약 물품 조회
     * 파라미터로 받는 갯수만큼 조회한다.
     *
     * @param count 조회할 갯수 0:전체
     * @return
     */
    public Mono<Map<String, Collection<ReserveItemMainResponseDto>>> findLatest(Integer count) {
        return reserveItemRepository.findCodeDetail(RESERVE_CATEGORY_CODE)
            .flatMap(code -> reserveItemRepository.findLatestByCategory(count, code.getCodeId()))
            .map(reserveItem -> ReserveItemMainResponseDto.builder().entity(reserveItem).build())
            .collectMultimap(ReserveItemMainResponseDto::getCategoryName);

    }


    /**
     * entity -> dto 변환
     *
     * @param reserveItem
     * @return
     */
    private Mono<ReserveItemResponseDto> convertReserveItemResponseDto(ReserveItem reserveItem) {
        return Mono.just(ReserveItemResponseDto.builder().reserveItem(reserveItem).build());
    }

    /**
     * entity -> dto 변환
     *
     * @param reserveItem
     * @return
     */
    private Mono<ReserveItemListResponseDto> convertReserveItemListResponseDto(ReserveItem reserveItem) {
        return Mono.just(ReserveItemListResponseDto.builder().entity(reserveItem).build());
    }

    /**
     * 재고 변경 성공 여부 이벤트 발생
     *
     * @param reserveId
     * @param isItemUpdated
     */
    private void sendMessage(String reserveId, Boolean isItemUpdated) {
        streamBridge.send(INVENTORY_UPDATED_BINDING_NAME,
            MessageBuilder.withPayload(
                RequestMessage.builder()
                    .reserveId(reserveId)
                    .isItemUpdated(isItemUpdated)
                    .build())
                .setHeader(EVENT_HEADER_NAME, reserveId).build());
    }

}
