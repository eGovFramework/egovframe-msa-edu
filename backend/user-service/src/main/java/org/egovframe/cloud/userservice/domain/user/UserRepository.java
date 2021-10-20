package org.egovframe.cloud.userservice.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


/**
 * org.egovframe.cloud.userservice.domain.user.UserRepository
 * <p>
 * Spring Data JPA 에서 제공되는 JpaRepository 를 상속하는 인터페이스
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/07/01
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/01    jaeyeolkim  최초 생성
 * </pre>
 */
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    // email을 통해 이미 생성된 사용자인지 판단하기 위한 메소드
    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(String userId);
    Optional<User> findByRefreshToken(String refreshToken);
    List<User> findByEmailContains(String email);
    Optional<User> findByEmailAndUserName(String email, String userName);
    Optional<User> findByEmailAndUserIdNot(String email, String userId);
    Optional<User> findByGoogleId(String googleId);
    Optional<User> findByKakaoId(String kakaoId);
    Optional<User> findByNaverId(String naverId);
}
