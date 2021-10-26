package org.egovframe.cloud.reserverequestservice.domain;

import java.time.LocalDateTime;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
/**
 * org.egovframe.cloud.reserverequestservice.domain.ReserveRepositoryCustom
 *
 * 예약 도메인 repository custom interface
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
public interface ReserveRepositoryCustom {
    Mono<Reserve> insert(Reserve reserve);
    Flux<Reserve> findAllByReserveDate(Long reserveItemId, LocalDateTime startDate, LocalDateTime endDate);
    Mono<Long> findAllByReserveDateCount(Long reserveItemId, LocalDateTime startDate, LocalDateTime endDate);
}
