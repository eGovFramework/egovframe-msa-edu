package org.egovframe.cloud.portalservice.api.message;

import lombok.RequiredArgsConstructor;
import org.egovframe.cloud.portalservice.api.message.dto.MessageListResponseDto;
import org.egovframe.cloud.portalservice.config.MessageSourceFiles;
import org.egovframe.cloud.portalservice.domain.message.MessageRepository;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * org.egovframe.cloud.portalservice.api.message.MessageApiController
 * <p>
 * Message 요청을 처리하는 REST API Controller
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/07/22
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/22    jaeyeolkim  최초 생성
 * </pre>
 */
@RequiredArgsConstructor // final이 선언된 모든 필드를 인자값으로 하는 생성자를 대신 생성하여, 빈을 생성자로 주입받게 한다.
@RestController
public class MessageApiController {

    private final MessageRepository messageRepository;
    private final MessageSource messageSource;
    private final MessageSourceFiles messageSourceFiles;

    /**
     * Message 목록 조회
     *
     * @param lang ko/en
     * @return
     * @deprecated Map 형태 반환을 기본으로 한다. 이 API는 사용하지 않는다.
     */
    @GetMapping("/api/v1/messages/{lang}/list-type")
    public List<MessageListResponseDto> findAllMessages(@PathVariable String lang) {
        return messageRepository.findAllMessages(lang);
    }

    /**
     * Message 목록 조회하여 Map 형태로 변환하여 반환한다
     *
     * @param lang ko/en
     * @return
     */
    @GetMapping("/api/v1/messages/{lang}")
    public Map<String, String> findAllMessagesMap(@PathVariable String lang) {
        return messageRepository.findAllMessagesMap(lang);
    }

    @GetMapping("/api/v1/messages/{code}/{lang}")
    public String getMessage(@PathVariable String code, @PathVariable String lang) {
        Locale locale = "en".equals(lang)? Locale.ENGLISH : Locale.KOREAN;
        return messageSource.getMessage(code, null, locale);
    }

    @GetMapping("/api/v1/messages/refresh")
    public int refresh() {
        return messageSourceFiles.create();
    }
}
