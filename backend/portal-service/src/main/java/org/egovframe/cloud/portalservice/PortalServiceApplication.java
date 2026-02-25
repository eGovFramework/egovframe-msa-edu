package org.egovframe.cloud.portalservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * org.egovframe.cloud.userservice.UserApplication
 * <p>
 * 유저 서비스 어플리케이션 클래스
 * Eureka Client 로 설정했기 때문에 Eureka Server 가 먼저 기동되어야 한다.
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
@ComponentScan(
	basePackages = {"org.egovframe.cloud.common", "org.egovframe.cloud.servlet", "org.egovframe.cloud.portalservice"},
	excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = org.egovframe.cloud.servlet.config.JpaConfig.class)
) // org.egovframe.cloud.common package 포함하기 위해
@EntityScan({"org.egovframe.cloud.servlet.domain", "org.egovframe.cloud.portalservice.domain"})
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class PortalServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortalServiceApplication.class, args);
    }

}
