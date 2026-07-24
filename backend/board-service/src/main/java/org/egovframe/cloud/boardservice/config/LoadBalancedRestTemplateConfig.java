package org.egovframe.cloud.boardservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * user-service 인가 API 등 서비스 간 호출용 RestTemplate (Eureka 로드밸런싱).
 */
@Configuration
public class LoadBalancedRestTemplateConfig {

    @Bean
    @LoadBalanced
    public RestTemplate loadBalancedRestTemplate() {
        // 안정성: 기본 RestTemplate 은 connect/read 타임아웃이 없어(무한 대기)
        // 다운스트림 무응답 시 호출 스레드가 영구 블록될 수 있다(CWE-400).
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(30000);
        return new RestTemplate(factory);
    }

}
