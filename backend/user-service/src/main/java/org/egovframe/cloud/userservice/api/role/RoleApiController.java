package org.egovframe.cloud.userservice.api.role;

import lombok.RequiredArgsConstructor;
import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.userservice.api.role.dto.RoleListResponseDto;
import org.egovframe.cloud.userservice.service.role.RoleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * org.egovframe.cloud.userservice.api.role.RoleApiController
 * <p>
 * 권한 Rest API 컨트롤러 클래스
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
@RequiredArgsConstructor
@RestController
public class RoleApiController {

    /**
     * 권한 서비스
     */
    private final RoleService roleService;

    /**
     * 권한 페이지 목록 조회
     *
     * @param requestDto 요청 DTO
     * @param pageable 페이지 정보
     * @return Page<RoleListResponseDto> 페이지 권한 목록 응답 DTO
     */
    @GetMapping("/api/v1/roles")
    public Page<RoleListResponseDto> findPage(RequestDto requestDto,
                                              @PageableDefault(sort = "sort_seq", direction = Sort.Direction.ASC) Pageable pageable) {
        return roleService.findPage(requestDto, pageable);
    }

    /**
     * 권한 정렬 순서 오름차순 전체 목록 조회
     *
     * @return List<RoleListResponseDto>
     */
    @GetMapping("/api/v1/roles/all")
    public List<RoleListResponseDto> findAll() {
        return roleService.findAllBySort(Sort.by(Sort.Direction.ASC, "sortSeq"));
    }

}
