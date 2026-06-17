package org.egovframe.cloud.userservice;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SLF4J 바인딩이 단일(logback)임을 검증한다.
 *
 * <p>egovframe-rte-fdl-logging이 끌어오던 log4j-slf4j2-impl과 Spring Boot 기본
 * logback의 log4j-to-slf4j가 동시에 존재하면 로거 초기화 시
 * {@code LoggingException: log4j-slf4j2-impl cannot be present with log4j-to-slf4j}가
 * 발생한다. 이 테스트는 로거 초기화가 예외 없이 수행되는지 확인한다.</p>
 */
class LoggingBindingSmokeTest {

    @Test
    void slf4jLoggerInitializesWithoutLog4jConflict() {
        assertDoesNotThrow(() -> {
            Logger log = LoggerFactory.getLogger(LoggingBindingSmokeTest.class);
            log.info("logging smoke");
        });
    }
}
