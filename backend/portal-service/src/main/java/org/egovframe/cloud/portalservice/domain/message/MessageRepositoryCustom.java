package org.egovframe.cloud.portalservice.domain.message;

import java.util.List;
import java.util.Map;

import org.egovframe.cloud.portalservice.api.message.dto.MessageListResponseDto;

/**
 * org.egovframe.cloud.portalservice.domain.message.CodeRepositoryCustom
 * <p>
 * Message Querydsl interface
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
public interface MessageRepositoryCustom {
    List<MessageListResponseDto> findAllMessages(String lang);
    Map<String, String> findAllMessagesMap(String lang);
}
