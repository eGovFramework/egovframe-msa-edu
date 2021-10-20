package org.egovframe.cloud.userservice.domain.role;

import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.userservice.api.role.dto.QRoleListResponseDto;
import org.egovframe.cloud.userservice.api.role.dto.RoleListResponseDto;
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
 * org.egovframe.cloud.userservice.domain.role.RoleRepositoryImpl
 * <p>
 * 권한 Querydsl 구현 클래스
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
public class RoleRepositoryImpl implements RoleRepositoryCustom {

    /**
     * DML 생성을위한 Querydsl 팩토리 클래스
     */
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 권한 페이지 목록 조회
     *
     * @param requestDto 요청 DTO
     * @param pageable   페이지 정보
     * @return Page<RoleListResponseDto> 페이지 권한 목록 응답 DTO
     */
    @Override
	public Page<RoleListResponseDto> findPage(RequestDto requestDto, Pageable pageable) {
        JPQLQuery<RoleListResponseDto> query = jpaQueryFactory
                .select(new QRoleListResponseDto(
                        QRole.role.roleId,
                        QRole.role.roleName,
                        QRole.role.roleContent,
                        QRole.role.createdDate
                ))
                .from(QRole.role)
                .where(getBooleanExpressionKeyword(requestDto));

        //정렬
        pageable.getSort().stream().forEach(sort -> {
            Order order = sort.isAscending() ? Order.ASC : Order.DESC;
            String property = sort.getProperty();

            Path<Object> target = Expressions.path(Object.class, QRole.role, CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, property));
            @SuppressWarnings({ "unchecked", "rawtypes" })
            OrderSpecifier<?> orderSpecifier = new OrderSpecifier(order, target);
            query.orderBy(orderSpecifier);
        });

        QueryResults<RoleListResponseDto> result = query
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
            case "roleId": // 권한 id
                return QRole.role.roleId.containsIgnoreCase(requestDto.getKeyword());
            case "roleName": // 권한 명
                return QRole.role.roleName.containsIgnoreCase(requestDto.getKeyword());
            case "roleContent": // 권한 내용
                return QRole.role.roleContent.containsIgnoreCase(requestDto.getKeyword());
            default:
                return null;
        }
    }

}
