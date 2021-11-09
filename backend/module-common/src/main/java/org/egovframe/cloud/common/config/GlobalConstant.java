package org.egovframe.cloud.common.config;

/**
 * org.egovframe.cloud.common.config.Constants
 *
 * 공통 전역 상수 정의
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/07/19
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/19    jaeyeolkim  최초 생성
 * </pre>
 */
public interface GlobalConstant {
    final String HEADER_SITE_ID = "X-Site-Id"; // header에 어떤 사이트에서 보내는 요청인지 구분하기 위한 정보
    final String AUTHORIZATION_URI = "/api/v1/authorizations/check";
    final String REFRESH_TOKEN_URI = "/api/v1/users/token/refresh";
    final String MESSAGES_URI = "/api/v1/messages/**";
    final String LOGIN_URI = "/login";
    final String[] SECURITY_PERMITALL_ANTPATTERNS = {AUTHORIZATION_URI, REFRESH_TOKEN_URI, MESSAGES_URI, LOGIN_URI, "/actuator/**", "/v3/api-docs/**", "/api/v1/images/**", "/swagger-ui.html"};
    final String USER_SERVICE_URI = "/user-service";
    //예약 신청 후 재고 변경 성공여부 exchange name
    final String SUCCESS_OR_NOT_EX_NAME = "success-or-not.direct";
    // 첨부파일 저장 후 entity 정보 update binding name
    final String ATTACHMENT_ENTITY_BINDING_NAME = "attachmentEntity-out-0";
}
