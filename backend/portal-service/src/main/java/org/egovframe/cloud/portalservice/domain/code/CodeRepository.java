package org.egovframe.cloud.portalservice.domain.code;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * org.egovframe.cloud.portalservice.domain.code.CodeRepository
 * <p>
 * 공통코드 엔티티를 위한 Repository
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/07/12
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/12    jaeyeolkim  최초 생성
 * </pre>
 */
public interface CodeRepository extends JpaRepository<Code, String>, CodeRepositoryCustom {
    Optional<Code> findByCodeId(String codeId);
}
