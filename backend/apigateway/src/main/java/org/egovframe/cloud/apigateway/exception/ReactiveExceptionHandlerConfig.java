package org.egovframe.cloud.apigateway.exception;

import lombok.extern.slf4j.Slf4j;
import org.egovframe.cloud.apigateway.exception.dto.ErrorCode;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * org.egovframe.cloud.apigateway.exception.ReactiveExceptionHandlerConfig
 * <p>
 * 에러 발생 시 에러 정보 중 필요한 내용만 반환한다
 * ErrorCode 에서 status, code, message 세 가지 속성을 의존한다
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
@Slf4j
//@Configuration
public class ReactiveExceptionHandlerConfig {

    private final MessageSource messageSource;

    public ReactiveExceptionHandlerConfig(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * 에러 발생 시 에러 정보 중 필요한 내용만 반환한다
     *
     * @return
     */
//    @Bean
    public ErrorAttributes errorAttributes() {
        return new DefaultErrorAttributes() {
            @Override
            public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
                Map<String, Object> defaultMap = super.getErrorAttributes(request, options);
                Map<String, Object> errorAttributes = new LinkedHashMap<>();

                int status = (int) defaultMap.get("status");
                ErrorCode errorCode = getErrorCode(status);
                String message = messageSource.getMessage(errorCode.getMessage(), null, LocaleContextHolder.getLocale());
                errorAttributes.put("timestamp", LocalDateTime.now());
                errorAttributes.put("message", message);
                errorAttributes.put("status", status);
                errorAttributes.put("code", errorCode.getCode());
                // API Gateway 에서 FieldError는 처리하지 않는다.

                log.error("getErrorAttributes()={}", defaultMap);
                return errorAttributes;
            }
        };
    }

    /**
     * 상태코드로부터 ErrorCode 를 매핑하여 리턴한다.
     *
     * @param status
     * @return
     */
    private ErrorCode getErrorCode(int status) {
        switch (status) {
            case 400:
                return ErrorCode.ENTITY_NOT_FOUND;
            case 401:
                return ErrorCode.UNAUTHORIZED;
            case 403:
                return ErrorCode.ACCESS_DENIED;
            case 404:
                return ErrorCode.NOT_FOUND;
            case 405:
                return ErrorCode.METHOD_NOT_ALLOWED;
            case 422:
                return ErrorCode.UNPROCESSABLE_ENTITY;
            default:
                return ErrorCode.INTERNAL_SERVER_ERROR;
        }
    }
}
