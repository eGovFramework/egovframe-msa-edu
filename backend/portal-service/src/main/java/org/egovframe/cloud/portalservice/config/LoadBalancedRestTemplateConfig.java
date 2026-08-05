package org.egovframe.cloud.portalservice.config;

import java.time.Duration;

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

    /** 연결 타임아웃. 상대 서버가 응답하지 않을 때 호출 스레드가 무한 대기하지 않도록 한다. */
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);

    /** 읽기 타임아웃. 연결 후 응답 본문이 오지 않는 경우를 제한한다. */
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(30);

    @Bean
    @LoadBalanced
    RestTemplate loadBalancedRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(CONNECT_TIMEOUT);
        factory.setReadTimeout(READ_TIMEOUT);
        return new RestTemplate(factory);
    }

}
