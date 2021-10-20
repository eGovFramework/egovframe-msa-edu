package org.egovframe.cloud.portalservice.api.message.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

/**
 * org.egovframe.cloud.portalservice.api.message.dto.MessageListResponseDto
 * <p>
 * Message 목록 조회 응답 dto
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/07/22
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/22    jaeyeolkim  최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
public class MessageListResponseDto {
    private String messageId;
    private String messageName;
}
