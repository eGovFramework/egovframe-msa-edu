package org.egovframe.cloud.userservice.domain.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

/**
 * org.egovframe.cloud.userservice.domain.user.UserFindPasswordRepositoryImpl
 * <p>
 * 사용자 비밀번호 찾기 Querydsl 구현 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/09/15
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *    수정일       수정자              수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/15    jooho       최초 생성
 * </pre>
 */
@RequiredArgsConstructor
public class UserFindPasswordRepositoryImpl implements UserFindPasswordRepositoryCustom {

    /**
     * DML 생성을위한 Querydsl 팩토리 클래스
     */
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 다음 요청 번호 조회
     *
     * @param emailAddr 이메일 주소
     * @return Integer 다음 요청 번호
     */
    @Override
    public Integer findNextRequestNo(String emailAddr) {
        return jpaQueryFactory
                .select(QUserFindPassword.userFindPassword.userFindPasswordId.requestNo.max().add(1).coalesce(1))
                .from(QUserFindPassword.userFindPassword)
                .where(QUserFindPassword.userFindPassword.userFindPasswordId.emailAddr.eq(emailAddr))
                .fetchOne();
    }

}
