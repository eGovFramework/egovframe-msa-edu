package org.egovframe.cloud.reserveitemservice.api.reserveItem;

import java.util.Collection;
import java.util.Map;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.cloud.reserveitemservice.api.reserveItem.dto.ReserveItemListResponseDto;
import org.egovframe.cloud.reserveitemservice.api.reserveItem.dto.ReserveItemMainResponseDto;
import org.egovframe.cloud.reserveitemservice.api.reserveItem.dto.ReserveItemRelationResponseDto;
import org.egovframe.cloud.reserveitemservice.api.reserveItem.dto.ReserveItemRequestDto;
import org.egovframe.cloud.reserveitemservice.api.reserveItem.dto.ReserveItemResponseDto;
import org.egovframe.cloud.reserveitemservice.api.reserveItem.dto.ReserveItemSaveRequestDto;
import org.egovframe.cloud.reserveitemservice.api.reserveItem.dto.ReserveItemUpdateRequestDto;
import org.egovframe.cloud.reserveitemservice.service.reserveItem.ReserveItemService;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * org.egovframe.cloud.reserveitemservice.api.reserveItem.ReserveItemApiController
 * <p>
 * 예약 물품 api controller class
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
 *  2021/09/13    shinmj      최초 생성
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class ReserveItemApiController {

    private final ReserveItemService reserveItemService;

    private final Environment env;

    /**
     * 서비스 상태 확인
     *
     * @return
     */
    @GetMapping("/actuator/health-info")
    public String status() {
        return String.format("GET Reserve Item Service on" +
                "\n local.server.port :" + env.getProperty("local.server.port")
                + "\n egov.message :" + env.getProperty("egov.message")
        );
    }

    /**
     * 목록 조회
     *
     * @param requestDto
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/api/v1/reserve-items")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Page<ReserveItemListResponseDto>> search(ReserveItemRequestDto requestDto,
                                                         @RequestParam(name = "page") int page,
                                                         @RequestParam(name = "size") int size) {
        return reserveItemService.search(requestDto, PageRequest.of(page, size));
    }

    /**
     * 목록 조회 - 사용자 조회 시
     *
     * @param requestDto
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/api/v1/{categoryId}/reserve-items")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Page<ReserveItemListResponseDto>> searchForUser(@PathVariable String categoryId,
                                                                ReserveItemRequestDto requestDto,
                                                         @RequestParam(name = "page") int page,
                                                         @RequestParam(name = "size") int size) {
        return reserveItemService.searchForUser(categoryId, requestDto, PageRequest.of(page, size));
    }

    /**
     * 한건 조회
     *
     * @param reserveItemId
     * @return
     */
    @GetMapping("/api/v1/reserve-items/{reserveItemId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ReserveItemResponseDto> findById(@PathVariable Long reserveItemId) {
        return reserveItemService.findById(reserveItemId);
    }

    /**
     * 한건 등록
     *
     * @param saveRequestDto
     * @return
     */
    @PostMapping("/api/v1/reserve-items")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ReserveItemResponseDto> save(@Valid @RequestBody ReserveItemSaveRequestDto saveRequestDto) {
        return reserveItemService.save(saveRequestDto);
    }

    /**
     * 한건 수정
     *
     * @param reserveItemId
     * @param updateRequestDto
     * @return
     */
    @PutMapping("/api/v1/reserve-items/{reserveItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> update(@PathVariable Long reserveItemId, @Valid @RequestBody ReserveItemUpdateRequestDto updateRequestDto) {
        return reserveItemService.update(reserveItemId, updateRequestDto);
    }

    /**
     * 사용여부 업데이트
     *
     * @param reserveItemId
     * @param isUse
     * @return
     */
    @PutMapping("/api/v1/reserve-items/{reserveItemId}/{isUse}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> updateIsUse(@PathVariable Long reserveItemId, @PathVariable Boolean isUse) {
        return reserveItemService.updateIsUse(reserveItemId, isUse);
    }

    /**
     * 한건 조회 시 연관관계(지역, 공통코드) 데이터까지 모두 조회
     *
     * @param reserveItemId
     * @return
     */
    @GetMapping("/api/v1/reserve-items/relations/{reserveItemId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ReserveItemRelationResponseDto> findByIdWithRelations(@PathVariable Long reserveItemId) {
        return reserveItemService.findByIdWithRelations(reserveItemId);
    }

    /**
     * 관리자가 예약 신청 시 이벤트 스트림 없이 바로 재고 변경
     *
     * @param reserveItemId
     * @param reserveQty
     * @return
     */
    @PutMapping("/api/v1/reserve-items/{reserveItemId}/inventories")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Boolean> updateInventory(@PathVariable Long reserveItemId, @RequestBody Integer reserveQty) {
        return reserveItemService.updateInventory(reserveItemId, reserveQty);
    }

    /**
     * 각 카테고리별 최신 예약 물품 조회
     * 파라미터로 받는 갯수만큼 조회한다.
     *
     * @param count 조회할 갯수 0:전체
     * @return
     */
    @GetMapping("/api/v1/reserve-items/latest/{count}")
    @ResponseStatus(HttpStatus.OK)
    public  Mono<Map<String, Collection<ReserveItemMainResponseDto>>> findLatest(@PathVariable Integer count) {
        return reserveItemService.findLatest(count);
    }
}
