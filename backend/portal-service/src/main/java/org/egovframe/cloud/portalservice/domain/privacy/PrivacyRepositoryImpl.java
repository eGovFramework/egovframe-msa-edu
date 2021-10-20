package org.egovframe.cloud.portalservice.domain.privacy;

import java.util.List;

import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.portalservice.api.privacy.dto.PrivacyListResponseDto;
import org.egovframe.cloud.portalservice.api.privacy.dto.PrivacyResponseDto;
import org.egovframe.cloud.portalservice.api.privacy.dto.QPrivacyListResponseDto;
import org.egovframe.cloud.portalservice.api.privacy.dto.QPrivacyResponseDto;
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
 * org.egovframe.cloud.portalservice.domain.privacy.PrivacyRepositoryImpl
 *
 * 개인정보처리방침 Querydsl 구현 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/23
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/23    jooho       최초 생성
 * </pre>
 */
@RequiredArgsConstructor
public class PrivacyRepositoryImpl {

    /**
     * DML 생성을위한 Querydsl 팩토리 클래스
     */
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 개인정보처리방침 페이지 목록 조회
     * 가급적 Entity 보다는 Dto를 리턴 - Entity 조회시 hibernate 캐시, 불필요 컬럼 조회, oneToOne N+1 문제 발생
     *
     * @param requestDto 요청 DTO
     * @param pageable   페이지 정보
     * @return Page<PrivacyListResponseDto> 페이지 개인정보처리방침 목록 응답 DTO
     */
    public Page<PrivacyListResponseDto> findPage(RequestDto requestDto, Pageable pageable) {
        JPQLQuery<PrivacyListResponseDto> query = jpaQueryFactory
                .select(new QPrivacyListResponseDto(
                        QPrivacy.privacy.privacyNo,
                        QPrivacy.privacy.privacyTitle,
                        QPrivacy.privacy.useAt,
                        QPrivacy.privacy.createdDate
                ))
                .from(QPrivacy.privacy)
                .where(getBooleanExpressionKeyword(requestDto));

        //정렬
        pageable.getSort().stream().forEach(sort -> {
            Order order = sort.isAscending() ? Order.ASC : Order.DESC;
            String property = sort.getProperty();

            Path<Object> target = Expressions.path(Object.class, QPrivacy.privacy, CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, property));
            @SuppressWarnings({ "rawtypes", "unchecked" })
            OrderSpecifier<?> orderSpecifier = new OrderSpecifier(order, target);
            query.orderBy(orderSpecifier);
        });

        QueryResults<PrivacyListResponseDto> result = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()) //페이징
                .fetchResults();

        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }

    /**
     * 사용여부로 개인정보처리방침 내림차순 전체 목록 조회
     *
     * @param useAt 사용 여부
     * @return List<PrivacyResponseDto> 개인정보처리방침 상세 응답 DTO List
     */
    public List<PrivacyResponseDto> findAllByUseAt(Boolean useAt) {
        return jpaQueryFactory
                .select(new QPrivacyResponseDto(
                        QPrivacy.privacy.privacyNo,
                        QPrivacy.privacy.privacyTitle,
                        QPrivacy.privacy.privacyContent,
                        QPrivacy.privacy.useAt
                ))
                .from(QPrivacy.privacy)
                .where(getBooleanExpressionUseAt(useAt))
                .orderBy(QPrivacy.privacy.privacyNo.desc())
                .fetch();
    }

    /**
     * 사용 여부 동적 검색 표현식 리턴
     *
     * @param useAt 사용 여부
     * @return BooleanExpression 검색 표현식
     */
    private BooleanExpression getBooleanExpressionUseAt(Boolean useAt) {
        return useAt == null ? null : QPrivacy.privacy.useAt.eq(useAt);
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
            case "privacyTitle": // 개인정보처리방침 제목
                return QPrivacy.privacy.privacyTitle.containsIgnoreCase(requestDto.getKeyword());
            case "privacyContent": // 개인정보처리방침 내용
                return QPrivacy.privacy.privacyContent.containsIgnoreCase(requestDto.getKeyword());
            default:
                return null;
        }
    }

}
