package org.egovframe.cloud.userservice.service.role;

import lombok.RequiredArgsConstructor;
import org.egovframe.cloud.common.service.AbstractService;
import org.egovframe.cloud.userservice.api.role.dto.RoleAuthorizationDeleteRequestDto;
import org.egovframe.cloud.userservice.api.role.dto.RoleAuthorizationListRequestDto;
import org.egovframe.cloud.userservice.api.role.dto.RoleAuthorizationListResponseDto;
import org.egovframe.cloud.userservice.api.role.dto.RoleAuthorizationSaveRequestDto;
import org.egovframe.cloud.userservice.domain.role.RoleAuthorization;
import org.egovframe.cloud.userservice.domain.role.RoleAuthorizationRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * org.egovframe.cloud.userservice.service.role.RoleAuthorizationService
 * <p>
 * 권한 인가 서비스 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/12
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/12    jooho       최초 생성
 * </pre>
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RoleAuthorizationService extends AbstractService {

    /**
     * 권한 인가 레파지토리 인터페이스
     */
    private final RoleAuthorizationRepository roleAuthorizationRepository;

    /**
     * 캐시 관리자
     */
    private final CacheManager cacheManager;

    /**
     * 조회 조건에 일치하는 권한 인가 페이지 목록 조회
     *
     * @param requestDto 권한 인가 목록 요청 DTO
     * @param pageable   페이지 정보
     * @return Page<RoleAuthorizationListResponseDto> 페이지 권한 인가 목록 응답 DTO
     */
    public Page<RoleAuthorizationListResponseDto> findPageAuthorizationList(RoleAuthorizationListRequestDto requestDto, Pageable pageable) {
        if (requestDto.getRoleId() == null || "".equals(requestDto.getRoleId())) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        return roleAuthorizationRepository.findPageAuthorizationList(requestDto, pageable);
    }

    /**
     * 권한 인가 다건 등록
     *
     * @param requestDtoList 권한 인가 등록 요청 DTO List
     * @return List<RoleAuthorizationListResponseDto> 등록 권한 인가 목록
     */
    @Transactional
    public List<RoleAuthorizationListResponseDto> save(List<RoleAuthorizationSaveRequestDto> requestDtoList) {
        List<RoleAuthorization> saveEntityList = requestDtoList.stream()
                .map(RoleAuthorizationSaveRequestDto::toEntity)
                .collect(Collectors.toList());

        List<RoleAuthorization> savedEntityList = roleAuthorizationRepository.saveAll(saveEntityList);

        clearAuthorizationCache();

        return savedEntityList.stream()
                .map(m -> RoleAuthorizationListResponseDto.builder()
                        .roleId(m.getRoleAuthorizationId().getRoleId())
                        .authorizationNo(m.getRoleAuthorizationId().getAuthorizationNo())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 권한 인가 다건 삭제
     *
     * @param requestDtoList 권한 인가 삭제 요청 DTO List
     */
    @Transactional
    public void delete(List<RoleAuthorizationDeleteRequestDto> requestDtoList) {
        List<RoleAuthorization> deleteEntityList = requestDtoList.stream()
                .map(RoleAuthorizationDeleteRequestDto::toEntity)
                .collect(Collectors.toList());

        roleAuthorizationRepository.deleteAll(deleteEntityList);

        clearAuthorizationCache();
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
