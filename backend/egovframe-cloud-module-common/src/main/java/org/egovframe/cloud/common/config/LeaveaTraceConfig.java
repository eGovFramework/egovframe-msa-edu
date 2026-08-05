package org.egovframe.cloud.common.config;

import org.egovframe.rte.fdl.cmmn.trace.LeaveaTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * org.egovframe.cloud.common.config.LeaveaTraceConfig
 * <p>
 * LeaveaTrace Bean 설정
 * EgovAbstractServiceImpl 클래스가 의존한다.
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/09/24
 *
 * <pre>
 * ===== 개정이력(Modification Information) =====
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/24    jaeyeolkim  최초 생성
 *  2026/06/26    이백행         [2026년 컨트리뷰션] @Bean 메서드의 불필요한 public 접근제어자 제거
 * </pre>
 */
@Configuration
public class LeaveaTraceConfig {

    @Bean
    LeaveaTrace leaveaTrace() {
        return new LeaveaTrace();
    }

}
