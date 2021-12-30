package org.egovframe.cloud.reservechecksevice.client;

import org.egovframe.cloud.reservechecksevice.client.dto.ReserveItemRelationResponseDto;
import org.egovframe.cloud.reservechecksevice.client.dto.ReserveItemResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

/**
 * org.egovframe.cloud.reservechecksevice.client.ReserveItemServiceClient
 * <p>
 * 예약 물품 서비스와 통신하는 feign client interface
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/09/23
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/23    shinmj  최초 생성
 * </pre>
 */
@ReactiveFeignClient(value = "reserve-item-service")
public interface ReserveItemServiceClient {
    /**
     * 예약 물품 한건 조회
     *
     * @param reserveItemId
     * @return
     */
    @GetMapping("/api/v1/reserve-items/{reserveItemId}")
    Mono<ReserveItemResponseDto> findById(@PathVariable("reserveItemId") Long reserveItemId);

    /**
     * 예약 물품 한건 조회 시 연결된 공통코드, 지역 정보 조회
     *
     * @param reserveItemId
     * @return
     */
    @GetMapping("/api/v1/reserve-items/relations/{reserveItemId}")
    Mono<ReserveItemRelationResponseDto> findByIdWithRelations(@PathVariable("reserveItemId") Long reserveItemId);

    /**
     * 관리자가 예약 신청 시 이벤트 스트림 없이 바로 재고 변경
     *
     * @param reserveItemId
     * @param reserveQty
     * @return
     */
    @PutMapping("/api/v1/reserve-items/{reserveItemId}/inventories")
    Mono<Boolean> updateInventory(@PathVariable("reserveItemId") Long reserveItemId, @RequestBody Integer reserveQty);
}
