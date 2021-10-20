package org.egovframe.cloud.portalservice.api.attachment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.egovframe.cloud.portalservice.domain.attachment.Attachment;

import java.time.LocalDateTime;

/**
 * org.egovframe.cloud.portalservice.api.attachment.dto.AttachmentResponseDto
 * <p>
 * 첨부파일 응답 dto class
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
@Getter
@NoArgsConstructor
@ToString
public class AttachmentResponseDto {
    private String code;
    private Long seq;
    private String id;
    private String physicalFileName;
    private String originalFileName;
    private Long size;
    private String fileType;
    private Long downloadCnt;
    private Boolean isDelete;
    private String entityName;
    private String entityId;
    private LocalDateTime createDate;

    @Builder
    public AttachmentResponseDto(Attachment attachment) {
        this.code = attachment.getAttachmentId().getCode();
        this.seq = attachment.getAttachmentId().getSeq();
        this.id = attachment.getUniqueId();
        this.physicalFileName = attachment.getPhysicalFileName();
        this.originalFileName = attachment.getOriginalFileName();
        this.size = attachment.getSize();
        this.fileType = attachment.getFileType();
        this.downloadCnt = attachment.getDownloadCnt();
        this.isDelete = attachment.getIsDelete();
        this.entityName = attachment.getEntityName();
        this.entityId = attachment.getEntityId();
        this.createDate = attachment.getCreatedDate();
    }

}
