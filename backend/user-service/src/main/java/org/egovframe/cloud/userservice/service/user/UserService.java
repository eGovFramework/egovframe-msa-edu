package org.egovframe.cloud.userservice.service.user;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.cloud.common.domain.Role;
import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.common.exception.BusinessException;
import org.egovframe.cloud.common.exception.BusinessMessageException;
import org.egovframe.cloud.common.exception.dto.ErrorCode;
import org.egovframe.cloud.common.service.AbstractService;
import org.egovframe.cloud.common.util.LogUtil;
import org.egovframe.cloud.userservice.api.user.dto.*;
import org.egovframe.cloud.userservice.config.UserPasswordChangeEmailTemplate;
import org.egovframe.cloud.userservice.config.dto.SocialUser;
import org.egovframe.cloud.userservice.domain.log.LoginLog;
import org.egovframe.cloud.userservice.domain.log.LoginLogRepository;
import org.egovframe.cloud.userservice.domain.user.*;
import org.egovframe.cloud.userservice.api.user.dto.SocialUserResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * org.egovframe.cloud.userservice.service.user.UserService
 * <p>
 * 사용자 정보 서비스
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/07/08
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/08    jaeyeolkim  최초 생성
 * </pre>
 */
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService extends AbstractService implements UserDetailsService {

    /**
     * 구글 클라이언트 ID
     */
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String GOOGLE_CLIENT_ID;

    /**
     * 카카오 사용자 정보 URL
     */
    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String KAKAO_USER_INFO_URI;

    /**
     * 네이버 사용자 정보 URL
     */
    @Value("${spring.security.oauth2.client.provider.naver.user-info-uri}")
    private String NAVER_USER_INFO_URI;

    /**
     * REST Template
     */
    private final RestTemplate restTemplate;

    private final UserRepository userRepository;
    private final UserFindPasswordRepository userFindPasswordRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final LoginLogRepository loginLogRepository;

    /**
     * 자바 메일 전송 인터페이스
     */
    private final JavaMailSender javaMailSender;

    /**
     * 조회 조건에 일치하는 사용자 페이지 목록 조회
     *
     * @param requestDto 요청 DTO
     * @param pageable   페이지 정보
     * @return Page<UserListResponseDto> 페이지 사용자 목록 응답 DTO
     */
    public Page<UserListResponseDto> findPage(RequestDto requestDto, Pageable pageable) {
        return userRepository.findPage(requestDto, pageable);
    }

    /**
     * 사용자 등록
     *
     * @param requestDto
     * @return
     */
    @Transactional
    public Long save(UserSaveRequestDto requestDto) {
        return userRepository.save(requestDto.toEntity(passwordEncoder)).getId();
    }

    /**
     * 사용자 수정
     *
     * @param userId     사용자 id
     * @param requestDto 사용자 수정 요청 DTO
     * @return String 사용자 id
     */
    @Transactional
    public String update(String userId, UserUpdateRequestDto requestDto) {
        User user = getUserByUserId(userId);

        final String password = requestDto.getPassword() != null && !"".equals(requestDto.getPassword())
                ? passwordEncoder.encode(requestDto.getPassword())
                : user.getEncryptedPassword();

        user.update(requestDto.getUserName(), requestDto.getEmail(), password,
                requestDto.getRoleId(), requestDto.getUserStateCode());

        return userId;
    }

    /**
     * 사용자 refresh token 정보를 필드에 입력한다
     *
     * @param userId
     * @param updateRefreshToken
     * @return
     */
    @Transactional
    public String updateRefreshToken(String userId, String updateRefreshToken) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException(getMessage("err.user.notexists")));

        user.updateRefreshToken(updateRefreshToken);

        return user.getRoleKey();
    }

    /**
     * 토큰으로 사용자를 찾아 반환한다.
     *
     * @param refreshToken
     * @return
     */
    public User findByRefreshToken(String refreshToken) {
        return userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new UsernameNotFoundException(getMessage("err.user.notexists")));
    }

    /**
     * 아이디로 사용자를 찾아 반환한다.
     *
     * @param userId
     * @return
     */
    public UserResponseDto findByUserId(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException(getMessage("err.user.notexists")));

        return new UserResponseDto(user);
    }

    /**
     * 이메일로 사용자를 찾아 반환한다.
     *
     * @param email
     * @return
     */
    public UserResponseDto findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(getMessage("err.user.notexists")));

        return new UserResponseDto(user);
    }

    /**
     * 모든 사용자를 생성일 역순으로 정렬하여 조회하여 List<UserListResponseDto> 형태로 반환한다.
     *
     * @return
     */
    public List<UserListResponseDto> findAllDesc() {
        return userRepository.findAll(Sort.by(Sort.Direction.DESC, "createdDate")).stream()
                .map(UserListResponseDto::new) // User의 Stream을 map을 통해 UserListResponseDto로 변환한다. 실제로 .map(user -> new UserListResponseDto(user)) 과 같다.
                .collect(Collectors.toList());
    }

    /**
     * SecurityConfig > configure > UserDetailsService 메소드에서 호출된다.
     * 스프링 시큐리티에 의해 로그인 대상 사용자의 패스워드와 권한 정보를 DB에서 조회하여 UserDetails 를 리턴한다.
     *
     * @param email
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("loadUserByUsername! email={}", email);
        // 로그인 실패시 이메일 계정을 로그에 남기기 위해 세팅하고 unsuccessfulAuthentication 메소드에서 받아서 로그에 입력한다.
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        request.setAttribute("email", email);

        // UsernameNotFoundException 을 던지면 AbstractUserDetailsAuthenticationProvider 에서 BadCredentialsException 으로 처리하기 때문에 IllegalArgumentException 을 발생시켰다.
        // 사용자가 없는 것인지 패스워드가 잘못된 것인지 구분하기 위함이다.
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(getMessage("err.user.notexists")));
        log.info("{} 사용자 존재함", user);

        if (!UserStateCode.NORMAL.getKey().equals(user.getUserStateCode())) {
            throw new IllegalArgumentException(getMessage("err.user.state.cantlogin"));
        }

        // 로그인 유저의 권한 목록 주입
        ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRoleKey()));

        if (user.isSocialUser() && user.getEncryptedPassword() == null || "".equals(user.getEncryptedPassword())) { // 소셜 회원이고 비밀번호가 등록되지 않은 경우
            return new SocialUser(user.getEmail(), authorities);
        } else {
            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getEncryptedPassword(), authorities);
        }
    }

    /**
     * 로그인 후처리
     *
     * @param siteId      사이트 id
     * @param email       이메일
     * @param successAt   성공 여부
     * @param failContent 실패 내용
     */
    @Transactional
    public void loginCallback(Long siteId, String email, Boolean successAt, String failContent) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(getMessage("err.user.notexists")));

        if (Boolean.TRUE.equals(successAt)) {
            user.successLogin();
        } else {
            user.failLogin();
        }

        // 로그인 로그 입력
        loginLogRepository.save(
                LoginLog.builder()
                        .siteId(siteId)
                        .email(email)
                        .remoteIp(LogUtil.getUserIp())
                        .successAt(successAt)
                        .failContent(failContent)
                        .build()
        );
    }

    /**
     * 이메일 중복 확인
     *
     * @param email  이메일
     * @param userId 사용자 id
     * @return Boolean 중복 여부
     */
    public Boolean existsEmail(String email, String userId) {
        if (email == null || "".equals(email)) {
            throw new BusinessMessageException(getMessage("valid.required.format", new Object[]{getMessage("user.email")}));
        }

        if (userId == null || "".equals(userId)) {
            return userRepository.findByEmail(email).isPresent();
        } else {
            return userRepository.findByEmailAndUserIdNot(email, userId).isPresent();
        }
    }

    /**
     * 사용자 회원 가입
     *
     * @param requestDto 사용자 가입 요청 DTO
     * @return Boolean 성공 여부
     */
    @Transactional
    public Boolean join(UserJoinRequestDto requestDto) {
        boolean exists = existsEmail(requestDto.getEmail(), null);
        if (exists) {
            throw new BusinessMessageException(getMessage("msg.join.email.exists"));
        }

        User user = requestDto.toEntity(passwordEncoder);

        if (requestDto.isProvider()) {
            SocialUserResponseDto socialUserResponseDto = getSocialUserInfo(requestDto.getProvider(), requestDto.getToken());
            user.setSocial(requestDto.getProvider(), socialUserResponseDto.getId());
        }

        userRepository.save(user);

        return true;
    }


    /**
     * 사용자 비밀번호 찾기
     *
     * @param requestDto 사용자 비밀번호 찾기 등록 요청 DTO
     * @return Boolean 메일 전송 여부
     */
    @Transactional
    public Boolean findPassword(UserFindPasswordSaveRequestDto requestDto) {
        final String emailAddr = requestDto.getEmailAddr();

        Optional<User> user = userRepository.findByEmailAndUserName(emailAddr, requestDto.getUserName());
        if (!user.isPresent()) {
            throw new BusinessMessageException(getMessage("err.user.notexists"));
        }
        User entity = user.get();

        // 이메일 전송
        try {
            final String mainUrl = requestDto.getMainUrl();
            final String tokenValue = UUID.randomUUID().toString().replaceAll("-", "");

            final String subject = getMessage("email.user.password.title");
            //final String text = getMessage("email.user.password.content"); // varchar(2000)
            final String text = UserPasswordChangeEmailTemplate.html;
            final String userName = entity.getUserName();
            final String changePasswordUrl = requestDto.getChangePasswordUrl() + "?token=" + tokenValue;

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setTo(emailAddr);
            helper.setSubject(subject);
            helper.setText(String.format(text, mainUrl, userName, changePasswordUrl), true); // String.format에서 %를 쓰려면 %%로

            log.info("start send change password email: " + emailAddr);
            javaMailSender.send(message);

            Integer requestNo = userFindPasswordRepository.findNextRequestNo(emailAddr);
            UserFindPassword userFindPassword = requestDto.toEntity(requestNo, tokenValue);

            userFindPasswordRepository.save(userFindPassword);

            log.info("end send change password email - emailAddr: " + emailAddr + ", tokenValue: " + tokenValue);
        } catch (MessagingException e) {
            String errorMessage = getMessage("err.user.find.password");
            log.error(errorMessage + ": " + e.getMessage());
            throw new BusinessMessageException(errorMessage);
        } catch (Exception e) {
            String errorMessage = getMessage("err.user.find.password");
            log.error(errorMessage + ": " + e.getMessage());
            throw new BusinessMessageException(errorMessage);
        }

        return true;
    }

    /**
     * 사용자 비밀번호 찾기 유효성 확인
     *
     * @param tokenValue 토큰 값
     * @return Boolean 유효 여부
     */
    @Transactional
    public Boolean validPassword(String tokenValue) {
        if (tokenValue == null || "".equals(tokenValue)) {
            throw new BusinessMessageException(getMessage("err.invalid.input.value"));
        }

        Optional<UserFindPassword> userPassword = userFindPasswordRepository.findByTokenValue(tokenValue);
        if (userPassword.isPresent()) {
            UserFindPassword entity = userPassword.get();

            boolean isExpired = LocalDateTime.now().isAfter(entity.getCreatedDate().plusHours(1)); // 1시간 후 만료
            if (Boolean.FALSE.equals(entity.getChangeAt()) && !isExpired) return true;
        }

        return false;
    }

    /**
     * 사용자 비밀번호 찾기 변경
     *
     * @param requestDto 사용자 비밀번호 수정 요청 DTO
     * @return Boolean 수정 여부
     */
    @Transactional
    public Boolean changePassword(UserFindPasswordUpdateRequestDto requestDto) {
        final String tokenValue = requestDto.getTokenValue();

        Optional<UserFindPassword> userPassword = userFindPasswordRepository.findByTokenValue(tokenValue);

        if (!userPassword.isPresent()) {
            throw new BusinessMessageException(getMessage("err.user.change.password"));
        }

        UserFindPassword entity = userPassword.get();
        if (Boolean.TRUE.equals(entity.getChangeAt()) || LocalDateTime.now().isAfter(entity.getCreatedDate().plusHours(1))) { // 1시간 후 만료
            throw new BusinessMessageException(getMessage("err.user.change.password"));
        }

        User user = userRepository.findByEmail(entity.getUserFindPasswordId().getEmailAddr())
                .orElseThrow(() -> new UsernameNotFoundException(getMessage("err.user.notexists")));

        user.updatePassword(passwordEncoder.encode(requestDto.getPassword())); // 비밀번호 수정

        entity.updateChangeAt(Boolean.TRUE); // 변경 완료

        return true;
    }

    /**
     * 사용자 비밀번호 변경
     *
     * @param userId     사용자 id
     * @param requestDto 사용자 비밀번호 변경 요청 DTO
     * @return Boolean 수정 여부
     */
    @Transactional
    public Boolean updatePassword(String userId, UserPasswordUpdateRequestDto requestDto) {
        try {
            User entity = findUserVerify(userId, requestDto);

            entity.updatePassword(passwordEncoder.encode(requestDto.getNewPassword())); // 비밀번호 수정
        } catch (IllegalArgumentException e) {
            log.error(e.getLocalizedMessage());
            throw e;
        }

        return true;
    }

    /**
     * 사용자 비밀번호 확인
     *
     * @param userId   사용자 id
     * @param password 비밀번호
     * @return Boolean 일치 여부
     */
    public Boolean matchPassword(String userId, String password) {
        try {
            findUserVerifyPassword(userId, password);
        } catch (BusinessMessageException e) {
            return false;
        }

        return true;
    }

    /**
     * 사용자 id로 조회
     *
     * @param userId 사용자 id
     * @return User 사용자 엔티티
     */
    private User getUserByUserId(String userId) {
        Optional<User> user = userRepository.findByUserId(userId);
        if (!user.isPresent()) {
            throw new BusinessMessageException(getMessage("err.user.notexists"));
        }

        return user.get();
    }

    /**
     * 사용자 조회, 비밀번호 검증
     *
     * @param userId   사용자 id
     * @param password 비밀번호
     * @return User 사용자 엔티티
     */
    private User findUserVerifyPassword(String userId, String password) {
        User entity = getUserByUserId(userId);

        if (!passwordEncoder.matches(password, entity.getEncryptedPassword())) { // 소셜 사용자가 아닌 경우 비밀번호 확인
            throw new BusinessMessageException(getMessage("err.user.password.notmatch"));
        }

        return entity;
    }

    /**
     * 사용자 정보 수정
     *
     * @param userId     사용자 id
     * @param requestDto 사용자 정보 수정 요청 DTO
     * @return String 사용자 id
     */
    @Transactional
    public String updateInfo(String userId, UserUpdateInfoRequestDto requestDto) {
        User user = findUserVerify(userId, requestDto);

        user.updateInfo(requestDto.getUserName(), requestDto.getEmail());

        return userId;
    }

    /**
     * 사용자 회원탈퇴
     *
     * @param userId     사용자 id
     * @param requestDto 회원 탈퇴 요청 DTO
     * @return User 사용자 엔티티
     */
    @Transactional
    public Boolean leave(String userId, UserVerifyRequestDto requestDto) {
        User entity = findUserVerify(userId, requestDto);

        entity.updateUserStateCode(UserStateCode.LEAVE.getKey());

        return true;
    }

    /**
     * 사용자 검증 및 조회
     *
     * @param userId     사용자 id
     * @param requestDto 회원 탈퇴 요청 DTO
     * @return User 사용자 엔티티
     */
    private User findUserVerify(String userId, UserVerifyRequestDto requestDto) {
        if (userId == null || "".equals(userId)) {
            throw new BusinessMessageException(getMessage("err.required.login"));
        }

        User user = null;

        if ("password".equals(requestDto.getProvider())) {
            user = findUserVerifyPassword(userId, requestDto.getPassword());
        } else {
            user = findSocialUserByToken(requestDto.getProvider(), requestDto.getToken());

            if (user == null) {
                throw new BusinessMessageException(getMessage("err.user.socail.find"));
            }
            if (!userId.equals(user.getUserId())) {
                throw new BusinessMessageException(getMessage("err.unauthorized"));
            }
        }

        return user;
    }

    /**
     * 사용자 삭제
     *
     * @param userId 사용자 id
     * @return User 사용자 엔티티
     */
    @Transactional
    public Boolean delete(String userId) {
        User user = getUserByUserId(userId);

        user.updateUserStateCode(UserStateCode.DELETE.getKey());

        return true;
    }

    /**
     * OAuth 사용자 검색
     *
     * @param requestDto 사용자 로그인 요청 DTO
     * @return UserLoginRequestDto 사용자 로그인 요청 DTO
     */
    @Transactional
    public UserResponseDto loadUserBySocial(UserLoginRequestDto requestDto) {
        SocialUserResponseDto socialUserResponseDto = getSocialUserInfo(requestDto.getProvider(), requestDto.getToken());

        User user = findSocialUser(requestDto.getProvider(), socialUserResponseDto.getId());
        if (user == null) {
            throw new BusinessException(ErrorCode.REQUIRE_USER_JOIN);
        }
        if (!UserStateCode.NORMAL.getKey().equals(user.getUserStateCode())) {
            throw new BusinessMessageException(getMessage("err.user.state.cantlogin"));
        }

        return new UserResponseDto(user);
    }

    /**
     * 토큰으로 사용자 엔티티 조회
     *
     * @param provider 공급자
     * @param token    토큰
     * @return User 사용자 엔티티
     */
    private User findSocialUserByToken(String provider, String token) {
        SocialUserResponseDto socialUserResponseDto = getSocialUserInfo(provider, token);

        return findSocialUser(provider, socialUserResponseDto.getId());
    }

    /**
     * 토큰으로 소셜 사용자 정보 조회
     *
     * @param provider 공급자
     * @param token    토큰
     * @return String[] 소셜 사용자 정보
     */
    public SocialUserResponseDto getSocialUserInfo(String provider, String token) {
        SocialUserResponseDto social = null;

        switch (provider) {
            case "google":
                social = getGoogleUserInfo(token);
                break;
            case "naver":
                social = getNaverUserInfo(token);
                break;
            case "kakao":
                social = getKakaoUserInfo(token);
                break;
            default:
                break;
        }

        if (social == null) throw new BusinessMessageException(getMessage("err.user.social.get"));

        return social;
    }

    /**
     * 구글 사용자 정보 조회
     *
     * @param token 토큰
     * @return String[] 구글 사용자 정보
     */
    private SocialUserResponseDto getGoogleUserInfo(String token) {
        try {
            HttpTransport transport = new NetHttpTransport();
            GsonFactory gsonFactory = new GsonFactory();

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, gsonFactory)
                    .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify(token);

            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                log.info("google oauth2: {}", payload.toString());

                return SocialUserResponseDto.builder()
                        .id(payload.getSubject())
                        .email(payload.getEmail())
                        .name((String) payload.get("name"))
                        .build();
            }

            return null;
        } catch (GeneralSecurityException e) {
            throw new BusinessMessageException(getMessage("err.user.social.get"));
        } catch (IOException e) {
            throw new BusinessMessageException(getMessage("err.user.social.get"));
        } catch (Exception e) {
            throw new BusinessMessageException(getMessage("err.user.social.get"));
        }
    }

    /**
     * 네이버 사용자 정보 조회
     *
     * @param token 토큰
     * @return String[] 네이버 사용자 정보
     */
    private SocialUserResponseDto getNaverUserInfo(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.exchange(NAVER_USER_INFO_URI, HttpMethod.GET, request, String.class);

        if (response.getBody() != null && !"".equals(response.getBody())) {
            JsonElement element = JsonParser.parseString(response.getBody());
            JsonObject object = element.getAsJsonObject();
            log.info("naver oauth2: {}", object);

            if (object.get("resultcode") != null && "00".equals(object.get("resultcode").getAsString())) {
                JsonElement responseElement = object.get("response");

                if (responseElement != null) {
                    SocialUserResponseDto.SocialUserResponseDtoBuilder builder = SocialUserResponseDto.builder();

                    if (responseElement.getAsJsonObject().get("id") != null && !"".equals(responseElement.getAsJsonObject().get("id").getAsString())) {
                        builder.id(responseElement.getAsJsonObject().get("id").getAsString());
                    }
                    if (responseElement.getAsJsonObject().get("email") != null && !"".equals(responseElement.getAsJsonObject().get("email").getAsString())) {
                        builder.email(responseElement.getAsJsonObject().get("email").getAsString());
                    }
                    if (responseElement.getAsJsonObject().get("name") != null && !"".equals(responseElement.getAsJsonObject().get("name").getAsString())) {
                        builder.name(responseElement.getAsJsonObject().get("name").getAsString());
                    }

                    return builder.build();
                }
            }
        }

        return null;
    }

    /**
     * 카카오 사용자 정보 조회
     *
     * @param token 토큰
     * @return String[] 카카오 사용자 정보
     */
    private SocialUserResponseDto getKakaoUserInfo(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.exchange(KAKAO_USER_INFO_URI, HttpMethod.GET, request, String.class);

        if (response.getBody() != null && !"".equals(response.getBody())) {
            JsonElement element = JsonParser.parseString(response.getBody());
            JsonObject object = element.getAsJsonObject();
            JsonElement kakaoAccount = object.get("kakao_account");
            log.info("kakao oauth2: {}", object.toString());

            String id = object.get("id") != null && !"".equals(object.get("id").getAsString()) ? object.get("id").getAsString() : null;

            if (id != null) {
                SocialUserResponseDto.SocialUserResponseDtoBuilder builder = SocialUserResponseDto.builder()
                        .id(id);

                if (kakaoAccount.getAsJsonObject().get("email") != null && !"".equals(kakaoAccount.getAsJsonObject().get("email").getAsString())) {
                    builder.email(kakaoAccount.getAsJsonObject().get("email").getAsString());
                }
                JsonElement profile = kakaoAccount.getAsJsonObject().get("profile");
                if (profile != null) {
                    if (profile.getAsJsonObject().get("nickname") != null && !"".equals(profile.getAsJsonObject().get("nickname").getAsString())) {
                        builder.name(profile.getAsJsonObject().get("nickname").getAsString());
                    }
                }

                return builder.build();
            }
        }

        return null;
    }

    /**
     * 소셜 사용자 엔티티 조회
     *
     * @param providerCode 공급자 코드
     * @param providerId   공급자 id
     * @return User 사용자 엔티티
     */
    private User findSocialUser(String providerCode, String providerId) {
        Optional<User> user;

        // 공급자 id로 조회
        switch (providerCode) {
            case "google":
                user = userRepository.findByGoogleId(providerId);
                break;
            case "kakao":
                user = userRepository.findByKakaoId(providerId);
                break;
            case "naver":
                user = userRepository.findByNaverId(providerId);
                break;
            default:
                user = Optional.empty();
                break;
        }

        return user.orElse(null);
    }

    /**
     * 소셜 사용자 엔티티 조회
     * 등록되어 있지 않은 경우 사용자 등록
     *
     * @param providerCode 공급자 코드
     * @param providerId   공급자 id
     * @param email        이메일
     * @param userName     사용자 명
     * @return UserLoginRequestDto 사용자 로그인 요청 DTO
     */
    private UserResponseDto getAndSaveSocialUser(String providerCode, String providerId, String email, String userName) {
        User user = findSocialUser(providerCode, providerId);

        // 이메일로 조회
        // 공급자에서 동일한 이메일을 사용할 수 있고
        // 현재 시스템 구조 상 이메일을 사용자 식별키로 사용하고 있어서 이메일로 사용자를 한번 더 검색한다.
        if (user == null) {
            user = userRepository.findByEmail(email).orElse(null);

            // 공급자 id로 조회되지 않지만 이메일로 조회되는 경우 공급자 id 등록
            if (user != null) {
                user.setSocial(providerCode, providerId);
            }
        }

        if (user == null) {
            // 사용자 등록
            final String userId = UUID.randomUUID().toString();
            //final String password = makeRandomPassword(); // 임의 비밀번호 생성 시 복호화 불가능

            user = User.builder()
                    .email(email) // 100byte
                    //.encryptedPassword(passwordEncoder.encode(password)) // 100 byte
                    .userName(userName)
                    .userId(userId)
                    .role(Role.USER)
                    .userStateCode(UserStateCode.NORMAL.getKey())
                    .build();
            user.setSocial(providerCode, providerId);

            user = userRepository.save(user);

        }

        return new UserResponseDto(user);
    }

}