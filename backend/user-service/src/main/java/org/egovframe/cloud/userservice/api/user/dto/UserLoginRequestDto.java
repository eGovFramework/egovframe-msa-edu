package org.egovframe.cloud.userservice.api.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * org.egovframe.cloud.userservice.api.user.dto.UserLoginRequestDto
 * <p>
 * 로그인 요청시 사용되는 필요한 정보만 담긴 DTO
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/06/30
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/06/30    jaeyeolkim  최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
public class UserLoginRequestDto {

    /**
     * 이메일
     */
    @Email
    private String email;

    /**
     * 비밀번호
     */
    // (숫자)(영문)(특수문자)(공백제거)(자리수)
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}",
            message = "{valid.password}")
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

    /**
     * 이름
     */
    private String name;

    /**
     * 사용자 로그인 요청 DTO 클래스 생성자
     * 빌더 패턴으로 객체 생성
     *
     * @param email    이메일
     * @param password 비밀번호
     * @param provider 공급자
     * @param token    토큰
     */
    @Builder
    public UserLoginRequestDto(String email, String password, String provider, String token, String name) {
        this.email = email;
        this.password = password;
        this.provider = provider;
        this.token = token;
        this.name = name;
    }

    /**
     * OAuth 로그인 정보 세팅
     *
     * @param email
     * @param password
     */
    public void setOAuthLoginInfo(String email, String password) {
        this.email = email;
        this.password = password;
    }

}
