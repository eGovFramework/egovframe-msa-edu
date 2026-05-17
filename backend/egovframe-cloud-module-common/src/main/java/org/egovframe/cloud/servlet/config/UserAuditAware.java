package org.egovframe.cloud.servlet.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * org.egovframe.cloud.servlet.config.UserAuditAware
 * <p>
 * JPA Entity 생성자/수정자 정보를 자동 입력한다.
 * AuthenticationFilter.doFilter 메소드에서 UsernamePasswordAuthenticationToken 정보를 세팅해주기 때문에
 * Authentication 에서 userId 값을 받아올 수 있게 된다.
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/07/08
 *
 * <pre>
 * ===== 개정이력(Modification Information) =====
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/08    jaeyeolkim  최초 생성
 * </pre>
 */
@Component
public class UserAuditAware implements AuditorAware<String> {

    /**
     * Auditing 기능이 활성화된 엔티티에 변경이 감지되면 호출되어 생성자/수정자 정보를 반환한다.
     *
     * @return
     */
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        // getName()은 UsernamePasswordAuthenticationToken·JWT 등에서 일관된 사용자 식별자를 준다.
        // 익명 사용자(anonymousUser)도 DB NOT NULL 제약을 만족하도록 반환한다.
        String userId = authentication.getName();
        return StringUtils.hasText(userId) ? Optional.of(userId) : Optional.empty();
    }
}