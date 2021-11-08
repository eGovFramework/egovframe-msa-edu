package org.egovframe.cloud.portalservice.api.attachment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 * org.egovframe.cloud.portalservice.api.attachment.dto.AttachmentImageResponseDto
 * <p>
 * 이미지태그에 대한 응답 dto class
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
public class AttachmentImageResponseDto {
    private String mimeType;
    private byte[] data;

    @Builder
    public AttachmentImageResponseDto(String mimeType, byte[] data) {
        this.mimeType = mimeType;
        this.data = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            this.data[i] = data[i];
        }
    }
}
