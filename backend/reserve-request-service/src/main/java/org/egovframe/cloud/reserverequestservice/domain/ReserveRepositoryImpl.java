package org.egovframe.cloud.reserverequestservice.domain;

import static org.springframework.data.relational.core.query.Criteria.*;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
/**
 * org.egovframe.cloud.reserverequestservice.domain.ReserveRepositoryImpl
 *
 * 예약 도메인 repository custom interface 구현체
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/09/27
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/27    shinmj       최초 생성
 * </pre>
 */
@RequiredArgsConstructor
public class ReserveRepositoryImpl implements ReserveRepositoryCustom {

    private final R2dbcEntityTemplate entityTemplate;

    /**
     * 예약 insert
     * pk(reserveId)를 서비스에서 생성하여 insert 하기 위함.
     *
     * @param reserve
     * @return
     */
    @Override
    public Mono<Reserve> insert(Reserve reserve) {
        return entityTemplate.insert(reserve);
    }

    /**
     * 조회 기간에 예약된 건 조회
     * 현 예약건은 제외
     *
     * @param reserveItemId
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public Flux<Reserve> findAllByReserveDate(Long reserveItemId, LocalDateTime startDate, LocalDateTime endDate) {
        return entityTemplate.select(Reserve.class)
            .matching(Query.query(where("reserve_item_id").is(reserveItemId)
                .and ("reserve_start_date").lessThanOrEquals(endDate)
                .and("reserve_end_date").greaterThanOrEquals(startDate)
            ))
            .all();
    }

    /**
     * 조회 기간에 예약된 건수 조회
     * 현 예약건은 제외
     *
     * @param reserveItemId
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public Mono<Long> findAllByReserveDateCount(Long reserveItemId, LocalDateTime startDate, LocalDateTime endDate) {
        return entityTemplate.select(Reserve.class)
            .matching(Query.query(where("reserve_item_id").is(reserveItemId)
                .and ("reserve_start_date").lessThanOrEquals(endDate)
                .and("reserve_end_date").greaterThanOrEquals(startDate)
                .and("reserve_status_id").not(ReserveStatus.CANCEL.getKey())
            ))
            .count();
    }
}
