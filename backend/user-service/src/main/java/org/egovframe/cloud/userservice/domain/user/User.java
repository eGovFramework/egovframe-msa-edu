package org.egovframe.cloud.userservice.domain.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egovframe.cloud.common.domain.Role;
import org.egovframe.cloud.servlet.domain.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Arrays;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * org.egovframe.cloud.userservice.domain.user.User
 * <p>
 * 사용자 정보 엔티티
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
@DynamicInsert
@DynamicUpdate
@Entity
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "user_no")
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false, length = 50)
    private String userName;

    @Column(nullable = false, name = "email_addr", length = 100, unique = true)
    private String email;

    @Column(length = 100)
    private String encryptedPassword;

    @Enumerated(EnumType.STRING) // Enum 값을 String 문자열로 저장
    @Column(name = "role_id", nullable = false)
    private Role role;

    private String refreshToken;

    @Column(nullable = false, length = 20, columnDefinition = "varchar(20) default '00'")
    private String userStateCode;

    @Column
    private LocalDateTime lastLoginDate;

    @Column(nullable = false, columnDefinition = "tinyint default '0'")
    private Integer loginFailCount;

    @Column(length = 100)
    private String googleId;

    @Column(length = 100)
    private String kakaoId;

    @Column(length = 100)
    private String naverId;

    @Builder
    public User(String userName, String email, String encryptedPassword, String userId,
                Role role, String userStateCode, String googleId, String kakaoId, String naverId) {
        this.userName = userName;
        this.email = email;
        this.encryptedPassword = encryptedPassword;
        this.userId = userId;
        this.role = role;
        this.userStateCode = userStateCode;
        this.googleId = googleId;
        this.kakaoId = kakaoId;
        this.naverId = naverId;
    }

    /**
     * 사용자 명과 이메일 정보를 수정한다.
     *
     * @param username          사용자 명
     * @param email             이메일
     * @param encryptedPassword 암호화 비밀번호
     * @param roleId            권한 id
     * @param userStateCode     회원 상태 코드
     * @return
     */
    public User update(String username, String email, String encryptedPassword, String roleId, String userStateCode) {
        this.userName = username;
        this.email = email;
        this.encryptedPassword = encryptedPassword;
        this.role = Arrays.stream(Role.values()).filter(c -> c.getKey().equals(roleId)).findAny().orElse(null);
        this.userStateCode = userStateCode;

        return this;
    }

    /**
     * 사용자 refresh token 정보를 필드에 입력한다.
     *
     * @param refreshToken
     * @return
     */
    public User updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    /**
     * 사용자 비밀번호 정보를 필드에 입력한다.
     *
     * @param encryptedPassword 암호화 비밀번호
     * @return User 사용자 엔티티
     */
    public User updatePassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
        return this;
    }

    /**
     * 사용자 명과 이메일 정보를 수정한다.
     *
     * @param username
     * @param email
     * @return
     */
    public User updateInfo(String username, String email) {
        this.userName = username;
        this.email = email;

        return this;
    }

    /**
     * 사용자 상태 코드 정보를 필드에 입력한다.
     *
     * @param userStateCode 상태 코드
     * @return User 사용자 엔티티
     */
    public User updateUserStateCode(String userStateCode) {
        this.userStateCode = userStateCode;
        return this;
    }

    /**
     * 로그인 실패 시 로그인실패수를 증가시키고 5회 이상 실패한 경우 회원상태를 정지로 변경
     *
     * @return User 사용자 엔티티
     */
    public User failLogin() {
        this.loginFailCount = loginFailCount + 1;
        if (this.loginFailCount >= 5) {
            this.userStateCode = UserStateCode.HALT.getKey();
        }
        return this;
    }

    /**
     * 로그인 성공 시 로그인실패수와 마지막로그인일시 정보를 갱신
     *
     * @return User 사용자 엔티티
     */
    public User successLogin() {
        this.loginFailCount = 0;
        this.lastLoginDate = LocalDateTime.now();
        return this;
    }

    /**
     * 소셜 사용자 여부 반환
     *
     * @return boolean 소셜 사용자 여부
     */
    public boolean isSocialUser() {
        if (this.googleId != null && !"".equals(this.googleId)) return true;
        else if (this.kakaoId != null && !"".equals(this.kakaoId)) return true;
        else if (this.naverId != null && !"".equals(this.naverId)) return true;

        return false;
    }

    /**
     * 소셜 정보 설정
     *
     * @return User 사용자 엔티티
     */
    public User setSocial(String provider, String providerId) {
        switch (provider) {
            case "google":
                this.googleId = providerId;
                break;
            case "naver":
                this.naverId = providerId;
                break;
            case "kakao":
                this.kakaoId = providerId;
                break;
            default:
        }

        return this;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }
}
