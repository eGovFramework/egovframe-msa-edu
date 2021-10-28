package org.egovframe.cloud.userservice.api.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * org.egovframe.cloud.userservice.api.user.dto.UserUpdateRequestDto
 * <p>
 * 사용자 정보 수정 요청시 처리 가능한 정보만 담긴 DTO
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
public class UserUpdateRequestDto {

    @NotBlank(message = "{user.user_name}{valid.required}")
    private String userName;

    @NotBlank(message = "{user.email}{valid.required}")
    @Email
    private String email;

    // (숫자)(영문)(특수문자)(공백제거)(자리수)
    @Pattern(regexp = "((?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20})|()",
            message = "{valid.password}")
    private String password;

    @NotBlank(message = "{role}{valid.required}")
    private String roleId;

    @NotBlank(message = "{user.user_state_code}{valid.required}")
    private String userStateCode;

    @Builder
    public UserUpdateRequestDto(String userName, String email, String password, String roleId, String userStateCode) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.roleId = roleId;
        this.userStateCode = userStateCode;
    }

}
