package org.egovframe.cloud.portalservice.config;

import org.egovframe.cloud.portalservice.client.decoder.CustomErrorDecoder;
import org.springframework.context.annotation.Bean;

import feign.Logger;
import feign.Retryer;
import feign.codec.ErrorDecoder;

/**
 * org.egovframe.cloud.portalservice.config.CustomFeignConfiguration
 * <p>
 *  feign client custom 설정 클래스
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/08/23
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/08/23    shinmj  최초 생성
 *  2026/06/26    이백행     [2026년 컨트리뷰션] @Bean 메서드의 불필요한 public 접근제어자 제거
 * </pre>
 */
public class CustomFeignConfiguration {

    /**
     * log level 설정
     *
     * @return
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    /**
     * 에러 핸들링
     *
     * @return
     */
    @Bean
    ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

    /**
     * retryer 설정
     *
     * @return
     */
    @Bean
    Retryer retryer() {
        return new Retryer.Default();
    }

}
