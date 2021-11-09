package org.egovframe.cloud.common.service;

import static org.egovframe.cloud.common.config.GlobalConstant.*;

import org.egovframe.cloud.common.dto.AttachmentEntityMessage;
import org.egovframe.cloud.common.util.MessageUtil;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;

import javax.annotation.Resource;

/**
 * org.egovframe.cloud.common.service.AbstractService
 * <p>
 * 표준프레임워크 EgovAbstractServiceImpl 을 상속하는 공통 추상 클래스이다.
 * 각 @Service 클래스는 이 클래스를 반드시 상속하여야 한다.(표준프레임워크 준수사항)
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/28    jaeyeolkim  최초 생성
 * </pre>
 * @since 2021/07/28
 */
public abstract class AbstractService extends EgovAbstractServiceImpl {

    @Resource(name = "messageUtil")
    protected MessageUtil messageUtil;

    /**
     * messageSource 에 코드값을 넘겨 메시지를 찾아 리턴한다.
     *
     * @param code
     * @return
     */
    protected String getMessage(String code) {
        return messageUtil.getMessage(code);
    }

    /**
     * messageSource 에 코드값과 인자를 넘겨 메시지를 찾아 리턴한다.
     *
     * @param code
     * @param args
     * @return
     */
    protected String getMessage(String code, Object[] args) {
        return messageUtil.getMessage(code, args);
    }

    /**
     * 게시물 저장 후 해당 정보를 첨부파일 entity에 입력하기 위해
     * 이벤트 메세지 발행
     *
     * @param entityMessage
     */
    protected void sendAttachmentEntityInfo(StreamBridge streamBridge, AttachmentEntityMessage entityMessage) {
        streamBridge.send(ATTACHMENT_ENTITY_BINDING_NAME,
            MessageBuilder.withPayload(entityMessage).build());
    }

}
