package org.egovframe.cloud.common.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * org.egovframe.cloud.common.domain.RoleTest
 * <p>
 * Role 열거형 단위 테스트
 *
 * @author 표준프레임워크센터
 * @version 1.0
 * @since 2024/01/01
 *
 * <pre>
 * ===== 개정이력(Modification Information) =====
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2024/01/01    eGovFrame   최초 생성
 * </pre>
 */
class RoleTest {

    @Test
    @DisplayName("각 Role의 key와 title이 올바르게 정의되어야 한다")
    void role_속성값_검증() {
        assertEquals("ROLE_ANONYMOUS", Role.ANONYMOUS.getKey());
        assertEquals("손님", Role.ANONYMOUS.getTitle());

        assertEquals("ROLE_USER", Role.USER.getKey());
        assertEquals("일반 사용자", Role.USER.getTitle());

        assertEquals("ROLE_EMPLOYEE", Role.EMPLOYEE.getKey());
        assertEquals("내부 사용자", Role.EMPLOYEE.getTitle());

        assertEquals("ROLE_ADMIN", Role.ADMIN.getKey());
        assertEquals("시스템 관리자", Role.ADMIN.getTitle());
    }

    @Test
    @DisplayName("유효한 key로 findByKey 호출 시 해당 Role이 반환되어야 한다")
    void findByKey_유효한키_반환() {
        assertEquals(Role.ANONYMOUS, Role.findByKey("ROLE_ANONYMOUS"));
        assertEquals(Role.USER, Role.findByKey("ROLE_USER"));
        assertEquals(Role.EMPLOYEE, Role.findByKey("ROLE_EMPLOYEE"));
        assertEquals(Role.ADMIN, Role.findByKey("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("존재하지 않는 key로 findByKey 호출 시 null이 반환되어야 한다")
    void findByKey_존재하지않는키_null반환() {
        assertNull(Role.findByKey("ROLE_UNKNOWN"));
        assertNull(Role.findByKey(""));
        assertNull(Role.findByKey("ADMIN"));
    }

    @Test
    @DisplayName("모든 Role key는 ROLE_ 접두어를 가져야 한다")
    void role_key_접두어_검증() {
        for (Role role : Role.values()) {
            assertTrue(role.getKey().startsWith("ROLE_"),
                    role.name() + "의 key가 ROLE_로 시작하지 않음: " + role.getKey());
        }
    }
}
