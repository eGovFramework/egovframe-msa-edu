package org.egovframe.cloud.reactive.exception;

import io.jsonwebtoken.ExpiredJwtException;
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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.webjars.NotFoundException;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionHandlerAdvice {

    private final MessageSource messageSource;

    /**
     * javax.validation.Valid or @Validated 으로 binding error 발생시 발생한다.
     * HttpMessageConverter 에서 등록한 HttpMessageConverter binding 못할경우 발생
     * 주로 @RequestBody, @RequestPart 어노테이션에서 발생
     *
     * @param e
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected Mono<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("handleMethodArgumentNotValidException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, e.getBindingResult(), messageSource);
        return Mono.just(response);
    }

    /**
     * 바인딩 객체 @ModelAttribute 으로 binding error 발생시 BindException 발생한다.
     * ref https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-modelattrib-method-args
     *
     * @param e
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected Mono<ErrorResponse> handleBindException(BindException e) {
        log.error("handleBindException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, e.getBindingResult(), messageSource);
        return Mono.just(response);
    }

    /**
     * 요청은 잘 만들어졌지만, 문법 오류로 인하여 따를 수 없습니다
     *
     * @param e
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(HttpClientErrorException.UnprocessableEntity.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected Mono<ErrorResponse> handleUnprocessableEntityException(HttpClientErrorException.UnprocessableEntity e) {
        log.error("handleUnprocessableEntityException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.UNPROCESSABLE_ENTITY, messageSource);
        return Mono.just(response);
    }

    /**
     * enum type 일치하지 않아 binding 못할 경우 발생
     * 주로 @RequestParam enum으로 binding 못했을 경우 발생
     *
     * @param e
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected Mono<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("handleMethodArgumentTypeMismatchException", e);
        final ErrorResponse response = ErrorResponse.of(e, messageSource);
        return Mono.just(response);
    }

    /**
     * 요청한 페이지가 존재하지 않는 경우
     *
     * @param e
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected Mono<ErrorResponse> handleNotFoundException(NotFoundException e) {
        log.error("handleNotFoundException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.NOT_FOUND, messageSource);
        return Mono.just(response);
    }

    /**
     * Authentication 객체가 필요한 권한을 보유하지 않은 경우 발생
     *
     * @param e
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(AccessDeniedException.class)
    protected Mono<ResponseEntity<ErrorResponse>> handleAccessDeniedException(AccessDeniedException e) {
        log.error("handleAccessDeniedException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.ACCESS_DENIED, messageSource);
        return Mono.just(ResponseEntity.status(HttpStatus.valueOf(ErrorCode.ACCESS_DENIED.getStatus()))
                .body(response));
    }

    /**
     * 사용자 인증되지 않은 경우 발생
     *
     * @param e
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
    protected Mono<ResponseEntity<ErrorResponse>> handleUnauthorizedException(HttpClientErrorException.Unauthorized e) {
        log.error("handleUnauthorizedException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.UNAUTHORIZED, messageSource);
        return Mono.just(ResponseEntity.status(HttpStatus.valueOf(ErrorCode.ACCESS_DENIED.getStatus()))
                .body(response));
    }

    /**
     * JWT 인증 만료
     *
     * @param e
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(ExpiredJwtException.class)
    protected Mono<ResponseEntity<ErrorResponse>> handleExpiredJwtException(ExpiredJwtException e) {
        log.error("handleExpiredJwtException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.JWT_EXPIRED, messageSource);
        return Mono.just(ResponseEntity.status(HttpStatus.valueOf(ErrorCode.ACCESS_DENIED.getStatus()))
                .body(response));
    }

    /**
     * 사용자에게 표시할 다양한 메시지를 직접 정의하여 처리하는 Business RuntimeException Handler
     * 개발자가 만들어 던지는 런타임 오류를 처리
     *
     * @param e
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(BusinessMessageException.class)
    protected Mono<ResponseEntity<ErrorResponse>> handleBusinessMessageException(BusinessMessageException e) {
        log.error("handleBusinessMessageException", e);
        final ErrorCode errorCode = e.getErrorCode();
        final String customMessage = e.getCustomMessage();
        final ErrorResponse response = ErrorResponse.of(errorCode, customMessage);
        return Mono.just(ResponseEntity.status(HttpStatus.valueOf(errorCode.getStatus()))
                .body(response));
    }

    /**
     * 개발자 정의 ErrorCode 를 처리하는 Business RuntimeException Handler
     * 개발자가 만들어 던지는 런타임 오류를 처리
     *
     * @param e
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(BusinessException.class)
    protected Mono<ResponseEntity<ErrorResponse>> handleBusinessException(BusinessException e) {
        log.error("handleBusinessException", e);
        final ErrorCode errorCode = e.getErrorCode();
        final ErrorResponse response = ErrorResponse.of(errorCode, messageSource);
        return Mono.just(ResponseEntity.status(HttpStatus.valueOf(errorCode.getStatus()))
                .body(response));
    }

    /**
     * default exception
     *
     * @param e
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected Mono<ErrorResponse> handleException(Exception e) {
        log.error("handleException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, messageSource);
        return Mono.just(response);
    }

    /**
     * javax.validation.Valid or @Validated 으로 binding error 발생시 발생한다.
     *
     * @param e
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected Mono<ErrorResponse> handleWebExchangeBindException(WebExchangeBindException e) {
        log.error("handleWebExchangeBindException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, e.getBindingResult(), messageSource);
        return Mono.just(response);

    }
}
