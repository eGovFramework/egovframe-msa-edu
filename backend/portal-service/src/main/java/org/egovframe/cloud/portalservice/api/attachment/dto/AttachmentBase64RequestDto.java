package org.egovframe.cloud.portalservice.api.attachment.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * org.egovframe.cloud.portalservice.api.attachment.dto.AttachmentBase64RequestDto
 * <p>
 * 첨부파일 Base64 업로드 요청 dto class
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
public class AttachmentBase64RequestDto {

    private String fieldName;
    private String fileType;
    private String fileBase64;
    private String originalName;
    private Long size;

    @Builder
    public AttachmentBase64RequestDto(String fieldName, String fileType, String fileBase64, String originalName, Long size) {
        this.fieldName = fieldName;
        this.fileType = fileType;
        this.fileBase64 = fileBase64;
        this.originalName = originalName;
        this.size = size;
    }
}
