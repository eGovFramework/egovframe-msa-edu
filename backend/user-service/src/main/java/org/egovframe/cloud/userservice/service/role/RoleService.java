package org.egovframe.cloud.userservice.service.role;

import lombok.RequiredArgsConstructor;
import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.common.service.AbstractService;
import org.egovframe.cloud.userservice.api.role.dto.RoleListResponseDto;
import org.egovframe.cloud.userservice.domain.role.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * org.egovframe.cloud.userservice.service.role.RoleService
 * <p>
 * 권한 서비스 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/07
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/07    jooho       최초 생성
 * </pre>
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RoleService extends AbstractService {

    /**
     * 권한 레파지토리 인터페이스
     */
    private final RoleRepository roleRepository;

    /**
     * 조회 조건에 일치하는 권한 페이지 목록 조회
     *
     * @param requestDto 요청 DTO
     * @param pageable   페이지 정보
     * @return Page<RoleListResponseDto> 페이지 권한 목록 응답 DTO
     */
    public Page<RoleListResponseDto> findPage(RequestDto requestDto, Pageable pageable) {
        return roleRepository.findPage(requestDto, pageable);
    }

    /**
     * 권한 전체 조회
     *
     * @param sort 정렬
     * @return List<RoleListResponseDto> 권한 목록 응답 DTO
     */
    public List<RoleListResponseDto> findAllBySort(Sort sort) {
        return roleRepository.findAll(sort).stream()
                .map(m -> RoleListResponseDto.builder()
                        .roleId(m.getRoleId())
                        .roleName(m.getRoleName())
                        .roleContent(m.getRoleContent())
                        .createdDate(m.getCreatedDate())
                        .build())
                .collect(Collectors.toList());
    }

}
