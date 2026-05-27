package org.egovframe.cloud.common.exception;

import org.egovframe.cloud.common.exception.dto.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * org.egovframe.cloud.common.exception.BusinessExceptionTest
 * <p>
 * BusinessException 및 하위 예외 클래스 단위 테스트
 *
 * @author 표준프레임워크센터
 * @version 1.0
 * @since 2024/01/01
 *
 * <pre>
 * ===== 개정이력(Modification Information) =====
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2024/01/01    eGovFrame   최초 생성
 * </pre>
 */
class BusinessExceptionTest {

    @Test
    @DisplayName("사용자 정의 메시지로 BusinessException 생성 시 errorCode와 customMessage가 설정되어야 한다")
    void businessException_커스텀메시지_생성() {
        BusinessException ex = new BusinessException(ErrorCode.BUSINESS_CUSTOM_MESSAGE, "처리 중 오류가 발생했습니다");

        assertEquals(ErrorCode.BUSINESS_CUSTOM_MESSAGE, ex.getErrorCode());
        assertEquals("처리 중 오류가 발생했습니다", ex.getCustomMessage());
        assertEquals("처리 중 오류가 발생했습니다", ex.getMessage());
    }

    @Test
    @DisplayName("사전 정의된 ErrorCode로 BusinessException 생성 시 errorCode가 설정되어야 한다")
    void businessException_에러코드_생성() {
        BusinessException ex = new BusinessException("서버 로그용 메시지", ErrorCode.INTERNAL_SERVER_ERROR);

        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, ex.getErrorCode());
        assertEquals("서버 로그용 메시지", ex.getMessage());
        assertNull(ex.getCustomMessage());
    }

    @Test
    @DisplayName("ErrorCode만으로 BusinessException 생성 시 에러코드 메시지가 사용되어야 한다")
    void businessException_에러코드만으로_생성() {
        BusinessException ex = new BusinessException(ErrorCode.UNAUTHORIZED);

        assertEquals(ErrorCode.UNAUTHORIZED, ex.getErrorCode());
        assertEquals(ErrorCode.UNAUTHORIZED.getMessage(), ex.getMessage());
    }

    @Test
    @DisplayName("EntityNotFoundException 생성 시 ENTITY_NOT_FOUND 에러코드가 설정되어야 한다")
    void entityNotFoundException_생성() {
        EntityNotFoundException ex = new EntityNotFoundException("게시글을 찾을 수 없습니다");

        assertEquals(ErrorCode.ENTITY_NOT_FOUND, ex.getErrorCode());
        assertEquals("게시글을 찾을 수 없습니다", ex.getMessage());
        assertInstanceOf(BusinessException.class, ex);
    }

    @Test
    @DisplayName("BusinessMessageException 생성 시 BUSINESS_CUSTOM_MESSAGE 에러코드가 설정되어야 한다")
    void businessMessageException_생성() {
        BusinessMessageException ex = new BusinessMessageException("중복된 데이터입니다");

        assertEquals(ErrorCode.BUSINESS_CUSTOM_MESSAGE, ex.getErrorCode());
        assertEquals("중복된 데이터입니다", ex.getCustomMessage());
        assertInstanceOf(BusinessException.class, ex);
    }

    @Test
    @DisplayName("InvalidValueException 기본 생성 시 INVALID_INPUT_VALUE 에러코드가 설정되어야 한다")
    void invalidValueException_기본_생성() {
        InvalidValueException ex = new InvalidValueException("잘못된 입력값");

        assertEquals(ErrorCode.INVALID_INPUT_VALUE, ex.getErrorCode());
        assertEquals("잘못된 입력값", ex.getMessage());
        assertInstanceOf(BusinessException.class, ex);
    }

    @Test
    @DisplayName("InvalidValueException에 별도 ErrorCode 지정 시 해당 에러코드가 설정되어야 한다")
    void invalidValueException_커스텀에러코드_생성() {
        InvalidValueException ex = new InvalidValueException("중복 입력", ErrorCode.DUPLICATE_INPUT_INVALID);

        assertEquals(ErrorCode.DUPLICATE_INPUT_INVALID, ex.getErrorCode());
        assertEquals("중복 입력", ex.getMessage());
    }
}
