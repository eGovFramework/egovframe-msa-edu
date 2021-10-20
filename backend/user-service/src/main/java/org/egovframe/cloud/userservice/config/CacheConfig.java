package org.egovframe.cloud.userservice.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * org.egovframe.cloud.userservice.config.CacheConfig
 *
 * 캐시 설정 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/21
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/21    jooho       최초 생성
 * </pre>
 */
@Configuration
@EnableCaching
@EnableAspectJAutoProxy(exposeProxy=true) // AopContext.currentProxy() 사용 옵션
public class CacheConfig {
}
