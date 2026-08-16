package org.egovframe.cloud.userservice.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.egovframe.cloud.common.domain.Role;
import org.egovframe.cloud.common.util.MessageUtil;
import org.egovframe.cloud.userservice.api.user.dto.UserFindPasswordSaveRequestDto;
import org.egovframe.cloud.userservice.domain.user.User;
import org.egovframe.cloud.userservice.domain.user.UserFindPassword;
import org.egovframe.cloud.userservice.domain.user.UserFindPasswordRepository;
import org.egovframe.cloud.userservice.domain.user.UserRepository;
import org.egovframe.cloud.userservice.domain.user.UserStateCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.util.ReflectionTestUtils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import jakarta.mail.internet.MimeMessage;

/**
 * org.egovframe.cloud.userservice.service.user.UserServiceFindPasswordLogTest
 * <p>
 * Verifies that UserService.findPassword does not expose the password reset token
 * to the logs (CWE-532).
 * <p>
 * The reset token (tokenValue) is carried in changePasswordUrl(...?token=), emailed to
 * the user and persisted in UserFindPassword. It is the secret that authorizes a
 * password reset, so anyone able to read the log file could reset an arbitrary user's
 * password. Therefore no log event may contain the raw token.
 *
 * @author EricSeokgon
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class UserServiceFindPasswordLogTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserFindPasswordRepository userFindPasswordRepository;

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private MessageUtil messageUtil;

    @InjectMocks
    private UserService userService;

    private Logger logger;
    private ListAppender<ILoggingEvent> appender;

    @BeforeEach
    void setUp() {
        // messageUtil is a @Resource field on AbstractService (not a constructor arg),
        // so inject it directly.
        ReflectionTestUtils.setField(userService, "messageUtil", messageUtil);

        logger = (Logger) LoggerFactory.getLogger(UserService.class);
        logger.setLevel(Level.DEBUG); // ensure INFO events are emitted
        appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(appender);
    }

    private UserFindPasswordSaveRequestDto requestDto(String email, String userName) {
        UserFindPasswordSaveRequestDto dto = new UserFindPasswordSaveRequestDto();
        ReflectionTestUtils.setField(dto, "userName", userName);
        ReflectionTestUtils.setField(dto, "emailAddr", email);
        ReflectionTestUtils.setField(dto, "mainUrl", "https://portal.example.org");
        ReflectionTestUtils.setField(dto, "changePasswordUrl", "https://portal.example.org/change");
        return dto;
    }

    private User user(String email, String userName) {
        return User.builder()
                .userId("uid-" + email)
                .userName(userName)
                .email(email)
                .encryptedPassword("")
                .role(Role.USER)
                .userStateCode(UserStateCode.NORMAL.getKey())
                .build();
    }

    @Test
    @DisplayName("findPassword must not log the reset token (CWE-532)")
    void findPassword_doesNotLogResetToken() {
        // given
        String email = "reset-target@example.com";
        String userName = "tester";
        when(userRepository.findByEmailAndUserName(email, userName)).thenReturn(Optional.of(user(email, userName)));
        when(messageUtil.getMessage("email.user.password.title")).thenReturn("Password reset");
        when(javaMailSender.createMimeMessage()).thenReturn(realMimeMessage());
        when(userFindPasswordRepository.findNextRequestNo(email)).thenReturn(1);

        // when
        userService.findPassword(requestDto(email, userName));

        // then: capture the token actually issued by the flow.
        ArgumentCaptor<UserFindPassword> captor = ArgumentCaptor.forClass(UserFindPassword.class);
        verify(userFindPasswordRepository).save(captor.capture());
        String issuedToken = captor.getValue().getTokenValue();

        assertThat(issuedToken)
                .as("precondition: the reset flow must complete and issue a token")
                .isNotBlank();

        boolean tokenLeaked = appender.list.stream()
                .map(ILoggingEvent::getFormattedMessage)
                .anyMatch(msg -> msg.contains(issuedToken));

        assertThat(tokenLeaked)
                .as("reset token must not appear in logs (CWE-532). Captured logs: %s",
                        appender.list.stream().map(ILoggingEvent::getFormattedMessage).toList())
                .isFalse();
    }

    @Test
    @DisplayName("findPassword still logs a completion marker (token omitted)")
    void findPassword_stillLogsCompletionMarker() {
        // given
        String email = "reset-target@example.com";
        String userName = "tester";
        when(userRepository.findByEmailAndUserName(email, userName)).thenReturn(Optional.of(user(email, userName)));
        when(messageUtil.getMessage("email.user.password.title")).thenReturn("Password reset");
        when(javaMailSender.createMimeMessage()).thenReturn(realMimeMessage());
        when(userFindPasswordRepository.findNextRequestNo(email)).thenReturn(1);

        // when
        userService.findPassword(requestDto(email, userName));

        // then: the completion log line itself must remain for operational tracing.
        boolean hasCompletionLog = appender.list.stream()
                .map(ILoggingEvent::getFormattedMessage)
                .anyMatch(msg -> msg.contains("end send change password email"));

        assertThat(hasCompletionLog)
                .as("completion marker log should be preserved")
                .isTrue();
    }

    private MimeMessage realMimeMessage() {
        return new JavaMailSenderImpl().createMimeMessage();
    }
}
