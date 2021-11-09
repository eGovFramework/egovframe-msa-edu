package org.egovframe.cloud.common.util;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Locale;

/**
 * org.egovframe.cloud.common.util.MessageUtil
 * <p>
 * MessageSource 값을 읽을 수 있는 메소드를 제공한다.
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/09/08
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/08    jaeyeolkim  최초 생성
 * </pre>
 */
@Component
public class MessageUtil {

    @Resource(name = "messageSource")
    private MessageSource messageSource;

    /**
     * messageSource 에 코드값을 넘겨 메시지를 찾아 리턴한다.
     *
     * @param code
     * @return
     */
    public String getMessage(String code) {
        return this.getMessage(code, new Object[]{});
    }

    /**
     * messageSource 에 코드값과 인자를 넘겨 메시지를 찾아 리턴한다.
     *
     * @param code
     * @param args
     * @return
     */
    public String getMessage(String code, Object[] args) {
        return this.getMessage(code, args, LocaleContextHolder.getLocale());
    }

    /**
     * messageSource 에 코드값, 인자, 지역정보를 넘겨 메시지를 찾아 리턴한다.
     *
     * @param code
     * @param args
     * @param locale
     * @return
     */
    public String getMessage(String code, Object[] args, Locale locale) {
        return messageSource.getMessage(code, args, locale);
    }

}
