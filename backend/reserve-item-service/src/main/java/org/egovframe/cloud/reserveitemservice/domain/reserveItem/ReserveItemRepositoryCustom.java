package org.egovframe.cloud.reserveitemservice.domain.reserveItem;

import org.egovframe.cloud.reserveitemservice.api.reserveItem.dto.ReserveItemRequestDto;
import org.egovframe.cloud.reserveitemservice.domain.code.Code;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * org.egovframe.cloud.reserveitemservice.domain.reserveItem.ReserveItemRepositoryCustom
 *
 * 예약 물품 도메인 repository custom(query) interface
 * R2DBCEntityTemplate을 이용하여 쿼리하기 위한 Interface
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
public interface ReserveItemRepositoryCustom {

    Flux<ReserveItem> search(ReserveItemRequestDto requestDto, Pageable pageable);
    Mono<Long> searchCount(ReserveItemRequestDto requestDto, Pageable pageable);

    Mono<ReserveItem> findWithRelation(Long reserveItemId);

    Flux<ReserveItem> findLatestByCategory(Integer count, String categoryId);
    Flux<Code> findCodeDetail(String codeId);
}
