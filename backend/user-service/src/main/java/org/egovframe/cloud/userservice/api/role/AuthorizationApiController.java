package org.egovframe.cloud.userservice.api.role;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.userservice.api.role.dto.AuthorizationListResponseDto;
import org.egovframe.cloud.userservice.api.role.dto.AuthorizationResponseDto;
import org.egovframe.cloud.userservice.api.role.dto.AuthorizationSaveRequestDto;
import org.egovframe.cloud.userservice.api.role.dto.AuthorizationUpdateRequestDto;
import org.egovframe.cloud.userservice.service.role.AuthorizationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * org.egovframe.cloud.userservice.api.role.AuthorizationApiController
 * <p>
 * API Gateway 의 RestApiAuthorization.check 메소드에 의해 호출된다.
 * 요청 url에 대한 사용자 인가 서비스를 수행하는 클래스
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/07/19
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/19    jaeyeolkim  최초 생성
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class AuthorizationApiController {

    /**
     * 인가 서비스
     */
    private final AuthorizationService authorizationService;

    /**
     * 인가 여부 확인
     *
     * @param httpMethod  Http Method
     * @param requestPath 요청 경로
     * @return Boolean 인가 여부
     */
    @GetMapping("/api/v1/authorizations/check")
    public Boolean isAuthorization(@RequestParam("httpMethod") String httpMethod, @RequestParam("requestPath") String requestPath) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userId = authentication.getName();
        List<String> roles = authentication.getAuthorities().stream().map(GrantedAuthority::toString).collect(Collectors.toList());

        // 사용자 아이디로 조회
        // return authorizationService.isAuthorization(userId, httpMethod, requestPath);

        // 권한으로 조회
        Boolean isAuth = authorizationService.isAuthorization(roles, httpMethod, requestPath);

        log.info("[isAuthorization={}] authentication.isAuthenticated()={}, userId={}, httpMethod={}, requestPath={}, roleList={}", isAuth, authentication.isAuthenticated(), userId, httpMethod, requestPath, roles);

        return isAuth;
    }

    /**
     * 인가 페이지 목록 조회
     *
     * @param requestDto 요청 DTO
     * @param pageable   페이지 정보
     * @return Page<AuthorizationListResponseDto> 페이지 인가 목록 응답 DTO
     */
    @GetMapping("/api/v1/authorizations")
    public Page<AuthorizationListResponseDto> findPage(RequestDto requestDto,
                                                       @PageableDefault(sort = "sort_seq", direction = Sort.Direction.ASC) Pageable pageable) {
        return authorizationService.findPage(requestDto, pageable);
    }

    /**
     * 인가 단건 조회
     *
     * @param authorizationNo 인가 번호
     * @return AuthorizationResponseDto 인가 상세 응답 DTO
     */
    @GetMapping("/api/v1/authorizations/{authorizationNo}")
    public AuthorizationResponseDto findById(@PathVariable Integer authorizationNo) {
        return authorizationService.findById(authorizationNo);
    }

    /**
     * 인가 다음 정렬 순서 조회
     *
     * @return Integer 다음 정렬 순서
     */
    @GetMapping("/api/v1/authorizations/sort-seq/next")
    public Integer findNextSortSeq() {
        return authorizationService.findNextSortSeq();
    }

    /**
     * 인가 등록
     *
     * @param requestDto 인가 등록 요청 DTO
     * @return AuthorizationResponseDto 인가 상세 응답 DTO
     */
    @PostMapping("/api/v1/authorizations")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorizationResponseDto save(@RequestBody @Valid AuthorizationSaveRequestDto requestDto) {
        return authorizationService.save(requestDto);
    }

    /**
     * 인가 수정
     *
     * @param authorizationNo 인가 번호
     * @param requestDto      인가 수정 요청 DTO
     * @return AuthorizationResponseDto 인가 상세 응답 DTO
     */
    @PutMapping("/api/v1/authorizations/{authorizationNo}")
    public AuthorizationResponseDto update(@PathVariable Integer authorizationNo, @RequestBody @Valid AuthorizationUpdateRequestDto requestDto) {
        return authorizationService.update(authorizationNo, requestDto);
    }

    /**
     * 인가 삭제
     *
     * @param authorizationNo 인가 번호
     */
    @DeleteMapping("/api/v1/authorizations/{authorizationNo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer authorizationNo) {
        authorizationService.delete(authorizationNo);
    }

}
