package org.egovframe.cloud.portalservice.domain.message;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * org.egovframe.cloud.portalservice.domain.message.CodeRepositoryCustom
 * <p>
 * Message 엔티티를 위한 Repository
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/07/22
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/22    jaeyeolkim  최초 생성
 * </pre>
 */
public interface MessageRepository extends JpaRepository<Message, String>, MessageRepositoryCustom {
}
