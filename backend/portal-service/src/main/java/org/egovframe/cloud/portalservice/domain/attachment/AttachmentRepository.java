package org.egovframe.cloud.portalservice.domain.attachment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * org.egovframe.cloud.portalservice.domain.attachment.AttachmentRepository
 * <p>
 * 첨부파일 repository interface
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/07/14
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/14    shinmj  최초 생성
 * </pre>
 */
public interface AttachmentRepository extends JpaRepository<Attachment, AttachmentId>, AttachmentRepositoryCustom {
    Optional<Attachment> findAllByUniqueId(String uniqueId);
}
