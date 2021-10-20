package org.egovframe.cloud.reserveitemservice.domain.location;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * org.egovframe.cloud.reserveitemservice.domain.location.LocationRepository
 *
 * 예약 지역 R2dbc repository 클래스
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/09/06
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/06    shinmj       최초 생성
 * </pre>
 */
@Repository
public interface LocationRepository extends R2dbcRepository<Location, Long> {

    /**
     * 검색조건(지역이름)을 포함한 목록조회
     *
     * @param locationName
     * @param pageable
     * @return
     */
    Flux<Location> findAllByLocationNameContainingOrderBySortSeq(String locationName, Pageable pageable);

    /**
     * 검색조건(지역이름)을 포함한 count
     * paging 처리를 하기 위해서 조회
     *
     * @param locationName
     * @return
     */
    Mono<Long> countAllByLocationNameContaining(String locationName);

    /**
     * paging 처리를 하기 위한 목록 조회
     *
     * @param pageable
     * @return
     */
    Flux<Location> findAllByOrderBySortSeq (Pageable pageable);

    Flux<Location> findAllByIsUseTrueOrderBySortSeq();

}

