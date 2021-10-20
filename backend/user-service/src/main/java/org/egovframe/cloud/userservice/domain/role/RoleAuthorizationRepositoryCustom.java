package org.egovframe.cloud.userservice.domain.role;

import org.egovframe.cloud.userservice.api.role.dto.RoleAuthorizationListRequestDto;
import org.egovframe.cloud.userservice.api.role.dto.RoleAuthorizationListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * org.egovframe.cloud.userservice.domain.role.RoleAuthorizationRepositoryCustom
 * <p>
 * 권한 인가 Querydsl 인터페이스
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
public interface RoleAuthorizationRepositoryCustom {

    /**
     * 권한 인가 페이지 목록 조회
     * 인가 기준으로 권한 인가 아우터 조인
     * 가급적 Entity 보다는 Dto를 리턴 - Entity 조회시 hibernate 캐시, 불필요 컬럼 조회, oneToOne N+1 문제 발생
     *
     * @param requestDto 권한 인가 목록 요청 DTO
     * @param pageable   페이지 정보
     * @return Page<RoleAuthorizationListResponseDto> 페이지 권한 인가 목록 응답 DTO
     */
    Page<RoleAuthorizationListResponseDto> findPageAuthorizationList(RoleAuthorizationListRequestDto requestDto, Pageable pageable);

}
