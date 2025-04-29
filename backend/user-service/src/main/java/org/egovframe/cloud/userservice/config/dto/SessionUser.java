package org.egovframe.cloud.userservice.config.dto;

import lombok.Getter;
import org.egovframe.cloud.userservice.domain.user.User;

import java.io.Serializable;

/**
 * org.egovframe.cloud.userservice.config.dto.SessionUser
 * <p>
 * session 에 담을 사용자 정보
 * session 에 저장하기 위해 직렬화를 구현하였다
 * 토큰 사용시에는 사용되지 않는다.
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/07/06
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/06    jaeyeolkim  최초 생성
 * </pre>
 */
@Getter
public class SessionUser implements Serializable {
    private String userName;
    private String email;

    // 인증된 사용자 정보만 필요하여 두 컬럼만 정의
    public SessionUser(User user) {
        this.userName = user.getUserName();
        this.email = user.getEmail();
    }
}
