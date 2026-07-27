package org.egovframe.cloud.userservice.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * org.egovframe.cloud.userservice.config.RestTemplateConfig
 *
 * REST Template 설정 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/09/27
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *    수정일       수정자              수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/27    jooho       최초 생성
 * </pre>
 */
@Configuration
public class RestTemplateConfig {

    /** 연결 타임아웃. 상대 서버가 응답하지 않을 때 호출 스레드가 무한 대기하지 않도록 한다. */
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);

    /** 읽기 타임아웃. 연결 후 응답 본문이 오지 않는 경우를 제한한다. */
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(30);

    /**
     * REST Template 빈 등록
     *
     * @return RestTemplate REST Template
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(CONNECT_TIMEOUT);
        factory.setReadTimeout(READ_TIMEOUT);
        return new RestTemplate(factory);
    }

}
