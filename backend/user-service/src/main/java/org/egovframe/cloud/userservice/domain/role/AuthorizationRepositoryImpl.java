package org.egovframe.cloud.userservice.domain.role;

import com.google.common.base.CaseFormat;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.userservice.api.role.dto.AuthorizationListResponseDto;
import org.egovframe.cloud.userservice.domain.user.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * org.egovframe.cloud.userservice.domain.role.AuthorizationRepositoryImpl
 * <p>
 * 인가 Querydsl 구현 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/15
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/15    jooho       최초 생성
 * </pre>
 */
@RequiredArgsConstructor
public class AuthorizationRepositoryImpl implements AuthorizationRepositoryCustom {

    /**
     * DML 생성을위한 Querydsl 팩토리 클래스
     */
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 인가 페이지 목록 조회
     * 가급적 Entity 보다는 Dto를 리턴 - Entity 조회시 hibernate 캐시, 불필요 컬럼 조회, oneToOne N+1 문제 발생
     *
     * @param requestDto 요청 DTO
     * @param pageable   페이지 정보
     * @return Page<AuthorizationListResponseDto> 페이지 인가 목록 응답 DTO
     */
    @Override
	public Page<AuthorizationListResponseDto> findPage(RequestDto requestDto, Pageable pageable) {
        JPQLQuery<AuthorizationListResponseDto> query = getAuthorizationListJPQLQuery()
                .where(getBooleanExpressionKeyword(requestDto));

        //정렬
        pageable.getSort().stream().forEach(sort -> {
            Order order = sort.isAscending() ? Order.ASC : Order.DESC;
            String property = sort.getProperty();

            Path<Object> target = Expressions.path(Object.class, QAuthorization.authorization, CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, property));
            @SuppressWarnings({ "unchecked", "rawtypes" })
			OrderSpecifier<?> orderSpecifier = new OrderSpecifier(order, target);
            query.orderBy(orderSpecifier);
        });

        QueryResults<AuthorizationListResponseDto> result = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()) //페이징
                .fetchResults();

        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }

    /**
     * 권한 목록의 인가 전체 목록 조회
     *
     * @param roles 권한 목록
     * @return Page<AuthorizationListResponseDto> 페이지 인가 목록 응답 DTO
     */
    @Override
	public List<AuthorizationListResponseDto> findByRoles(List<String> roles) {
        JPQLQuery<AuthorizationListResponseDto> query = getAuthorizationListJPQLQuery()
                .where(JPAExpressions
                        .selectFrom(QRoleAuthorization.roleAuthorization)
                        .where(QRoleAuthorization.roleAuthorization.roleAuthorizationId.authorizationNo.eq(QAuthorization.authorization.authorizationNo)
                                .and(QRoleAuthorization.roleAuthorization.roleAuthorizationId.roleId.in(roles)))
                        .exists());

        QueryResults<AuthorizationListResponseDto> result = query.fetchResults();

        return result.getResults();
    }

    /**
     * 사용자의 인가 목록 조회
     *
     * @param userId 사용자 id
     * @return List<AuthorizationListResponseDto> 인가 목록 응답 DTO
     */
    @Override
	public List<AuthorizationListResponseDto> findByUserId(String userId) {
        JPQLQuery<AuthorizationListResponseDto> query = getAuthorizationListJPQLQuery()
                .where(JPAExpressions
                        .selectFrom(QRoleAuthorization.roleAuthorization)
                        .innerJoin(QUser.user)
                        .on(QUser.user.role.stringValue().eq(QRoleAuthorization.roleAuthorization.roleAuthorizationId.roleId))
                        .where(QRoleAuthorization.roleAuthorization.roleAuthorizationId.authorizationNo.eq(QAuthorization.authorization.authorizationNo)
                                .and(QUser.user.userId.eq(userId)))
                        .exists());

        QueryResults<AuthorizationListResponseDto> result = query.fetchResults();

        return result.getResults();
    }

    /**
     * 인가 목록 JPQL Query 반환
     *
     * @return JPQLQuery<AuthorizationListResponseDto> 인가 목록 JPQL Query
     */
    public JPQLQuery<AuthorizationListResponseDto> getAuthorizationListJPQLQuery() {
        return jpaQueryFactory
                .select(Projections.constructor(AuthorizationListResponseDto.class,
                        QAuthorization.authorization.authorizationNo,
                        QAuthorization.authorization.authorizationName,
                        QAuthorization.authorization.urlPatternValue,
                        QAuthorization.authorization.httpMethodCode,
                        QAuthorization.authorization.sortSeq
                ))
                .from(QAuthorization.authorization);
    }

    /**
     * 인가 다음 정렬 순서 조회
     *
     * @return Integer 다음 정렬 순서
     */
    @Override
	public Integer findNextSortSeq() {
        return jpaQueryFactory
                .select(QAuthorization.authorization.sortSeq.max().add(1).coalesce(1))
                .from(QAuthorization.authorization)
                .fetchOne();
    }

    /**
     * 인가 정렬 순서 수정
     *
     * @param startSortSeq    시작 정렬 순서
     * @param endSortSeq      종료 정렬 순서
     * @param increaseSortSeq 증가 정렬 순서
     * @return Long 수정 건수
     */
    @Override
	public Long updateSortSeq(Integer startSortSeq, Integer endSortSeq, int increaseSortSeq) {
        return jpaQueryFactory.update(QAuthorization.authorization)
                .set(QAuthorization.authorization.sortSeq, QAuthorization.authorization.sortSeq.add(increaseSortSeq))
                .where(isGoeSortSeq(startSortSeq),
                        isLoeSortSeq(endSortSeq))
                .execute();
    }

    /**
     * 요청 DTO로 동적 검색 표현식 리턴
     *
     * @param requestDto 요청 DTO
     * @return BooleanExpression 검색 표현식
     */
    private BooleanExpression getBooleanExpressionKeyword(RequestDto requestDto) {
        if (requestDto.getKeyword() == null || "".equals(requestDto.getKeyword())) return null;

        switch (requestDto.getKeywordType()) {
            case "authorizationName": // 인가 명
                return QAuthorization.authorization.authorizationName.containsIgnoreCase(requestDto.getKeyword());
            case "urlPatternValue": // URL 패턴 값
                return QAuthorization.authorization.urlPatternValue.containsIgnoreCase(requestDto.getKeyword());
            case "httpMethodCode": // Http Method 코드
                return QAuthorization.authorization.httpMethodCode.containsIgnoreCase(requestDto.getKeyword());
            default:
                return null;
        }
    }

    /**
     * 정렬 순서 이하 검색 표현식
     *
     * @param sortSeq 정렬 순서
     * @return BooleanExpression 검색 표현식
     */
    private BooleanExpression isLoeSortSeq(Integer sortSeq) {
        return sortSeq == null ? null : QAuthorization.authorization.sortSeq.loe(sortSeq);
    }

    /**
     * 정렬 순서 이상 검색 표현식
     *
     * @param sortSeq 정렬 순서
     * @return BooleanExpression 검색 표현식
     */
    private BooleanExpression isGoeSortSeq(Integer sortSeq) {
        return sortSeq == null ? null : QAuthorization.authorization.sortSeq.goe(sortSeq);
    }

}
