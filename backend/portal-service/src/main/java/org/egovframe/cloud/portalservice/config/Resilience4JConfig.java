package org.egovframe.cloud.portalservice.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * org.egovframe.cloud.portalservice.config.Resilience4JConfig
 * <p>
 * Resilience4J Configuration
 * 기본 설정값으로 운영되어도 무방하다. 이 클래스는 필수는 아니다.
 * retry 기본값은 최대 3회이고, fallback 이 없는 경우에만 동작하므로 설정하지 않았다.
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/08/31
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/08/31    jaeyeolkim  최초 생성
 * </pre>
 */
@Configuration
public class Resilience4JConfig {

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> resilience4JCircuitBreakerFactoryCustomizer() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.TIME_BASED) // circuit breaker time 기반 처리
                .slowCallDurationThreshold(Duration.ofSeconds(10)) // 요청 지연으로 간주하는 시간
                .minimumNumberOfCalls(10) // 통계 최소 요청 건
                .build();

        return circuitBreakerFactory -> circuitBreakerFactory.configureDefault(
                id -> new Resilience4JConfigBuilder(id)
                        .circuitBreakerConfig(circuitBreakerConfig)
                        .build()
        );
    }
}
