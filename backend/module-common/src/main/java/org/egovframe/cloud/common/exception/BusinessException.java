package org.egovframe.cloud.common.exception;

import org.egovframe.cloud.common.exception.dto.ErrorCode;

/**
 * org.egovframe.cloud.common.exception.BusinessException
 * <p>
 * 런타임시 비즈니스 로직상 사용자에게 알려줄 오류 메시지를 만들어 던지는 처리를 담당한다
 * 이 클래스를 상속하여 다양한 형태의 business exception 을 만들 수 있고,
 * 그것들은 모두 ExceptionHandlerAdvice BusinessException 처리 메소드에서 잡아낸다.
 * 상황에 맞게 에러 코드를 추가하고 이 클래스를 상속하여 사용할 수 있다.
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
public class BusinessException extends RuntimeException {

    private String customMessage;
    private ErrorCode errorCode;

    /**
     * 사용자 정의 메시지를 받아 처리하는 경우
     *
     * @param errorCode 400 에러
     * @param customMessage 사용자에게 표시할 메시지
     */
    public BusinessException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.customMessage = customMessage;
    }

    /**
     * 사전 정의된 에러코드 객체를 넘기는 경우
     *
     * @param message 서버에 남길 메시지
     * @param errorCode 사전 정의된 에러코드
     */
    public BusinessException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 사전 정의된 에러코드의 메시지를 서버에 남기고 에러코드 객체를 리턴한다
     * @param errorCode 사전 정의된 에러코드
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getCustomMessage() {
        return customMessage;
    }

}
