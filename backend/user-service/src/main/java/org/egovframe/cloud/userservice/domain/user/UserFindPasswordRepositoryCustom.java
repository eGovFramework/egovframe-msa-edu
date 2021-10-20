package org.egovframe.cloud.userservice.domain.user;

/**
 * org.egovframe.cloud.userservice.domain.user.UserFindPasswordRepositoryCustom
 * <p>
 * 사용자 비밀번호 찾기 Querydsl 인터페이스
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
public interface UserFindPasswordRepositoryCustom {

    /**
     * 다음 요청 번호 조회
     *
     * @param emailAddr 이메일 주소
     * @return Integer 다음 요청 번호
     */
    Integer findNextRequestNo(String emailAddr);

}
