package org.egovframe.cloud.userservice.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * org.egovframe.cloud.userservice.domain.user.UserFindPasswordRepository
 *
 * 사용자 비밀번호 찾기 레파지토리 인터페이스
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
public interface UserFindPasswordRepository extends JpaRepository<UserFindPassword, UserFindPasswordId>, UserFindPasswordRepositoryCustom {

    /**
     * 토큰 값이 일치하는 사용자 비밀번호 찾기 조회
     *
     * @param tokenValue 토큰 값
     * @return Optional<UserFindPassword> 사용자 비밀번호 번경 요청 엔티티
     */
    Optional<UserFindPassword> findByTokenValue(String tokenValue);

}
