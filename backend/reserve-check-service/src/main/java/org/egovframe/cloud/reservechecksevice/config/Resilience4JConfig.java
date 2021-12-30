package org.egovframe.cloud.reservechecksevice.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
 *  2021/10/05    shinmj      reactive로 변경
 * </pre>
 */
@Configuration
public class Resilience4JConfig {

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
            .failureRateThreshold(50) // Circuit 열지 말지 결정하는 실패 threshold 퍼센테이지
            .waitDurationInOpenState(Duration.ofSeconds(5)) // (half closed 전에) circuitBreaker가 open 되기 전에 기다리는 기간
            .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED) // circuit breaker count 기반 처리
            .slidingWindowSize(10) // 통계 대상 건수 -> N건의 요청중..
            .build();
        return CircuitBreakerRegistry.of(circuitBreakerConfig);
    }

}
