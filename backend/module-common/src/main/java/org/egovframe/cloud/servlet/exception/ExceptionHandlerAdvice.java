package org.egovframe.cloud.servlet.exception;

import io.jsonwebtoken.ExpiredJwtException;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.cloud.common.exception.BusinessException;
import org.egovframe.cloud.common.exception.BusinessMessageException;
import org.egovframe.cloud.common.exception.dto.ErrorCode;
import org.egovframe.cloud.common.exception.dto.ErrorResponse;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * org.egovframe.cloud.common.exception.WebControllerAdvice
 * <p>
 * 모든 컨트롤러에 적용되는 컨트롤러 어드바이스 클래스
 * 예외 처리 (@ExceptionHandler), 바인딩 설정(@InitBinder), 모델 객체(@ModelAttributes)
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/07/15
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/15    jaeyeolkim  최초 생성
 * </pre>
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionHandlerAdvice {

    protected final MessageSource messageSource;

    /**
     * javax.validation.Valid or @Validated 으로 binding error 발생시 발생한다.
     * HttpMessageConverter 에서 등록한 HttpMessageConverter binding 못할경우 발생
     * 주로 @RequestBody, @RequestPart 어노테이션에서 발생
     *
     * @param e
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("handleMethodArgumentNotValidException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, e.getBindingResult(), messageSource);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * 바인딩 객체 @ModelAttribute 으로 binding error 발생시 BindException 발생한다.
     * ref https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-modelattrib-method-args
     *
     * @param e
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        log.error("handleBindException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, e.getBindingResult(), messageSource);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * 요청은 잘 만들어졌지만, 문법 오류로 인하여 따를 수 없습니다
     *
     * @param e
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(HttpClientErrorException.UnprocessableEntity.class)
    protected ResponseEntity<ErrorResponse> handleUnprocessableEntityException(HttpClientErrorException.UnprocessableEntity e) {
        log.error("handleUnprocessableEntityException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.UNPROCESSABLE_ENTITY, messageSource);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * 지원하지 않은 HTTP method 호출 할 경우 발생
     *
     * @param e
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("handleHttpRequestMethodNotSupportedException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.METHOD_NOT_ALLOWED, messageSource);
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * enum type 일치하지 않아 binding 못할 경우 발생
     * 주로 @RequestParam enum으로 binding 못했을 경우 발생
     *
     * @param e
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("handleMethodArgumentTypeMismatchException", e);
        final ErrorResponse response = ErrorResponse.of(e, messageSource);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * 요청한 페이지가 존재하지 않는 경우
     *
     * @param e
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        log.error("handleNotFoundException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.NOT_FOUND, messageSource);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Authentication 객체가 필요한 권한을 보유하지 않은 경우 발생
     *
     * @param e
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        log.error("handleAccessDeniedException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.ACCESS_DENIED, messageSource);
        return new ResponseEntity<>(response, HttpStatus.valueOf(ErrorCode.ACCESS_DENIED.getStatus()));
    }

    /**
     * 사용자 인증되지 않은 경우 발생
     *
     * @param e
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
    protected ResponseEntity<ErrorResponse> handleUnauthorizedException(HttpClientErrorException.Unauthorized e) {
        log.error("handleUnauthorizedException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.UNAUTHORIZED, messageSource);
        return new ResponseEntity<>(response, HttpStatus.valueOf(ErrorCode.ACCESS_DENIED.getStatus()));
    }

    /**
     * JWT 인증 만료
     *
     * @param e
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(ExpiredJwtException.class)
    protected ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException e) {
        log.error("handleExpiredJwtException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.JWT_EXPIRED, messageSource);
        return new ResponseEntity<>(response, HttpStatus.valueOf(ErrorCode.ACCESS_DENIED.getStatus()));
    }

    /**
     * 사용자에게 표시할 다양한 메시지를 직접 정의하여 처리하는 Business RuntimeException Handler
     * 개발자가 만들어 던지는 런타임 오류를 처리
     *
     * @param e
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(BusinessMessageException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessMessageException(BusinessMessageException e) {
        log.error("handleBusinessMessageException", e);
        final ErrorCode errorCode = e.getErrorCode();
        final String customMessage = e.getCustomMessage();
        final ErrorResponse response = ErrorResponse.of(errorCode, customMessage);
        return new ResponseEntity<>(response, HttpStatus.valueOf(errorCode.getStatus()));
    }

    /**
     * 개발자 정의 ErrorCode 를 처리하는 Business RuntimeException Handler
     * 개발자가 만들어 던지는 런타임 오류를 처리
     *
     * @param e
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.error("handleBusinessException", e);
        final ErrorCode errorCode = e.getErrorCode();
        final ErrorResponse response = ErrorResponse.of(errorCode, messageSource);
        return new ResponseEntity<>(response, HttpStatus.valueOf(errorCode.getStatus()));
    }

    /**
     * default exception
     *
     * @param e
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("handleException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, messageSource);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
