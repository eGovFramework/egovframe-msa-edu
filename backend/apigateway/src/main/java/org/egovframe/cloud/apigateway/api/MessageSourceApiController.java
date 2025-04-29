package org.egovframe.cloud.apigateway.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

/**
 * org.egovframe.cloud.apigateway.api.MessageSourceApiController
 * <p>
 * MessageSource 정상 확인을 위한 컨트롤러
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/08/10
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/08/10    jaeyeolkim  최초 생성
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class MessageSourceApiController {
    private final MessageSource messageSource;

    @GetMapping("/api/v1/messages/{code}/{lang}")
    public String getMessage(@PathVariable String code, @PathVariable String lang) {
        Locale locale = "en".equals(lang)? Locale.ENGLISH : Locale.KOREAN;
        String message = messageSource.getMessage(code, null, locale);
        log.info("code={}, lang={}, message={}", code, lang, message);
        return message;
    }
}
