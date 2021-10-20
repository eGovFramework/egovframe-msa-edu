package org.egovframe.cloud.portalservice.api;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * org.egovframe.cloud.portalservice.api.PortalApiController
 * <p>
 * 상태 확인 요청을 처리하는 REST API Controller
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
@RequiredArgsConstructor
@RestController
public class PortalApiController {
    private final Environment env;

    /**
     * 포털 서비스 상태 확인
     *
     * @return
     */
    @GetMapping("/actuator/health-portal")
    public String status() {
        return String.format("GET Portal Service on" +
                "\n local.server.port :" + env.getProperty("local.server.port")
                + "\n token expiration time :" + env.getProperty("token.expiration_time")
                + "\n egov.server.ip :" + env.getProperty("egov.server.ip")
                + "\n spring.datasource.username :" + env.getProperty("spring.datasource.username")
                + "\n spring.profiles.active :" + env.getProperty("spring.profiles.active")
                + "\n spring.cloud.config.label :" + env.getProperty("spring.cloud.config.label")
                + "\n spring.cloud.config.uri :" + env.getProperty("spring.cloud.config.uri")
                + "\n egov.message :" + env.getProperty("egov.message")
        );
    }

    /**
     * 포털 서비스 상태 확인
     *
     * @return
     */
    @PostMapping("/actuator/health-portal")
    public String poststatus() {
        return String.format("POST Portal Service on" +
                "\n local.server.port :" + env.getProperty("local.server.port")
                + "\n token expiration time :" + env.getProperty("token.expiration_time")
                + "\n egov.server.ip :" + env.getProperty("egov.server.ip")
                + "\n spring.datasource.username :" + env.getProperty("spring.datasource.username")
                + "\n egov.message :" + env.getProperty("egov.message")
        );
    }
}
