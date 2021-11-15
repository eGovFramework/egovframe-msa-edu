package org.egovframe.cloud.portalservice.domain.message;

import org.egovframe.cloud.portalservice.api.message.dto.MessageListResponseDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles(profiles = "test")
class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @BeforeEach
    @Transactional
    public void 메세지_입력된다() throws Exception {
        messageRepository.save(Message.builder().messageId("test.one").messageKoName("테스트1").build());
        messageRepository.save(Message.builder().messageId("test.two").messageKoName("테스트2").build());
        messageRepository.save(Message.builder().messageId("test.three").messageKoName("테스트3").messageEnName("TEST3").build());
    }

    @AfterEach
    @Transactional
    public void 메세지_삭제된다() throws Exception {
        messageRepository.deleteAll();
    }

    @Test
    public void 메세지_한글명_목록_조회된다() throws Exception {
        List<MessageListResponseDto> results = messageRepository.findAllMessages("ko");
        System.out.println("results.size() = " + results.size());
        assertThat(results.size()).isEqualTo(3);
        assertThat(results.get(0).getMessageId()).isEqualTo("test.one");
        results.forEach(messageListResponseDto -> System.out.println("id = " + messageListResponseDto.getMessageId() + ", messageName() = " + messageListResponseDto.getMessageName()));
    }

    @Test
    public void 메세지_영문명_목록_조회된다() throws Exception {
        List<MessageListResponseDto> results = messageRepository.findAllMessages("en");
        System.out.println("results.size() = " + results.size());
        assertThat(results.size()).isEqualTo(3);
        assertThat(results.get(1).getMessageName()).isEqualTo("TEST3");
        results.forEach(messageListResponseDto -> System.out.println("id = " + messageListResponseDto.getMessageId() + ", messageName() = " + messageListResponseDto.getMessageName()));
    }

    @Test
    public void 메세지_한글명_목록_Map으로_조회된다() throws Exception {
        Map<String, String> results = messageRepository.findAllMessagesMap("ko");
        System.out.println("results.size() = " + results.size());
        assertThat(results.size()).isEqualTo(3);
        results.keySet().forEach(key -> System.out.println(key + "=" + results.get(key)));
    }
}