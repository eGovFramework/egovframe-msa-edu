package org.egovframe.cloud.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
 *  2026/06/26    이백행         [2026년 컨트리뷰션] @Bean 메서드의 불필요한 public 접근제어자 제거
 * </pre>
 */
@Configuration
public class RestTemplateConfig {

    /**
     * REST Template 빈 등록
     *
     * @return RestTemplate REST Template
     */
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
