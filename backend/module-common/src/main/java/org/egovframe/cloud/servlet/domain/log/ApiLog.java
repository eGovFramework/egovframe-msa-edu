package org.egovframe.cloud.servlet.domain.log;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egovframe.cloud.servlet.domain.BaseTimeEntity;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * org.egovframe.cloud.servlet.domain.log.LoginLog
 * <p>
 * 로그인 로그 엔티티
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/09/01
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/01    jaeyeolkim  최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
@Entity
public class ApiLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "log_id")
    private Long id;

    private Long siteId;

    private String userId;

    @Column(length = 10)
    private String httpMethod;

    @Column(length = 500)
    private String requestUrl;

    @Column(name = "ip_addr", length = 100)
    private String remoteIp;

    @Builder
    public ApiLog(Long siteId, String userId, String httpMethod, String requestUrl, String remoteIp) {
        this.siteId = siteId;
        this.userId = userId;
        this.httpMethod = httpMethod;
        this.requestUrl = requestUrl;
        this.remoteIp = remoteIp;
    }
}
