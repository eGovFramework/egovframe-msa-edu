package org.egovframe.cloud.userservice.service.role;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.cloud.common.config.GlobalConstant;
import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.common.exception.EntityNotFoundException;
import org.egovframe.cloud.common.service.AbstractService;
import org.egovframe.cloud.userservice.api.role.dto.AuthorizationListResponseDto;
import org.egovframe.cloud.userservice.api.role.dto.AuthorizationResponseDto;
import org.egovframe.cloud.userservice.api.role.dto.AuthorizationSaveRequestDto;
import org.egovframe.cloud.userservice.api.role.dto.AuthorizationUpdateRequestDto;
import org.egovframe.cloud.userservice.domain.role.Authorization;
import org.egovframe.cloud.userservice.domain.role.AuthorizationRepository;
import org.springframework.aop.framework.AopContext;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * org.egovframe.cloud.userservice.service.role.AuthorizationService
 * <p>
 * 인가 서비스 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/08
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/08    jooho       최초 생성
 * </pre>
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
@Slf4j
public class AuthorizationService extends AbstractService {

    /**
     * 인가 레파지토리 인터페이스
     */
    private final AuthorizationRepository authorizationRepository;

    /**
     * 캐시 관리자
     */
    private final CacheManager cacheManager;

    /**
     * 조회 조건에 일치하는 인가 페이지 목록 조회
     *
     * @param requestDto 요청 DTO
     * @param pageable   페이지 정보
     * @return Page<AuthorizationListResponseDto> 페이지 인가 목록 응답 DTO
     */
    public Page<AuthorizationListResponseDto> findPage(RequestDto requestDto, Pageable pageable) {
        return authorizationRepository.findPage(requestDto, pageable);
    }

    /**
     * 권한의 인가 여부 확인
     * 사용자 서비스 시큐리티 필터에서 호출
     *
     * @param request        http 요청
     * @param authentication 시큐리티 인증 토큰
     * @return Boolean 인가 여부
     */
    public Boolean isAuthorization(HttpServletRequest request, Authentication authentication) {
        List<String> roles = authentication.getAuthorities().stream().map(GrantedAuthority::toString).collect(Collectors.toList());

        List<AuthorizationListResponseDto> authorizationList = ((AuthorizationService) AopContext.currentProxy()).findByRoles(roles);

        return isContainMatch(authorizationList, request.getMethod(), GlobalConstant.USER_SERVICE_URI + request.getRequestURI());
    }

    /**
     * 권한의 인가 전체 목록 조회
     *
     * @param roles 권한 목록
     * @return List<AuthorizationListResponseDto> 인가 목록
     */
    @Cacheable(value = "cache-user-authorization-by-roles", key = "#roles")
    public List<AuthorizationListResponseDto> findByRoles(List<String> roles) {
        return authorizationRepository.findByRoles(roles);
    }

    /**
     * 권한의 인가 여부 확인
     * gateway 에서 호출
     * <p>
     * Spring Cache는 Spring AOP를 이용해서 proxy로 동작하기 때문에 외부 method 호출만 인터셉트해서 작동하고 self-invocation의 경우 동작하지 않음
     * 스프링에서는 AspectJ를 권장하지만 Load-time Weaving 방식은 퍼포먼스 문제가 있고
     * Compile-time Weaving 방식은 컴파일 시 수행되는 라이브러리(lombok)와 충돌 문제가 있음
     * AopContext.currentProxy()를 이용해서 proxy로 호출하도록 함 - CacheConfig @EnableAspectJAutoProxy(exposeProxy=true)
     *
     * @param roles       권한 목록
     * @param httpMethod  Http Method
     * @param requestPath 요청 경로
     * @return Boolean 인가 여부
     */
    public Boolean isAuthorization(List<String> roles, String httpMethod, String requestPath) {
        List<AuthorizationListResponseDto> authorizationList = ((AuthorizationService) AopContext.currentProxy()).findByRoles(roles);

        return isContainMatch(authorizationList, httpMethod, requestPath);
    }

    /**
     * 사용자의 인가 전체 목록 조회
     *
     * @param userId 사용자 id
     * @return List<AuthorizationListResponseDto> 인가 목록
     */
    @Cacheable(value = "cache-user-authorization-by-userid", key = "#roles")
    public List<AuthorizationListResponseDto> findByUserId(String userId) {
        return authorizationRepository.findByUserId(userId);
    }

    /**
     * 사용자의 인가 여부 확인
     * gateway 에서 호출
     *
     * @param userId      사용자 id
     * @param httpMethod  Http Method
     * @param requestPath 요청 경로
     * @return Boolean 인가 여부
     */
    public Boolean isAuthorization(String userId, String httpMethod, String requestPath) {
        List<AuthorizationListResponseDto> authorizationList = ((AuthorizationService) AopContext.currentProxy()).findByUserId(userId);

        return isContainMatch(authorizationList, httpMethod, requestPath);
    }

    /**
     * 인가 여부 체크
     *
     * @param authorizationList 인가 목록
     * @param httpMethod        Http Method
     * @param requestPath       요청 경로
     * @return Boolean 인가 여부
     */
    private Boolean isContainMatch(List<AuthorizationListResponseDto> authorizationList, String httpMethod, String requestPath) {
        AntPathMatcher antPathMatcher = new AntPathMatcher();

        for (AuthorizationListResponseDto dto : authorizationList) {
            if (antPathMatcher.match(dto.getUrlPatternValue(), requestPath) && dto.getHttpMethodCode().equals(httpMethod)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 인가 단건 조회
     *
     * @param authorizationNo 인가 번호
     * @return AuthorizationResponseDto 인가 응답 DTO
     */
    public AuthorizationResponseDto findById(Integer authorizationNo) {
        Authorization entity = findAuthorization(authorizationNo);

        return new AuthorizationResponseDto(entity);
    }

    /**
     * 인가 다음 정렬 순서 조회
     *
     * @return Integer 다음 정렬 순서
     */
    public Integer findNextSortSeq() {
        return authorizationRepository.findNextSortSeq();
    }

    /**
     * 인가 등록
     *
     * @param requestDto 인가 등록 요청 DTO
     * @return AuthorizationResponseDto 인가 응답 DTO
     */
    @Transactional
    public AuthorizationResponseDto save(AuthorizationSaveRequestDto requestDto) {
        // 동일한 정렬 순서가 존재할 경우 +1
        Optional<Authorization> authorization = authorizationRepository.findBySortSeq(requestDto.getSortSeq());
        if (authorization.isPresent()) {
            authorizationRepository.updateSortSeq(requestDto.getSortSeq(), null, 1);
        }

        // 등록
        Authorization entity = authorizationRepository.save(requestDto.toEntity());

        clearAuthorizationCache();

        return new AuthorizationResponseDto(entity);
    }

    /**
     * 인가 수정
     *
     * @param authorizationNo 인가 번호
     * @param requestDto      인가 수정 요청 DTO
     * @return AuthorizationResponseDto 인가 응답 DTO
     */
    @Transactional
    public AuthorizationResponseDto update(Integer authorizationNo, AuthorizationUpdateRequestDto requestDto) {
        Authorization entity = findAuthorization(authorizationNo);
        // 정렬 순서가 변경된 경우 사이 구간 정렬 순서 조정
        updateSortSeq(entity, requestDto);

        // 수정
        entity.update(requestDto.getAuthorizationName(), requestDto.getUrlPatternValue(), requestDto.getHttpMethodCode(), requestDto.getSortSeq());

        clearAuthorizationCache();

        return new AuthorizationResponseDto(entity);
    }

    /**
     * 정렬순서 update
     *
     * @param entity        인가 엔티티
     * @param requestDto    인가 수정 요청 DTO
     */
    private void updateSortSeq(Authorization entity, AuthorizationUpdateRequestDto requestDto) {
        // 정렬 순서가 변경된 경우 사이 구간 정렬 순서 조정
        Integer beforeSortSeq = entity.getSortSeq();
        Integer afterSortSeq = requestDto.getSortSeq();

        if (beforeSortSeq == null) {
            authorizationRepository.updateSortSeq(afterSortSeq, null, 1);
            return;
        }

        if (afterSortSeq == null) {
            authorizationRepository.updateSortSeq(beforeSortSeq+1, null, -1);
            return;
        }
        int compareTo = beforeSortSeq.compareTo(afterSortSeq);
        if (compareTo > 0) {
            authorizationRepository.updateSortSeq(afterSortSeq, beforeSortSeq-1, 1);
            return;
        }

        if (compareTo < 0) {
            authorizationRepository.updateSortSeq(beforeSortSeq+1, afterSortSeq, -1);
            return;
        }

    }

    /**
     * 인가 삭제
     * 권한 인가도 같이 삭제됨
     *
     * @param authorizationNo 인가 번호
     */
    @Transactional
    public void delete(Integer authorizationNo) {
        Authorization entity = findAuthorization(authorizationNo);

        // 삭제
        authorizationRepository.delete(entity);

        // 삭제한 데이터보다 정렬 순서가 더 큰 데이터 -1
        authorizationRepository.updateSortSeq(entity.getSortSeq() + 1, null, -1);

        clearAuthorizationCache();
    }

    /**
     * 인가 번호로 인가 엔티티 조회
     *
     * @param authorizationNo 인가 번호
     * @return Authorization 인가 엔티티
     */
    private Authorization findAuthorization(Integer authorizationNo) {
        return authorizationRepository.findById(authorizationNo)
                .orElseThrow(() -> new EntityNotFoundException(getMessage("valid.notexists.format", new Object[]{getMessage("authorization")})));
    }

    /**
     * 인가 조회 캐시 클리어
     */
    private void clearAuthorizationCache() {
        Cache useridCache = cacheManager.getCache("cache-user-authorization-by-userid");
        if (useridCache != null) useridCache.clear();
        Cache rolesCache = cacheManager.getCache("cache-user-authorization-by-roles");
        if (rolesCache != null) rolesCache.clear();
    }

}
