package org.egovframe.cloud.userservice.domain.role;

import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.userservice.api.role.dto.RoleListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * org.egovframe.cloud.userservice.domain.role.RoleRepositoryCustom
 * <p>
 * 권한 Querydsl 인터페이스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/15
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/15    jooho       최초 생성
 * </pre>
 */
public interface RoleRepositoryCustom {

    /**
     * 권한 페이지 목록 조회
     * 가급적 Entity 보다는 Dto를 리턴 - Entity 조회시 hibernate 캐시, 불필요 컬럼 조회, oneToOne N+1 문제 발생
     *
     * @param requestDto 요청 DTO
     * @param pageable   페이지 정보
     * @return Page<RoleListResponseDto> 페이지 권한 목록 응답 DTO
     */
    Page<RoleListResponseDto> findPage(RequestDto requestDto, Pageable pageable);

}
