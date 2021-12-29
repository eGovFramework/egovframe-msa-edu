package org.egovframe.cloud.userservice.api.user.dto;

import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egovframe.cloud.common.domain.Role;
import org.egovframe.cloud.userservice.domain.user.User;
import org.egovframe.cloud.userservice.domain.user.UserStateCode;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.UUID;

/**
 * org.egovframe.cloud.userservice.api.user.dto.UserJoinRequestDto
 *
 * 사용자 가입 요청 DTO 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/09/23
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *    수정일       수정자              수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/23    jooho       최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
public class UserJoinRequestDto {

    /**
     * 사용자 이름
     */
    @NotBlank(message = "{user.user_name}{valid.required}")
    private String userName;

    /**
     * 이메일
     */
    @NotBlank(message = "{user.email}{valid.required}")
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
     * 소셜 공급자
     */
    private String provider;

    /**
     * 소셜 토큰
     */
    private String token;

    public Boolean isProvider() {
        return Objects.nonNull(provider) && !"".equals(provider) & !"undefined".equals(provider)
            && Objects.nonNull(token) && !"".equals(token) && !"undefined".equals(token);
    }

    /**
     * UserSaveRequestDto 의 필드 값을 User Entity 빌더를 사용하여 주입 후 User를 리턴한다.
     * UserSaveRequestDto 가 가지고 있는 User 의 필드만 세팅할 수 있게 된다.
     *
     * @param passwordEncoder 비밀번호 인코더
     * @return User 사용자 엔티티
     */
    public User toEntity(BCryptPasswordEncoder passwordEncoder) {
        return User.builder()
                .userName(userName)
                .email(email)
                .encryptedPassword(passwordEncoder.encode(password)) // 패스워드 인코딩
                .userId(UUID.randomUUID().toString()) // 사용자 아이디 랜덤하게 생성
                .role(Role.USER) // 가입 시 기본 권한
                .userStateCode(UserStateCode.NORMAL.getKey()) // 승인 절차 없이 정상 상태로 가입
                .build();
    }
}
