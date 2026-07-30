package org.egovframe.cloud.apigateway.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

/**
 * org.egovframe.cloud.apigateway.api.MessageSourceApiControllerTest
 * <p>
 * 메세지소스 API 컨트롤러 단위 테스트 클래스
 *
 * @author 표준프레임워크센터
 * @version 1.0
 * @since 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
class MessageSourceApiControllerTest {

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private MessageSourceApiController messageSourceApiController;

    @Test
    @DisplayName("영문 언어코드요청 시 영어 로케일로 메세지를 조회한다")
    void should_returnEnglishMessage_when_langIsEn() {
        // given
        String code = "common.login";
        String lang = "en";
        String expectedMessage = "Login";
        given(messageSource.getMessage(eq(code), any(), eq(Locale.ENGLISH))).willReturn(expectedMessage);

        // when
        String result = messageSourceApiController.getMessage(code, lang);

        // then
        assertThat(result).isEqualTo(expectedMessage);
        verify(messageSource).getMessage(eq(code), any(), eq(Locale.ENGLISH));
    }

    @Test
    @DisplayName("그 외 언어코드요청 시 한국어 로케일로 메세지를 조회한다")
    void should_returnKoreanMessage_when_langIsNotEn() {
        // given
        String code = "common.login";
        String lang = "ko";
        String expectedMessage = "로그인";
        given(messageSource.getMessage(eq(code), any(), eq(Locale.KOREAN))).willReturn(expectedMessage);

        // when
        String result = messageSourceApiController.getMessage(code, lang);

        // then
        assertThat(result).isEqualTo(expectedMessage);
        verify(messageSource).getMessage(eq(code), any(), eq(Locale.KOREAN));
    }
}
