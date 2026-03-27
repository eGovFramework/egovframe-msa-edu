package org.egovframe.cloud.portalservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * user-service 인가 API 등 서비스 간 호출용 RestTemplate (Eureka 로드밸런싱).
 */
@Configuration
public class LoadBalancedRestTemplateConfig {

    @Bean
    @LoadBalanced
    public RestTemplate loadBalancedRestTemplate() {
        return new RestTemplate();
    }

}
