package org.egovframe.cloud.apigateway.exception.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * org.egovframe.cloud.apigateway.exception.dto.ErrorCodeTest
 *
 * ErrorCode 열거형 단위 테스트
 *
 * @author 표준프레임워크센터
 * @version 1.0
 * @since 2024/01/01
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2024/01/01    contributors  최초 생성
 * </pre>
 */
class ErrorCodeTest {

    @Test
    @DisplayName("INVALID_INPUT_VALUE - 상태코드 400, 코드 E001 반환")
    void INVALID_INPUT_VALUE_속성_확인() {
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;

        assertThat(errorCode.getStatus()).isEqualTo(400);
        assertThat(errorCode.getCode()).isEqualTo("E001");
        assertThat(errorCode.getMessage()).isEqualTo("err.invalid.input.value");
    }

    @Test
    @DisplayName("UNAUTHORIZED - 상태코드 401, 코드 E004 반환")
    void UNAUTHORIZED_속성_확인() {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        assertThat(errorCode.getStatus()).isEqualTo(401);
        assertThat(errorCode.getCode()).isEqualTo("E004");
        assertThat(errorCode.getMessage()).isEqualTo("err.unauthorized");
    }

    @Test
    @DisplayName("ACCESS_DENIED - 상태코드 403, 코드 E005 반환")
    void ACCESS_DENIED_속성_확인() {
        ErrorCode errorCode = ErrorCode.ACCESS_DENIED;

        assertThat(errorCode.getStatus()).isEqualTo(403);
        assertThat(errorCode.getCode()).isEqualTo("E005");
        assertThat(errorCode.getMessage()).isEqualTo("err.access.denied");
    }

    @Test
    @DisplayName("INTERNAL_SERVER_ERROR - 상태코드 500, 코드 E999 반환")
    void INTERNAL_SERVER_ERROR_속성_확인() {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        assertThat(errorCode.getStatus()).isEqualTo(500);
        assertThat(errorCode.getCode()).isEqualTo("E999");
        assertThat(errorCode.getMessage()).isEqualTo("err.internal.server");
    }

    @Test
    @DisplayName("SERVICE_UNAVAILABLE - 상태코드 503, 코드 E010 반환")
    void SERVICE_UNAVAILABLE_속성_확인() {
        ErrorCode errorCode = ErrorCode.SERVICE_UNAVAILABLE;

        assertThat(errorCode.getStatus()).isEqualTo(503);
        assertThat(errorCode.getCode()).isEqualTo("E010");
        assertThat(errorCode.getMessage()).isEqualTo("err.service.unavailable");
    }

    @Test
    @DisplayName("NOT_FOUND - 상태코드 404, 코드 E007 반환")
    void NOT_FOUND_속성_확인() {
        ErrorCode errorCode = ErrorCode.NOT_FOUND;

        assertThat(errorCode.getStatus()).isEqualTo(404);
        assertThat(errorCode.getCode()).isEqualTo("E007");
        assertThat(errorCode.getMessage()).isEqualTo("err.not.found");
    }

    @Test
    @DisplayName("모든 ErrorCode 항목이 정의되어 있음")
    void 전체_에러코드_항목_수_확인() {
        assertThat(ErrorCode.values()).hasSize(10);
    }
}
