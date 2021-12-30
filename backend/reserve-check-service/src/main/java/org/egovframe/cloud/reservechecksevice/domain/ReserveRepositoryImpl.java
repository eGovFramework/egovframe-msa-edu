package org.egovframe.cloud.reservechecksevice.domain;

import static org.springframework.data.relational.core.query.Criteria.where;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.egovframe.cloud.reservechecksevice.api.dto.ReserveRequestDto;
import org.egovframe.cloud.reservechecksevice.client.ReserveItemServiceClient;
import org.egovframe.cloud.reservechecksevice.client.UserServiceClient;
import org.egovframe.cloud.reservechecksevice.client.dto.UserResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * org.egovframe.cloud.reservechecksevice.domain.ReserveRepositoryImpl
 *
 * 예약 도메인 custom repository 구현 클래스
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
@RequiredArgsConstructor
public class ReserveRepositoryImpl implements ReserveRepositoryCustom{
    private static final String RESERVE_ITEM_CIRCUIT_BREAKER_NAME = "reserve-item";
    private static final String USER_CIRCUIT_BREAKER_NAME = "user";

    private final R2dbcEntityTemplate entityTemplate;
    private final ReserveItemServiceClient reserveItemServiceClient;
    private final UserServiceClient userServiceClient;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    /**
     * 조회조건 목록 조회
     *
     * @param requestDto
     * @param pageable
     * @return
     */
    @Override
    public Flux<Reserve> search(ReserveRequestDto requestDto, Pageable pageable) {

        return entityTemplate.select(Reserve.class)
                .matching(Query.query(Criteria.from(whereQuery(requestDto)))
                        .sort(Sort.by(Sort.Direction.DESC, "create_date"))
                        .with(pageable))
                .all()
                .flatMap(this::loadRelations)
                .switchIfEmpty(Flux.empty());
    }

    /**
     * 조회조건 목록 조회시 총 count 조회
     *
     * @param requestDto
     * @param pageable
     * @return
     */
    @Override
    public Mono<Long> searchCount(ReserveRequestDto requestDto, Pageable pageable) {
        return entityTemplate.select(Reserve.class)
                .matching(Query.query(Criteria.from(whereQuery(requestDto)))
                        .sort(Sort.by(Sort.Direction.DESC, "create_date"))
                        .with(pageable))
                .count();
    }

    /**
     * 예약정보 한건 조회시 relation 같이 조회
     *
     * @param reserveId
     * @return
     */
    @Override
    public Mono<Reserve> findReserveById(String reserveId) {
        return entityTemplate.selectOne(Query.query(where("reserve_id").is(reserveId)), Reserve.class)
                .flatMap(this::loadRelations)
                .switchIfEmpty(Mono.empty());

    }

    /**
     * 사용자 예약 목록 조회
     *
     * @param requestDto
     * @param pageable
     * @param userId
     * @return
     */
    @Override
    public Flux<Reserve> searchForUser(ReserveRequestDto requestDto, Pageable pageable, String userId) {
        Criteria where = Criteria.from(whereQuery(requestDto));
        return entityTemplate.select(Reserve.class)
                .matching(Query.query(where.and(where("user_id").is(userId)))
                        .sort(Sort.by(Sort.Direction.DESC, "create_date"))
                        .with(pageable))
                .all()
                .flatMap(this::loadRelations)
                .switchIfEmpty(Flux.empty());
    }

    /**
     * 사용자 예약 목록 건수 조회
     *
     * @param requestDto
     * @param pageable
     * @param userId
     * @return
     */
    @Override
    public Mono<Long> searchCountForUser(ReserveRequestDto requestDto, Pageable pageable, String userId) {
        Criteria where = Criteria.from(whereQuery(requestDto));
        return entityTemplate.select(Reserve.class)
                .matching(Query.query(where.and(where("user_id").is(userId)))
                        .sort(Sort.by(Sort.Direction.DESC, "create_date"))
                        .with(pageable))
                .count();
    }

    /**
     * relation 조회
     *
     * @param reserve
     * @return
     */
    @Override
    public Mono<Reserve> loadRelations(final Reserve reserve) {
        //load user
        Mono<Reserve> mono = Mono.just(reserve)
                .zipWith(findUserByUserId(reserve.getUserId()))
                .map(tuple -> tuple.getT1().setUser(tuple.getT2()))
                .switchIfEmpty(Mono.just(reserve));

        //load reserveItem
        mono = mono.zipWith(findReserveItemWithRelation(reserve.getReserveItemId()))
                .map(tuple -> tuple.getT1().setReserveItem(tuple.getT2()))
                .switchIfEmpty(Mono.just(reserve));

        return mono;
    }

    /**
     * 조회 기간에 예약된 건 조회
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
                        .and("reserve_status_id").not(ReserveStatus.CANCEL.getKey())
                ))
                .all();
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
    public Flux<Reserve> findAllByReserveDateWithoutSelf(String reserveId, Long reserveItemId, LocalDateTime startDate, LocalDateTime endDate) {
        return entityTemplate.select(Reserve.class)
            .matching(Query.query(where("reserve_item_id").is(reserveItemId)
                .and ("reserve_start_date").lessThanOrEquals(endDate)
                .and("reserve_end_date").greaterThanOrEquals(startDate)
                .and("reserve_id").not(reserveId)
                .and("reserve_status_id").not(ReserveStatus.CANCEL.getKey())
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
    public Mono<Long> findAllByReserveDateWithoutSelfCount(String reserveId, Long reserveItemId, LocalDateTime startDate, LocalDateTime endDate) {
        return entityTemplate.select(Reserve.class)
            .matching(Query.query(where("reserve_item_id").is(reserveItemId)
                .and ("reserve_start_date").lessThanOrEquals(endDate)
                .and("reserve_end_date").greaterThanOrEquals(startDate)
                .and("reserve_id").not(reserveId)
                .and("reserve_status_id").not(ReserveStatus.CANCEL.getKey())
            ))
            .count();
    }

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
     * 예약 물품 정보 조회
     *
     * @param reserveItemId
     * @return
     */
    private Mono<ReserveItem> findReserveItemWithRelation(Long reserveItemId) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(RESERVE_ITEM_CIRCUIT_BREAKER_NAME);

        return reserveItemServiceClient.findByIdWithRelations(reserveItemId)
            .transform(CircuitBreakerOperator.of(circuitBreaker))
            .onErrorResume(throwable -> Mono.empty())
            .switchIfEmpty(Mono.empty())
            .flatMap(reserveItemRelationResponseDto -> Mono.just(reserveItemRelationResponseDto.toEntity()));
    }

    /**
     * 예약자 정보 조회
     *
     * @param userId
     * @return
     */
    private Mono<UserResponseDto> findUserByUserId(String userId ) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(USER_CIRCUIT_BREAKER_NAME);
        return userServiceClient.findByUserId(userId)
            .transform(CircuitBreakerOperator.of(circuitBreaker))
            .onErrorResume(throwable -> Mono.empty());
    }

    /**
     * 조회조건 쿼리
     *
     * @param requestDto
     * @return
     */
    private List<Criteria> whereQuery(ReserveRequestDto requestDto) {
        List<Criteria>criteriaList = new ArrayList<>();

        if (requestDto.getLocationId() != null) {
            criteriaList.add(where("location_id").is(requestDto.getLocationId()));
        }

        if (requestDto.getCategoryId() != null) {
            criteriaList.add(where("category_id").is(requestDto.getCategoryId()));
        }

        if (StringUtils.hasText(requestDto.getKeyword())) {
            if ("item".equals(requestDto.getKeywordType())) {
                criteriaList.add(where("reserve_item_id").like(likeText(requestDto.getKeyword())));
            }
        }
        return criteriaList;
    }

    /**
     * like 검색
     *
     * @param keyword
     * @return
     */
    private String likeText(String keyword) {
        return "%" + keyword + "%";
    }
}
