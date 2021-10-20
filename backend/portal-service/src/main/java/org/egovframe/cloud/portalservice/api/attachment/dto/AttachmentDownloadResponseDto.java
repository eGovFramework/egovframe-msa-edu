package org.egovframe.cloud.portalservice.api.attachment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;
/**
 * org.egovframe.cloud.portalservice.api.attachment.dto.AttachmentDownloadResponseDto
 * <p>
 * 첨부파일 다운로드 시 응답 dto class
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
public class AttachmentDownloadResponseDto {
    private String originalFileName;
    private Resource file;

    @Builder
    public AttachmentDownloadResponseDto(String originalFileName, Resource file) {
        this.originalFileName = originalFileName;
        this.file = file;
    }
}
