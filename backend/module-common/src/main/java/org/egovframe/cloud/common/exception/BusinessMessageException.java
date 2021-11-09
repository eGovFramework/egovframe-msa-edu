package org.egovframe.cloud.common.exception;

import org.egovframe.cloud.common.exception.dto.ErrorCode;

/**
 * org.egovframe.cloud.common.exception.BusinessMessageException
 * <p>
 * 런타임시 비즈니스 로직상 사용자에게 알려줄 오류 메시지를 만들어 던지는 처리를 담당한다
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/07/28
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/28    jaeyeolkim  최초 생성
 * </pre>
 */
public class BusinessMessageException extends BusinessException {

    /**
     * 사용자에게 표시될 메시지와 상태코드 400 을 넘긴다
     *
     * @param customMessage
     */
    public BusinessMessageException(String customMessage) {
        super(ErrorCode.BUSINESS_CUSTOM_MESSAGE, customMessage);
    }

}
