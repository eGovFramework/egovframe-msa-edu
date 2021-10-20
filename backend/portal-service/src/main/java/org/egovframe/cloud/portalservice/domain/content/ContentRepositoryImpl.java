package org.egovframe.cloud.portalservice.domain.content;

import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.portalservice.api.content.dto.ContentListResponseDto;
import org.egovframe.cloud.portalservice.api.content.dto.QContentListResponseDto;
import org.egovframe.cloud.portalservice.domain.user.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.google.common.base.CaseFormat;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

/**
 * org.egovframe.cloud.portalservice.domain.content.ContentRepositoryImpl
 * <p>
 * 컨텐츠 Querydsl 구현 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/22
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/22    jooho       최초 생성
 * </pre>
 */
@RequiredArgsConstructor
public class ContentRepositoryImpl implements ContentRepositoryCustom {

    /**
     * DML 생성을위한 Querydsl 팩토리 클래스
     */
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 컨텐츠 페이지 목록 조회
     * 가급적 Entity 보다는 Dto를 리턴 - Entity 조회시 hibernate 캐시, 불필요 컬럼 조회, oneToOne N+1 문제 발생
     *
     * @param requestDto 요청 DTO
     * @param pageable   페이지 정보
     * @return Page<ContentListResponseDto> 페이지 컨텐츠 목록 응답 DTO
     */
    @Override
	public Page<ContentListResponseDto> findPage(RequestDto requestDto, Pageable pageable) {
        JPQLQuery<ContentListResponseDto> query = jpaQueryFactory
                .select(new QContentListResponseDto(
                        QContent.content.contentNo,
                        QContent.content.contentName,
                        Expressions.as(QUser.user.userName, "lastModifiedBy"),
                        QContent.content.modifiedDate
                ))
                .from(QContent.content)
                .leftJoin(QUser.user).on(QContent.content.lastModifiedBy.eq(QUser.user.userId))
                .fetchJoin()
                .where(getBooleanExpressionKeyword(requestDto));

        //정렬
        pageable.getSort().stream().forEach(sort -> {
            Order order = sort.isAscending() ? Order.ASC : Order.DESC;
            String property = sort.getProperty();

            Path<Object> target = Expressions.path(Object.class, QContent.content, CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, property));
            @SuppressWarnings({ "rawtypes", "unchecked" })
			OrderSpecifier<?> orderSpecifier = new OrderSpecifier(order, target);
            query.orderBy(orderSpecifier);
        });

        QueryResults<ContentListResponseDto> result = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()) //페이징
                .fetchResults();

        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
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
            case "contentName": // 컨텐츠 명
                return QContent.content.contentName.containsIgnoreCase(requestDto.getKeyword());
            case "contentRemark": // 컨텐츠 비고
                return QContent.content.contentRemark.containsIgnoreCase(requestDto.getKeyword());
            case "contentValue": // 컨텐츠 값
                return QContent.content.contentValue.containsIgnoreCase(requestDto.getKeyword());
            default:
                return null;
        }
    }

}
