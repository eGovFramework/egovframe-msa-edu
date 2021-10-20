package org.egovframe.cloud.userservice.domain.user;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.userservice.api.user.dto.UserListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * org.egovframe.cloud.userservice.domain.user.UserRepositoryImpl
 * <p>
 * 사용자 Querydsl 구현 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/09/23
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *    수정일       수정자              수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/23    jooho       최초 생성
 * </pre>
 */
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    /**
     * DML 생성을위한 Querydsl 팩토리 클래스
     */
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 사용자 페이지 목록 조회
     *
     * @param requestDto 요청 DTO
     * @param pageable   페이지 정보
     * @return Page<UserListResponseDto> 페이지 사용자 목록 응답 DTO
     */
    public Page<UserListResponseDto> findPage(RequestDto requestDto, Pageable pageable) {
        QueryResults<UserListResponseDto> result = jpaQueryFactory
                .select(Projections.constructor(UserListResponseDto.class,
                        QUser.user.userId,
                        QUser.user.userName,
                        QUser.user.email,
                        QUser.user.role,
                        QUser.user.userStateCode,
                        QUser.user.lastLoginDate,
                        QUser.user.loginFailCount
                ))
                .from(QUser.user)
                .where(getBooleanExpression(requestDto))
                .orderBy(QUser.user.userName.asc(), QUser.user.email.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }

    /**
     * 요청 DTO로 동적 검색 표현식 리턴
     *
     * @param requestDto 요청 DTO
     * @return BooleanExpression 검색 표현식
     */
    private BooleanExpression getBooleanExpression(RequestDto requestDto) {
        if (requestDto.getKeyword() == null || "".equals(requestDto.getKeyword())) return null;

        switch (requestDto.getKeywordType()) {
            case "userName": // 사용자 명
                return QUser.user.userName.containsIgnoreCase(requestDto.getKeyword());
            case "email": // 이메일 주소
                return QUser.user.email.containsIgnoreCase(requestDto.getKeyword());
            default:
                return null;
        }
    }

}
