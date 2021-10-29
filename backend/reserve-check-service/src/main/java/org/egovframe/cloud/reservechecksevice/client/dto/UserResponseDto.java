package org.egovframe.cloud.reservechecksevice.client.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * org.egovframe.cloud.userservice.api.user.dto.UserResponseDto
 * <p>
 * 사용자 정보 요청시 사용되는 필요한 정보만 담긴 DTO
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
public class UserResponseDto {

    private String userId;
    private String userName;
    private String email;
    private String roleId;
    private String userStateCode;
    private String googleId;
    private String kakaoId;
    private String naverId;
    private Boolean isSocialUser;
    private Boolean hasPassword;

    @Builder
    public UserResponseDto(String userId, String userName, String email, String roleId, String userStateCode, String googleId, String kakaoId, String naverId, Boolean isSocialUser, Boolean hasPassword) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.roleId = roleId;
        this.userStateCode = userStateCode;
        this.googleId = googleId;
        this.kakaoId = kakaoId;
        this.naverId = naverId;
        this.isSocialUser = isSocialUser;
        this.hasPassword = hasPassword;
    }
}
