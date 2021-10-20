package org.egovframe.cloud.userservice.domain.role;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.userservice.api.role.dto.QRoleAuthorizationListResponseDto;
import org.egovframe.cloud.userservice.api.role.dto.RoleAuthorizationListRequestDto;
import org.egovframe.cloud.userservice.api.role.dto.RoleAuthorizationListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * org.egovframe.cloud.userservice.domain.role.RoleAuthorizationRepositoryImpl
 * <p>
 * 권한 인가 Querydsl 구현 클래스
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
public class RoleAuthorizationRepositoryImpl implements RoleAuthorizationRepositoryCustom {

    /**
     * DML 생성을위한 Querydsl 팩토리 클래스
     */
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 권한 인가 페이지 목록 조회
     * 인가 기준으로 권한 인가 아우터 조인
     * 가급적 Entity 보다는 Dto를 리턴 - Entity 조회시 hibernate 캐시, 불필요 컬럼 조회, oneToOne N+1 문제 발생
     *
     * @param requestDto 권한 인가 목록 요청 DTO
     * @param pageable   페이지 정보
     * @return Page<RoleAuthorizationListResponseDto> 페이지 권한 인가 목록 응답 DTO
     */
    public Page<RoleAuthorizationListResponseDto> findPageAuthorizationList(RoleAuthorizationListRequestDto requestDto, Pageable pageable) {
        JPQLQuery<RoleAuthorizationListResponseDto> query = jpaQueryFactory
                .select(new QRoleAuthorizationListResponseDto(
                        Expressions.as(Expressions.constant(requestDto.getRoleId()), "roleId"),
                        QAuthorization.authorization.authorizationNo,
                        QAuthorization.authorization.authorizationName,
                        QAuthorization.authorization.urlPatternValue,
                        QAuthorization.authorization.httpMethodCode,
                        QAuthorization.authorization.sortSeq,
                        Expressions.as(new CaseBuilder()
                                        .when(QRoleAuthorization.roleAuthorization.roleAuthorizationId.roleId.isNotNull()
                                                .and(QRoleAuthorization.roleAuthorization.roleAuthorizationId.authorizationNo.isNotNull()))
                                        .then(true)
                                        .otherwise(false)
                                , "createdAt") // 생성 여부
                ))
                .from(QAuthorization.authorization)
                .leftJoin(QRoleAuthorization.roleAuthorization).on(QAuthorization.authorization.authorizationNo.eq(QRoleAuthorization.roleAuthorization.roleAuthorizationId.authorizationNo)
                        .and(getBooleanExpressionRoleId(requestDto.getRoleId()))) // 권한 id
                .fetchJoin()
                .where(getBooleanExpressionKeyword(requestDto))
                .orderBy(QAuthorization.authorization.sortSeq.asc()); // 인가 정렬 순서 오름차순

        QueryResults<RoleAuthorizationListResponseDto> result = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()) //페이징
                .fetchResults();

        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }

    /**
     * 권한 id 검색 표현식 리턴
     *
     * @param roleId 권한 id
     * @return BooleanExpression
     */
    private BooleanExpression getBooleanExpressionRoleId(String roleId) {
        return roleId != null && !"".equals(roleId) ? QRoleAuthorization.roleAuthorization.roleAuthorizationId.roleId.eq(roleId) : null;
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

}
