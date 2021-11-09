package org.egovframe.cloud.common.exception;

import org.egovframe.cloud.common.exception.dto.ErrorCode;

/**
 * org.egovframe.cloud.common.exception.EntityNotFoundException
 * <p>
 * 요청한 엔티티를 찾을 수 없을 경우 사용자에게 알려준다.
 * ExceptionHandlerAdvice BusinessException 처리 메소드에서 잡아낸다.
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/07/16
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/16    jaeyeolkim  최초 생성
 * </pre>
 */
public class EntityNotFoundException extends BusinessException {

    public EntityNotFoundException(String message) {
        super(message, ErrorCode.ENTITY_NOT_FOUND);
    }
}
