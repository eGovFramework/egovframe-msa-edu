package org.egovframe.cloud.userservice.config;

import lombok.extern.slf4j.Slf4j;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;

/**
 * org.egovframe.cloud.userservice.config.CacheEventLogger
 *
 * 캐시 이밴트 로깅 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/22
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/22    jooho       최초 생성
 * </pre>
 */
@Slf4j
public class CacheEventLogger implements CacheEventListener<Object, Object> {

    /**
     * 캐시 이벤트 발생시 로깅
     *
     * @param cacheEvent 캐시 이벤트
     */
    public void onEvent(CacheEvent<? extends Object, ? extends Object> cacheEvent) {
        log.info("cache event ::: key: {} / oldvalue: {} / newvalue:{}", cacheEvent.getKey(), cacheEvent.getOldValue(), cacheEvent.getNewValue());
    }

}
