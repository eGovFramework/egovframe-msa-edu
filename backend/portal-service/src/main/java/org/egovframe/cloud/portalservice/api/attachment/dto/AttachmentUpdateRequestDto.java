package org.egovframe.cloud.portalservice.api.attachment.dto;

import lombok.*;

/**
 * org.egovframe.cloud.portalservice.api.attachment.dto.AttachmentUpdateRequestDto
 * <p>
 * 첨부파일 수정 저장 시 요청 dto class
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
public class AttachmentUpdateRequestDto {

    private String uniqueId;
    private Boolean isDelete;

    @Builder
    public AttachmentUpdateRequestDto(String uniqueId, Boolean isDelete) {
        this.uniqueId = uniqueId;
        this.isDelete = isDelete;
    }
}
