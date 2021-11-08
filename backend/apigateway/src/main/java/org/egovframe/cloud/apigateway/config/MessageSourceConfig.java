package org.egovframe.cloud.apigateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * org.egovframe.cloud.apigateway.config.MessageSourceConfig
 * <p>
 * Spring MessageSource 설정
 * Message Domain 이 있는 portal-service 에서 messages.properties 를 공유 가능한 외부 위치에 생성한다.
 * 각 서비스에서 해당 파일을 통해 다국어를 지원하도록 한다.
 * module-common.jar 를 포함하지 않는 서비스에서는 이 configuration을 추가해주어야 한다.
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/08/09
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/08/09    jaeyeolkim  최초 생성
 * </pre>
 */
@Slf4j
@Configuration
public class MessageSourceConfig {

    @Value("${messages.directory}")
    private String messagesDirectory;

    @Value("${spring.profiles.active:default}")
    private String profile;

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        final String MESSAGES = "/messages";
        if ("default".equals(profile)) {
            Path fileStorageLocation = Paths.get(messagesDirectory).toAbsolutePath().normalize();
            String dbMessages = StringUtils.cleanPath("file://" + fileStorageLocation + MESSAGES);
            log.info("DB MessageSource location = {}", dbMessages);
            messageSource.setBasenames(dbMessages);
        } else {
            messageSource.setBasenames(messagesDirectory + MESSAGES);
        }
        messageSource.getBasenameSet().forEach(s -> log.info("messageSource getBasenameSet={}", s));

        messageSource.setCacheSeconds(60); // 메세지 파일 변경 감지 간격
        messageSource.setUseCodeAsDefaultMessage(true); // 메세지가 없으면 코드를 메세지로 한다
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        return messageSource;
    }
}
