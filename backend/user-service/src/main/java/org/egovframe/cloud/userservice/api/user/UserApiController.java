package org.egovframe.cloud.userservice.api.user;

import lombok.RequiredArgsConstructor;
import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.common.exception.BusinessMessageException;
import org.egovframe.cloud.common.util.MessageUtil;
import org.egovframe.cloud.userservice.api.user.dto.*;
import org.egovframe.cloud.userservice.config.TokenProvider;
import org.egovframe.cloud.userservice.service.user.UserService;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * org.egovframe.cloud.userservice.api.user.UserApiController
 * <p>
 * 사용자 CRUD 요청을 처리하는 REST API Controller
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/06/30
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/06/30    jaeyeolkim  최초 생성
 * </pre>
 */
@RequiredArgsConstructor // final이 선언된 모든 필드를 인자값으로 하는 생성자를 대신 생성하여, 빈을 생성자로 주입받게 한다.
@RestController
public class UserApiController {
    private final UserService userService;
    private final Environment env;
    private final TokenProvider tokenProvider;

    private final MessageUtil messageUtil;

    /**
     * 유저 서비스 상태 확인
     *
     * @return
     */
    @GetMapping("/actuator/health-info")
    public String status() {
        return String.format("GET User Service on" +
                "\n local.server.port :" + env.getProperty("local.server.port")
                + "\n egov.message :" + env.getProperty("egov.message")
        );
    }

    @PostMapping("/actuator/health-info")
    public String poststatus() {
        return String.format("POST User Service on" +
                "\n local.server.port :" + env.getProperty("local.server.port")
                + "\n egov.message :" + env.getProperty("egov.message")
        );
    }

    /**
     * 사용자 정보 입력
     *
     * @param requestDto
     * @return
     */
    @PostMapping("/api/v1/users")
    @ResponseStatus(HttpStatus.CREATED)
    public Long save(@RequestBody @Valid UserSaveRequestDto requestDto) {
        return userService.save(requestDto);
    }

    /**
     * 사용자 정보 업데이트
     *
     * @param userId
     * @param requestDto
     * @return
     */
    @PutMapping("/api/v1/users/{userId}")
    public String update(@PathVariable String userId, @RequestBody @Valid UserUpdateRequestDto requestDto) {
        return userService.update(userId, requestDto);
    }

    /**
     * 사용자 페이지 목록 조회
     *
     * @param requestDto 요청 DTO
     * @param pageable   페이지 정보
     * @return Page<UserListResponseDto> 페이지 사용자 목록 응답 DTO
     */
    @GetMapping("/api/v1/users")
    public Page<UserListResponseDto> findPage(RequestDto requestDto, Pageable pageable) {
        return userService.findPage(requestDto, pageable);
    }

    /**
     * 사용자 단 건 조회
     *
     * @param userId
     * @return
     */
    @GetMapping("/api/v1/users/{userId}")
    public UserResponseDto findByUserId(@PathVariable String userId) {
        return userService.findByUserId(userId);
    }

    /**
     * refresh token 과 일치하는 사용자가 있으면 access token 을 새로 발급하여 리턴한다.
     *
     * @param request
     * @param response
     * @return
     */
    @PutMapping("/api/v1/users/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        tokenProvider.refreshToken(refreshToken, response);
    }

    /**
     * 사용자 소셜 정보 조회
     *
     * @param requestDto 사용자 가입 요청 DTO
     * @return Boolean 성공 여부
     */
    @PostMapping("/api/v1/users/social")
    public SocialUserResponseDto social(@RequestBody @Valid SocialUserRequestDto requestDto) {
        return userService.getSocialUserInfo(requestDto.getProvider(), requestDto.getToken());
    }

    /**
     * 이메일 중복 확인
     *
     * @param requestDto 사용자 이메일 확인 요청 DTO
     * @return Boolean 중복 여부
     */
    @PostMapping("/api/v1/users/exists")
    public Boolean existsEmail(@RequestBody UserEmailRequestDto requestDto) {
        return userService.existsEmail(requestDto.getEmail(), requestDto.getUserId());
    }

    /**
     * 사용자 회원 가입
     *
     * @param requestDto 사용자 가입 요청 DTO
     * @return Boolean 성공 여부
     */
    @PostMapping("/api/v1/users/join")
    @ResponseStatus(HttpStatus.CREATED)
    public Boolean join(@RequestBody @Valid UserJoinRequestDto requestDto) {
        return userService.join(requestDto);
    }

    /**
     * 사용자 비밀번호 찾기
     *
     * @param requestDto 사용자 비밀번호 찾기 등록 요청 DTO
     * @return Boolean 메일 전송 여부
     */
    @PostMapping("/api/v1/users/password/find")
    public Boolean findPassword(@RequestBody @Valid UserFindPasswordSaveRequestDto requestDto) {
        return userService.findPassword(requestDto);
    }

    /**
     * 사용자 비밀번호 찾기 유효성 확인
     *
     * @param token 토큰
     * @return Boolean 유효 여부
     */
    @GetMapping("/api/v1/users/password/valid/{token}")
    public Boolean validPassword(@PathVariable String token) {
        return userService.validPassword(token);
    }

    /**
     * 사용자 비밀번호 찾기 변경
     *
     * @param requestDto 사용자 비밀번호 수정 요청 DTO
     * @return Boolean 수정 여부
     */
    @PutMapping("/api/v1/users/password/change")
    public Boolean changePassword(@RequestBody @Valid UserFindPasswordUpdateRequestDto requestDto) {
        return userService.changePassword(requestDto);
    }

    /**
     * 사용자 비밀번호 변경
     *
     * @param requestDto 사용자 비밀번호 수정 요청 DTO
     * @return Boolean 수정 여부
     */
    @PutMapping("/api/v1/users/password/update")
    public Boolean updatePassword(@RequestBody @Valid UserPasswordUpdateRequestDto requestDto) {
        final String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        return userService.updatePassword(userId, requestDto);
    }

    /**
     * 사용자 비밀번호 확인
     *
     * @param requestDto 사용자 비밀번호 확인 요청 DTO
     * @return Boolean 일치 여부
     */
    @PostMapping("/api/v1/users/password/match")
    public Boolean matchPassword(@RequestBody @Valid UserPasswordMatchRequestDto requestDto) {
        final String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        final String password = requestDto.getPassword();

        return userService.matchPassword(userId, password);
    }

    /**
     * 사용자 회원정보 변경
     *
     * @param userId     사용자 id
     * @param requestDto 사용자 수정 요청 DTO
     * @return String 사용자 id
     */
    @PutMapping("/api/v1/users/info/{userId}")
    public String updateInfo(@PathVariable String userId, @RequestBody @Valid UserUpdateInfoRequestDto requestDto) throws BusinessMessageException {
        final String authUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!authUserId.equals(userId)) {
            throw new BusinessMessageException(messageUtil.getMessage("err.access.denied"));
        }

        return userService.updateInfo(userId, requestDto);
    }

    /**
     * 사용자 회원탈퇴
     *
     * @param requestDto 사용자 검증 요청 DTO
     * @return Boolean 처리 여부
     */
    @PostMapping("/api/v1/users/leave")
    public Boolean leave(@RequestBody @Valid UserVerifyRequestDto requestDto) {
        final String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        return userService.leave(userId, requestDto);
    }

    /**
     * 사용자 삭제
     *
     * @param userId 사용자 id
     * @return Boolean 처리 여부
     */
    @DeleteMapping("/api/v1/users/delete/{userId}")
    public Boolean delete(@PathVariable String userId) {
        return userService.delete(userId);
    }

}
