package org.egovframe.cloud.portalservice.api.attachment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * org.egovframe.cloud.portalservice.api.attachment.dto.AttachmentUploadRequestDto
 * <p>
 * 첨부파일 업로드 저장 시 요청 dto class
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/07/21
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/21    shinmj  최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
@ToString
public class AttachmentUploadRequestDto {

    private String entityName;
    private String entityId;

    @Builder
    public AttachmentUploadRequestDto(String entityName, String entityId) {
        this.entityName = entityName;
        this.entityId = entityId;
    }
}
