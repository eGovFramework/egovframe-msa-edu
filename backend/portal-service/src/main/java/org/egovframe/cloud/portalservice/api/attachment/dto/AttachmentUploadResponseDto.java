package org.egovframe.cloud.portalservice.api.attachment.dto;


import lombok.*;

/**
 * org.egovframe.cloud.portalservice.api.attachment.dto.AttachmentUploadResponseDto
 * <p>
 * 첨부파일 업로드에 대한 응답 dto 추상 class
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
public class AttachmentUploadResponseDto {
    protected String originalFileName;
    protected String message;
    protected String fileType;
    protected long size;

}
