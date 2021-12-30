package org.egovframe.cloud.portalservice.api.attachment.dto;

import java.util.Objects;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.egovframe.cloud.portalservice.domain.attachment.Attachment;
import org.egovframe.cloud.portalservice.domain.attachment.AttachmentId;
import org.springframework.util.StringUtils;

/**
 * org.egovframe.cloud.portalservice.api.attachment.dto.AttachmentSaveRequestDto
 * <p>
 * 첨부파일 도메인 저장에대한 요청 dto class
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
public class AttachmentTempSaveRequestDto {
    private String uniqueId;
    private String physicalFileName;
    private String originalName;
    private Long size;
    private String fileType;
    private String entityName;
    private String entityId;
    private boolean isDelete;

    @Builder
    public AttachmentTempSaveRequestDto(String uniqueId, String physicalFileName,
                                        String originalName, Long size,
                                        String fileType, String entityName,
                                        String entityId, boolean isDelete) {
        this.uniqueId = uniqueId;
        this.physicalFileName = physicalFileName;
        this.originalName = originalName;
        this.size = size;
        this.fileType = fileType;
        this.entityName = entityName;
        this.entityId = entityId;
        this.isDelete = isDelete;
    }

    public boolean hasUniqueId() {
        return Objects.nonNull(uniqueId) || StringUtils.hasText(uniqueId);
    }

    public Attachment toEntity(AttachmentId attachmentId, String physicalFileName) {
        return Attachment.builder()
            .attachmentId(attachmentId)
            .uniqueId(UUID.randomUUID().toString())
            .physicalFileName(physicalFileName)
            .originalFileName(this.originalName)
            .size(this.size)
            .fileType(this.fileType)
            .entityName(this.entityName)
            .entityId(this.entityId)
            .build();
    }
}
