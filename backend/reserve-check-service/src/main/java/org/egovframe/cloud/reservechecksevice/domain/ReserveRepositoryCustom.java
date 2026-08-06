package org.egovframe.cloud.reservechecksevice.domain;

import java.time.LocalDateTime;
import java.util.Collection;

import org.egovframe.cloud.reservechecksevice.api.dto.ReserveRequestDto;
import org.springframework.data.domain.Pageable;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * org.egovframe.cloud.reservechecksevice.domain.ReserveRepositoryCustom
 *
 * 예약 도메인 custom Repository interface
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
public interface ReserveRepositoryCustom {
    Flux<Reserve> search(ReserveRequestDto requestDto, Pageable pageable);
    Mono<Long> searchCount(ReserveRequestDto requestDto, Pageable pageable);
    Mono<Reserve> findReserveById(String reserveId);

    Flux<Reserve> searchForUser(ReserveRequestDto requestDto, Pageable pageable, String userId);
    Mono<Long> searchCountForUser(ReserveRequestDto requestDto, Pageable pageable, String userId);

    Mono<Reserve> loadRelations(Reserve reserve);

    Flux<Reserve> findAllByReserveDate(Long reserveItemId, LocalDateTime startDate, LocalDateTime endDate);
    Flux<Reserve> findAllByReserveDateWithoutSelf(String reserveId, Long reserveItemId, LocalDateTime startDate, LocalDateTime endDate);
    Mono<Long> findAllByReserveDateWithoutSelfCount(String reserveId, Long reserveItemId, LocalDateTime startDate, LocalDateTime endDate);

    Mono<Reserve> insert(Reserve reserve);

    /**
     * 현재 상태가 허용 목록에 있을 때만 예약 상태를 변경한다.
     * WHERE 절에 현재 상태 조건을 포함한 단일 UPDATE 문으로 실행되므로, DB가 대상 행에 걸어주는
     * 잠금 범위 안에서 조건 확인과 반영이 함께 일어난다 - 동시에 같은 예약을 여러 번 호출해도
     * 실제로 상태가 바뀌는 것은 최대 1건이다.
     *
     * @param reserveId 예약 id
     * @param allowedCurrentStatuses 변경을 허용할 현재 상태 목록
     * @param newStatusId 변경할 상태
     * @return 실제로 변경된 행 수(0 또는 1). 0이면 다른 요청이 이미 처리했거나 애초에 대상 상태가 아니었다는 뜻이다
     */
    Mono<Long> updateStatusIfCurrentStatusIn(String reserveId, Collection<String> allowedCurrentStatuses, String newStatusId);

}
