package org.egovframe.cloud.portalservice.api.message;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.egovframe.cloud.portalservice.api.message.dto.MessageListResponseDto;
import org.egovframe.cloud.portalservice.domain.message.Message;
import org.egovframe.cloud.portalservice.domain.message.MessageRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles(profiles = "test")
class MessageApiControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MessageRepository messageRepository;

    private final static String API_URL = "/api/v1/messages/";

    @BeforeEach
    public void setup() throws Exception {
        messageRepository.save(Message.builder().messageId("test.one").messageKoName("테스트1").build());
        messageRepository.save(Message.builder().messageId("test.two").messageKoName("테스트2").build());
        messageRepository.save(Message.builder().messageId("test.three").messageKoName("테스트3").messageEnName("TEST3").build());
    }

    @AfterEach
    public void cleanup() throws Exception {
        messageRepository.deleteAll();
    }

    @Test
    public void 메시지_한글명_목록_조회된다() throws Exception {
        // given
        String lang = "ko";

        // when
        ResponseEntity<List<MessageListResponseDto>> responseEntity = restTemplate.exchange(API_URL + lang, HttpMethod.GET, null, new ParameterizedTypeReference<List<MessageListResponseDto>>(){});

        // then
        responseEntity.getBody().forEach(messageListResponseDto -> System.out.println("id = " + messageListResponseDto.getMessageId() + ", messageName() = " + messageListResponseDto.getMessageName()));
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.getBody().size()).isEqualTo(3);
    }
}