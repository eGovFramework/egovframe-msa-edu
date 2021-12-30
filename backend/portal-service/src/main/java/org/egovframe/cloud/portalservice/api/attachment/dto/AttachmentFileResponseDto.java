package org.egovframe.cloud.portalservice.api.attachment.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.egovframe.cloud.portalservice.domain.attachment.Attachment;
import org.egovframe.cloud.portalservice.domain.attachment.AttachmentId;

/**
 * org.egovframe.cloud.portalservice.api.attachment.dto.AttachmentFileResponseDto
 * <p>
 * 첨부파일 업로드 응답 dto class
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
public class AttachmentFileResponseDto extends AttachmentUploadResponseDto {
    private String physicalFileName;

    @Builder
    public AttachmentFileResponseDto(String originalFileName, String message, String fileType, long size, String physicalFileName) {
        this.originalFileName = originalFileName;
        this.message = message;
        this.fileType = fileType;
        this.size = size;
        this.physicalFileName = physicalFileName;
    }

    public Attachment toEntity(AttachmentId attachmentId, AttachmentUploadRequestDto uploadRequestDto) {
        return Attachment.builder()
            .attachmentId(attachmentId)
            .uniqueId(UUID.randomUUID().toString())
            .physicalFileName(this.physicalFileName)
            .originalFileName(this.originalFileName)
            .size(this.size)
            .fileType(this.fileType)
            .entityName(uploadRequestDto.getEntityName())
            .entityId(uploadRequestDto.getEntityId())
            .build();
    }
}
