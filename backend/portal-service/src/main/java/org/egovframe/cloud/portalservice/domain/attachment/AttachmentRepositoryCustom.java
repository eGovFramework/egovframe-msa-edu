package org.egovframe.cloud.portalservice.domain.attachment;

import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.portalservice.api.attachment.dto.AttachmentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * org.egovframe.cloud.portalservice.domain.attachment.AttachmentRepositoryCustom
 * <p>
 * 첨부파일 querydsl interface
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
public interface AttachmentRepositoryCustom {
    List<Attachment> findByCode(String attachmentCode);

    AttachmentId getId(String attachmentCode);

    Page<AttachmentResponseDto> search(RequestDto searchRequestDto, Pageable pageable);
}
