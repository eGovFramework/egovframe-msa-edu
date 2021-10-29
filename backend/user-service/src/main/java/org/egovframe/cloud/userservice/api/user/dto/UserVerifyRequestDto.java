package org.egovframe.cloud.userservice.api.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * org.egovframe.cloud.userservice.api.user.dto.UserVerifyRequestDto
 *
 * 사용자 검증 요청 DTO 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/09/16
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *    수정일       수정자              수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/16    jooho       최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
public class UserVerifyRequestDto {

    /**
     * 비밀번호
     */
    @Pattern(regexp = "((?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20})|()", message = "{valid.password}") // (숫자)(영문)(특수문자)(공백제거)(자리수)
    private String password;

    /**
     * 공급자
     */
    @NotBlank(message = "{common.provider}{valid.required}")
    private String provider;

    /**
     * 토큰
     */
    private String token;

}
