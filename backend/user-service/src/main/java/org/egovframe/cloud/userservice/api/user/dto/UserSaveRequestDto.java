package org.egovframe.cloud.userservice.api.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egovframe.cloud.common.domain.Role;
import org.egovframe.cloud.userservice.domain.user.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Arrays;
import java.util.UUID;

/**
 * org.egovframe.cloud.userservice.api.user.dto.UserSaveRequestDto
 * <p>
 * 사용자 정보 생성 요청시 처리 가능한 정보만 담긴 DTO
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
public class UserSaveRequestDto {

    @NotBlank(message = "{user.user_name}{valid.required}")
    private String userName;

    @NotBlank(message = "{user.email}{valid.required}")
    @Email
    private String email;

    // (숫자)(영문)(특수문자)(공백제거)(자리수)
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}",
            message = "{valid.password}")
    private String password;

    @NotBlank(message = "{role}{valid.required}")
    private String roleId;

    @NotBlank(message = "{user.user_state_code}{valid.required}")
    private String userStateCode;

    @Builder
    public UserSaveRequestDto(String userName, String email, String password, String roleId, String userStateCode) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.roleId = roleId;
        this.userStateCode = userStateCode;
    }

    /**
     * UserSaveRequestDto 의 필드 값을 User Entity 빌더를 사용하여 주입 후 User를 리턴한다.
     * UserSaveRequestDto 가 가지고 있는 User 의 필드만 세팅할 수 있게 된다.
     *
     * @param passwordEncoder
     * @return
     */
    public User toEntity(BCryptPasswordEncoder passwordEncoder) {
        return User.builder()
                .userName(userName)
                .email(email)
                .encryptedPassword(passwordEncoder.encode(password)) // 패스워드 인코딩
                .userId(UUID.randomUUID().toString()) // 사용자 아이디 랜덤하게 생성
                .role(Arrays.stream(Role.values()).filter(c -> c.getKey().equals(roleId)).findAny().orElse(null))
                .userStateCode(userStateCode)
                .build();
    }
}
