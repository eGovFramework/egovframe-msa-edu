package org.egovframe.cloud.portalservice.config;

import org.egovframe.cloud.common.exception.dto.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * org.egovframe.cloud.portalservice.config.ExceptionResponseTest
 * <p>
 * ExceptionResponse 정상 동작 확인 테스트
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
public class ExceptionResponseTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MessageSource messageSource;

    @Test
    public void 해당_데이터가_존재하지_않습니다_오류메시지가_일치한다() throws Exception {
        // given
        String nonExistCode = "99999";

        // when
        ResponseEntity<ErrorResponse> responseEntity = restTemplate.getForEntity("/api/v1/codes/" + nonExistCode, ErrorResponse.class);
        ErrorResponse errorResponse = responseEntity.getBody();
        System.out.println("errorResponse.getStatus() =" + errorResponse.getStatus() + ", errorResponse.getMessage() =" + errorResponse.getMessage());
        assertThat(responseEntity.getStatusCode().value()).isEqualTo(errorResponse.getStatus());
        assertThat(errorResponse.getMessage()).isEqualTo(messageSource.getMessage("err.entity.not.found", null, LocaleContextHolder.getLocale()));
    }
}
