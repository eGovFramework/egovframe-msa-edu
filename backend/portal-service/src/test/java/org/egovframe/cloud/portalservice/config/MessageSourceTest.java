package org.egovframe.cloud.portalservice.config;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * org.egovframe.cloud.portalservice.config.MessageSourceTest
 * <p>
 * MessageSource 정상 동작 확인 테스트
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/07/16
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/16    jaeyeolkim  최초 생성
 * </pre>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles(profiles = "test")
public class MessageSourceTest {

    @Autowired
    MessageSource messageSource;

    @Test
    public void 메세지_읽어온다() throws Exception {
        // given
        String messageCode = "err.invalid.input.value";
        String messageName = "입력값이 올바르지 않습니다";

        // then
        String message = messageSource.getMessage(messageCode, new Object[]{}, "default", LocaleContextHolder.getLocale());
        System.out.println("message = " + message);
        Assertions.assertThat(message).isEqualTo(messageName);
    }
}
