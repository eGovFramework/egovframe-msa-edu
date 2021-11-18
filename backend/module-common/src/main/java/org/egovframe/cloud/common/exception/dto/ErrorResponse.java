package org.egovframe.cloud.common.exception.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PROTECTED;

/**
 * org.egovframe.cloud.common.exception.ErrorResponse
 * <p>
 * 일관된 예외처리를 제공하는 클래스
 * https://github.com/cheese10yun/spring-guide
 *
 * @author 표준프레임워크센터 jooho
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
@Getter
@NoArgsConstructor(access = PROTECTED)
public class ErrorResponse {
    private LocalDateTime timestamp;
    private String message;
    private int status;
    private String code;
    private List<FieldError> errors;

    private static final String DEFAULT_ERROR_MESSAGE = "ERROR";

    private ErrorResponse(final ErrorCode code, final List<FieldError> errors, MessageSource messageSource) {
        this.timestamp = LocalDateTime.now();
        this.message = messageSource.getMessage(code.getMessage(), new Object[]{}, DEFAULT_ERROR_MESSAGE, LocaleContextHolder.getLocale());
        this.status = code.getStatus();
        this.code = code.getCode();
        this.errors = new ArrayList<>(errors);
    }

    private ErrorResponse(final ErrorCode code, MessageSource messageSource) {
        this.timestamp = LocalDateTime.now();
        this.message = messageSource.getMessage(code.getMessage(), new Object[]{}, DEFAULT_ERROR_MESSAGE, LocaleContextHolder.getLocale());
        this.status = code.getStatus();
        this.code = code.getCode();
        this.errors = new ArrayList<>();
    }

    private ErrorResponse(final ErrorCode code, String customMessage) {
        this.timestamp = LocalDateTime.now();
        this.message = customMessage;
        this.status = code.getStatus();
        this.code = code.getCode();
        this.errors = new ArrayList<>();
    }

    /**
     * 사용자 정의 메시지를 받아 넘기는 경우
     *
     * @param code
     * @param customMessage
     * @return
     */
    public static ErrorResponse of(final ErrorCode code, final String customMessage) {
        return new ErrorResponse(code, customMessage);
    }

    /**
     * ErrorResponse 는 protected 를 선언하여 new 생성할 수 없도록 막아두고 static 메소드를 통해 생성할 수 있도록 하였다
     * ExceptionHandlerAdvice 에서 인자를 받아 ErrorResponse 객체를 생성한다
     *
     * @param code
     * @param bindingResult
     * @param messageSource
     * @return
     */
    public static ErrorResponse of(final ErrorCode code, final BindingResult bindingResult, MessageSource messageSource) {
        return new ErrorResponse(code, FieldError.of(bindingResult), messageSource);
    }

    /**
     * ErrorResponse 는 protected 를 선언하여 new 생성할 수 없도록 막아두고 static 메소드를 통해 생성할 수 있도록 하였다
     * ExceptionHandlerAdvice 에서 인자를 받아 ErrorResponse 객체를 생성한다
     *
     * @param code
     * @param messageSource
     * @return
     */
    public static ErrorResponse of(final ErrorCode code, MessageSource messageSource) {
        return new ErrorResponse(code, messageSource);
    }

    /**
     * ErrorResponse 는 protected 를 선언하여 new 생성할 수 없도록 막아두고 static 메소드를 통해 생성할 수 있도록 하였다
     * ExceptionHandlerAdvice 에서 인자를 받아 ErrorResponse 객체를 생성한다
     *
     * @param code
     * @param errors
     * @param messageSource
     * @return
     */
    public static ErrorResponse of(final ErrorCode code, final List<FieldError> errors, MessageSource messageSource) {
        return new ErrorResponse(code, errors, messageSource);
    }

    /**
     * ErrorResponse 는 protected 를 선언하여 new 생성할 수 없도록 막아두고 static 메소드를 통해 생성할 수 있도록 하였다
     * ExceptionHandlerAdvice 에서 인자를 받아 ErrorResponse 객체를 생성한다
     * java validator 에러 발생 시 에러 정보 중 필요한 내용만 FieldError 로 반환한다
     *
     * @param e
     * @return
     */
    public static ErrorResponse of(MethodArgumentTypeMismatchException e, MessageSource messageSource) {
        if (e.getValue() == null) {
            return new ErrorResponse(ErrorCode.INVALID_TYPE_VALUE, ErrorResponse.FieldError.of(e.getName(), "", e.getErrorCode()), messageSource);
        }

        final List<ErrorResponse.FieldError> errors =
            ErrorResponse.FieldError.of(e.getName(), String.valueOf(e.getValue()), e.getErrorCode());
        return new ErrorResponse(ErrorCode.INVALID_TYPE_VALUE, errors, messageSource);
    }

    /**
     * java validator 에러 발생 시 에러 정보 중 필요한 내용만 반환한다
     */
    @Getter
    @NoArgsConstructor(access = PROTECTED)
    public static class FieldError {
        private String message;
        private String field;
        private String rejectedValue;

        private FieldError(final String field, final String rejectedValue, final String message) {
            this.field = field;
            this.rejectedValue = rejectedValue;
            this.message = message;
        }

        public static List<FieldError> of(final String field, final String rejectedValue, final String message) {
            List<FieldError> fieldErrors = new ArrayList<>();
            fieldErrors.add(new FieldError(field, rejectedValue, message));
            return fieldErrors;
        }

        /**
         * BindingResult to FieldError
         *
         * @param bindingResult
         * @return
         */
        private static List<FieldError> of(final BindingResult bindingResult) {
            final List<org.springframework.validation.FieldError> fieldErrors = bindingResult.getFieldErrors();
            return fieldErrors.stream()
                    .map(error -> new FieldError(
                            error.getField(),
                            error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                            error.getDefaultMessage()))
                    .collect(Collectors.toList());
        }
    }

}