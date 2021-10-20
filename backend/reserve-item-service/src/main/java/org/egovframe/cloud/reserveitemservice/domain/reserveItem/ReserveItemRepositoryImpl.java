package org.egovframe.cloud.reserveitemservice.domain.reserveItem;

import static org.springframework.data.relational.core.query.Criteria.*;

import java.util.ArrayList;
import java.util.List;

import org.egovframe.cloud.reserveitemservice.api.reserveItem.dto.ReserveItemRequestDto;
import org.egovframe.cloud.reserveitemservice.domain.code.Code;
import org.egovframe.cloud.reserveitemservice.domain.location.Location;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * org.egovframe.cloud.reserveitemservice.domain.reserveItem.ReserveItemRepositoryImpl
 *
 * 예약 물품 도메인 repository custom(query) 구현체
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
public class ReserveItemRepositoryImpl implements ReserveItemRepositoryCustom{

    private final R2dbcEntityTemplate entityTemplate;

    /**
     * page 목록 조회
     *
     * @param requestDto
     * @param pageable
     * @return
     */
    @Override
    public Flux<ReserveItem> search(ReserveItemRequestDto requestDto, Pageable pageable) {
        return entityTemplate.select(ReserveItem.class)
                .matching(Query.query(Criteria.from(whereQuery(requestDto)))
                        .sort(Sort.by(Sort.Direction.DESC, "create_date"))
                        .with(pageable))
                .all()
                .flatMap(this::loadRelations)
                .switchIfEmpty(Flux.empty());
    }

    /**
     * 목록 total count 조회
     *
     * @param requestDto
     * @param pageable
     * @return
     */
    @Override
    public Mono<Long> searchCount(ReserveItemRequestDto requestDto, Pageable pageable) {
        return entityTemplate.select(ReserveItem.class)
                .matching(Query.query(Criteria.from(whereQuery(requestDto)))
                        .sort(Sort.by(Sort.Direction.DESC, "create_date"))
                        .with(pageable))
                .count();
    }

    @Override
    public Flux<ReserveItem> searchForUser(String categoryId, ReserveItemRequestDto requestDto, Pageable pageable) {
        Criteria where = Criteria.from(whereQuery(requestDto));

        if (!"all".equals(categoryId)) {
            where = where.and(where("category_id").is(categoryId));
        }

        Query query = Query.query(where("use_at").isTrue().and(where))
                .sort(Sort.by(Sort.Direction.DESC, "create_date"))
                .with(pageable);

        return entityTemplate.select(ReserveItem.class)
                .matching(query)
                .all()
                .flatMap(this::loadRelations)
                .switchIfEmpty(Flux.empty());
    }


    @Override
    public Mono<Long> searchCountForUser(String categoryId, ReserveItemRequestDto requestDto, Pageable pageable) {
        Criteria where = Criteria.from(whereQuery(requestDto));

        if (!"all".equals(categoryId)) {
            where = where.and(where("category_id").is(categoryId));
        }

        Query query = Query.query(where("use_at").isTrue().and(where))
                .sort(Sort.by(Sort.Direction.DESC, "create_date"))
                .with(pageable);
        return entityTemplate.select(ReserveItem.class)
                .matching(query)
                .count();
    }

    /**
     * relation 걸린 table 정보도 같이 조회
     * 공통코드, 지역
     *
     * @param reserveItemId
     * @return
     */
    @Override
    public Mono<ReserveItem> findWithRelation(Long reserveItemId) {
        return entityTemplate.selectOne(Query.query(where("reserve_item_id").is(reserveItemId)), ReserveItem.class)
                .flatMap(this::loadRelationsAll)
                .switchIfEmpty(Mono.empty());
    }

    /**
     * 카테고리별 예약 물품 최신 데이터 count 만큼 조회
     *
     * @param count         조회할 갯수 0:전체
     * @param categoryId    카테고리 아이디
     * @return
     */
    @Override
    public Flux<ReserveItem> findLatestByCategory(Integer count, String categoryId) {
        Query query =Query.query(where("category_id").is(categoryId)
            .and("use_at").isTrue()).sort(Sort.by(Sort.Order.desc("create_date")));

        if (count > 0) {
            query.limit(count);
        }

        return entityTemplate.select(ReserveItem.class)
            .matching(query)
            .all()
            .flatMap(this::loadRelations);
    }

    /**
     * 공통코드 조회
     *
     * @param codeId
     * @return
     */
    @Override
    public Flux<Code> findCodeDetail(String codeId) {
        return entityTemplate.select(Code.class)
            .matching(Query.query(where("parent_code_id").is(codeId).and("use_at").isTrue()))
            .all();
    }

    /**
     * 유형만 공통코드 조회
     *
     * @param reserveItem
     * @return
     */
    private Mono<ReserveItem> loadRelations(final ReserveItem reserveItem) {
        //load common code
        Mono<ReserveItem> mono = Mono.just(reserveItem)
                .zipWith(findCodeById(reserveItem.getCategoryId()))
                .map(tuple -> tuple.getT1().setCategoryName(tuple.getT2().getCodeName()))
                .switchIfEmpty(Mono.just(reserveItem));

        // load location
        mono = mono.zipWith(findLocationById(reserveItem.getLocationId()))
                .map(tuple -> tuple.getT1().setLocation(tuple.getT2()))
                .switchIfEmpty(mono);

        return mono;
    }

    /**
     * 공통코드 이름 조회 (모든 공통코드에 대해 조회)
     *
     * @param reserveItem
     * @return
     */
    private Mono<ReserveItem> loadRelationsAll(final ReserveItem reserveItem) {
        //load common code
        Mono<ReserveItem> mono = Mono.just(reserveItem)
                .zipWith(findCodeById(reserveItem.getCategoryId()))
                .map(tuple -> tuple.getT1().setCategoryName(tuple.getT2().getCodeName()))
                .zipWith(findCodeById(reserveItem.getReserveMethodId()))
                .map(tuple -> tuple.getT1().setReserveMethodName(tuple.getT2().getCodeName()))
                .zipWith(findCodeById(reserveItem.getReserveMeansId()))
                .map(tuple -> tuple.getT1().setReserveMeansName(tuple.getT2().getCodeName()))
                .zipWith(findCodeById(reserveItem.getSelectionMeansId()))
                .map(tuple -> tuple.getT1().setSelectionMeansName(tuple.getT2().getCodeName()))
                .zipWith(findCodeById(reserveItem.getTargetId()))
                .map(tuple -> tuple.getT1().setTargetName(tuple.getT2().getCodeName()))
                .switchIfEmpty(Mono.just(reserveItem));

        // load location
        mono = mono.zipWith(findLocationById(reserveItem.getLocationId()))
                .map(tuple -> tuple.getT1().setLocation(tuple.getT2()))
                .switchIfEmpty(mono);

        return mono;
    }

    /**
     * 지역 조회
     *
     * @param locationId
     * @return
     */
    private Mono<Location> findLocationById(Long locationId ) {
        return entityTemplate.select(Location.class)
                .matching(Query.query(where("location_id").is(locationId)))
                .one()
                .switchIfEmpty(Mono.empty());
    }

    /**
     * 공통 코드 조회
     *
     * @param codeId
     * @return
     */
    private Mono<Code> findCodeById(String codeId ) {
        return entityTemplate.select(Code.class)
                .matching(Query.query(where("code_id").is(codeId)))
                .one()
                .switchIfEmpty(Mono.empty());

    }

    /**
     * 조회조건 쿼리
     *
     * @param requestDto
     * @return
     */
    private List<Criteria> whereQuery(ReserveItemRequestDto requestDto) {
        String keywordType = requestDto.getKeywordType();
        String keyword = requestDto.getKeyword();


        List<Criteria> whereCriteria = new ArrayList<>();

        if (StringUtils.hasText(keyword)) {
            if ("item".equals(keywordType)) {
                whereCriteria.add(where("reserve_item_name").like(likeText(keyword)));
            }
        }

        if (requestDto.getLocationId() != null) {
            whereCriteria.add(where("location_id").in(requestDto.getLocationId()));
        }

        if (requestDto.getCategoryId() != null ) {
            whereCriteria.add(where("category_id").in(requestDto.getCategoryId()));
        }

        // 물품 팝업에서 조회하는 경우 인터넷 예약이 가능한 물품만 조회
        if (requestDto.getIsUse()) {
            whereCriteria.add(where("use_at").isTrue());
            whereCriteria.add(where("reserve_method_id").is("internet"));
        }

        return whereCriteria;
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
