package org.egovframe.cloud.userservice.domain.log;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * org.egovframe.cloud.userservice.domain.log.LoginLogRepository
 * <p>
 * Spring Data JPA 에서 제공되는 JpaRepository 를 상속하는 인터페이스
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/09/01
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/01    jaeyeolkim  최초 생성
 * </pre>
 */
public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {
}
