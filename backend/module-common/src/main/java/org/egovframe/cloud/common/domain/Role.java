package org.egovframe.cloud.common.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * org.egovframe.cloud.common.domain.Role
 * <p>
 * 사용자 권한
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
@RequiredArgsConstructor
public enum Role {

    // 스프링 시큐리티에서는 권한 코드에 항상 ROLE_ 이 앞에 있어야 한다.
    ANONYMOUS("ROLE_ANONYMOUS", "손님"),
    USER("ROLE_USER", "일반 사용자"),
    EMPLOYEE("ROLE_EMPLOYEE", "내부 사용자"),
    ADMIN("ROLE_ADMIN", "시스템 관리자");

    private final String key;
    private final String title;

    /**
     * 권한 id로 상수 검색
     *
     * @param key 권한 id
     * @return Role 권한 상수
     */
    public static Role findByKey(String key) {
        return Arrays.stream(Role.values()).filter(c -> c.getKey().equals(key)).findAny().orElse(null);
    }
}
