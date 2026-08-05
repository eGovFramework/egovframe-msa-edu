package org.egovframe.cloud.userservice.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.egovframe.cloud.common.domain.Role;
import org.egovframe.cloud.userservice.config.dto.SocialUser;
import org.egovframe.cloud.userservice.domain.user.User;
import org.egovframe.cloud.userservice.domain.user.UserRepository;
import org.egovframe.cloud.userservice.domain.user.UserStateCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * org.egovframe.cloud.userservice.service.user.UserServiceTest
 * <p>
 * UserService.loadUserByUsername 의 소셜 회원 판별 분기 단위 테스트
 *
 * @author 표준프레임워크센터
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        // loadUserByUsername 내부에서 RequestContextHolder 를 사용하므로 요청 컨텍스트를 세팅한다.
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    /**
     * 테스트용 사용자 엔티티 생성
     */
    private User user(String email, String encryptedPassword, String socialProvider, String socialId) {
        User.UserBuilder builder = User.builder()
                .userId("uid-" + email)
                .userName("tester")
                .email(email)
                .encryptedPassword(encryptedPassword)
                .role(Role.USER)
                .userStateCode(UserStateCode.NORMAL.getKey());
        if ("google".equals(socialProvider)) builder.googleId(socialId);
        else if ("kakao".equals(socialProvider)) builder.kakaoId(socialId);
        else if ("naver".equals(socialProvider)) builder.naverId(socialId);
        return builder.build();
    }

    @Test
    @DisplayName("비-소셜 회원이고 비밀번호가 빈 문자열이면 일반 User 로 반환된다")
    void loadUserByUsername_nonSocialEmptyPassword_returnsGeneralUser() {
        // given
        String email = "nonsocial@egovframe.org";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user(email, "", null, null)));

        // when
        UserDetails userDetails = userService.loadUserByUsername(email);

        // then : 연산자 우선순위 버그가 있으면 SocialUser 로 새어나간다. 일반 User 여야 한다.
        assertThat(userDetails).isNotInstanceOf(SocialUser.class);
        assertThat(userDetails).isInstanceOf(org.springframework.security.core.userdetails.User.class);
        assertThat(userDetails.getUsername()).isEqualTo(email);
    }

    @Test
    @DisplayName("소셜 회원이고 비밀번호가 null 이면 SocialUser 로 반환된다")
    void loadUserByUsername_socialNullPassword_returnsSocialUser() {
        // given
        String email = "social-null@egovframe.org";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user(email, null, "google", "g-123")));

        // when
        UserDetails userDetails = userService.loadUserByUsername(email);

        // then
        assertThat(userDetails).isInstanceOf(SocialUser.class);
        assertThat(userDetails.getUsername()).isEqualTo(email);
    }

    @Test
    @DisplayName("소셜 회원이고 비밀번호가 빈 문자열이면 SocialUser 로 반환된다")
    void loadUserByUsername_socialEmptyPassword_returnsSocialUser() {
        // given
        String email = "social-empty@egovframe.org";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user(email, "", "naver", "n-123")));

        // when
        UserDetails userDetails = userService.loadUserByUsername(email);

        // then
        assertThat(userDetails).isInstanceOf(SocialUser.class);
    }

    @Test
    @DisplayName("일반 회원이고 정상 비밀번호가 있으면 일반 User 로 반환된다")
    void loadUserByUsername_generalUserWithPassword_returnsGeneralUser() {
        // given
        String email = "general@egovframe.org";
        String encryptedPassword = "$2a$10$encryptedpasswordsample";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user(email, encryptedPassword, null, null)));

        // when
        UserDetails userDetails = userService.loadUserByUsername(email);

        // then
        assertThat(userDetails).isNotInstanceOf(SocialUser.class);
        assertThat(userDetails).isInstanceOf(org.springframework.security.core.userdetails.User.class);
        assertThat(userDetails.getPassword()).isEqualTo(encryptedPassword);
    }
}
