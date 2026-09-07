package org.egovframe.cloud.common.exception;

import org.egovframe.cloud.common.exception.dto.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * org.egovframe.cloud.common.exception.ErrorCodeTest
 * <p>
 * ErrorCode 열거형 단위 테스트
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
class ErrorCodeTest {

    @Test
    @DisplayName("ErrorCode의 HTTP 상태코드, 에러코드, 메시지 키가 올바르게 정의되어야 한다")
    void errorCode_속성값_검증() {
        assertEquals(400, ErrorCode.INVALID_INPUT_VALUE.getStatus());
        assertEquals("E001", ErrorCode.INVALID_INPUT_VALUE.getCode());
        assertEquals("err.invalid.input.value", ErrorCode.INVALID_INPUT_VALUE.getMessage());

        assertEquals(400, ErrorCode.ENTITY_NOT_FOUND.getStatus());
        assertEquals("E003", ErrorCode.ENTITY_NOT_FOUND.getCode());

        assertEquals(401, ErrorCode.UNAUTHORIZED.getStatus());
        assertEquals("E004", ErrorCode.UNAUTHORIZED.getCode());

        assertEquals(403, ErrorCode.ACCESS_DENIED.getStatus());
        assertEquals("E006", ErrorCode.ACCESS_DENIED.getCode());

        assertEquals(500, ErrorCode.INTERNAL_SERVER_ERROR.getStatus());
        assertEquals("E999", ErrorCode.INTERNAL_SERVER_ERROR.getCode());
    }

    @Test
    @DisplayName("비즈니스 에러코드의 상태코드, 에러코드, 메시지 키가 올바르게 정의되어야 한다")
    void businessErrorCode_속성값_검증() {
        assertEquals(400, ErrorCode.BUSINESS_CUSTOM_MESSAGE.getStatus());
        assertEquals("B001", ErrorCode.BUSINESS_CUSTOM_MESSAGE.getCode());
        assertTrue(ErrorCode.BUSINESS_CUSTOM_MESSAGE.getMessage().isEmpty());

        assertEquals(400, ErrorCode.DUPLICATE_INPUT_INVALID.getStatus());
        assertEquals("B002", ErrorCode.DUPLICATE_INPUT_INVALID.getCode());
        assertEquals("err.duplicate.input.value", ErrorCode.DUPLICATE_INPUT_INVALID.getMessage());

        assertEquals(400, ErrorCode.DB_CONSTRAINT_DELETE.getStatus());
        assertEquals("B003", ErrorCode.DB_CONSTRAINT_DELETE.getCode());
        assertEquals("err.db.constraint.delete", ErrorCode.DB_CONSTRAINT_DELETE.getMessage());
    }

    @Test
    @DisplayName("ErrorCode는 총 14개 항목을 가져야 한다")
    void errorCode_총_개수_검증() {
        assertEquals(14, ErrorCode.values().length);
    }
}
