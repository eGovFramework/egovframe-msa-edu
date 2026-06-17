package org.egovframe.cloud.portalservice.utils;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class LoggingBindingSmokeTest {
    @Test
    void slf4jLoggerInitializesWithoutLog4jConflict() {
        assertDoesNotThrow(() -> {
            Logger log = LoggerFactory.getLogger(LoggingBindingSmokeTest.class);
            log.info("logging smoke");
        });
    }
}
