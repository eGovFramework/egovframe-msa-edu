package org.egovframe.cloud.userservice.domain.role;

import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.userservice.api.role.dto.AuthorizationListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * org.egovframe.cloud.userservice.domain.role.AuthorizationRepositoryCustom
 * <p>
 * 인가 Querydsl 인터페이스
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
public interface AuthorizationRepositoryCustom {

    /**
     * 인가 페이지 목록 조회
     *
     * @param requestDto 인가 목록 요청 DTO
     * @param pageable   페이지 정보
     * @return Page<AuthorizationListResponseDto> 페이지 인가 목록 응답 DTO
     */
    Page<AuthorizationListResponseDto> findPage(RequestDto requestDto, Pageable pageable);

    /**
     * 권한 목록의 인가 전체 목록 조회
     *
     * @param roles 권한 목록
     * @return Page<AuthorizationListResponseDto> 페이지 인가 목록 응답 DTO
     */
    List<AuthorizationListResponseDto> findByRoles(List<String> roles);

    /**
     * 사용자의 인가 목록 조회
     *
     * @param userId 사용자 id
     * @return List<AuthorizationListResponseDto> 인가 목록 응답 DTO
     */
    List<AuthorizationListResponseDto> findByUserId(String userId);

    /**
     * 인가 다음 정렬 순서 조회
     *
     * @return Integer 다음 정렬 순서
     */
    Integer findNextSortSeq();

    /**
     * 인가 정렬 순서 수정
     *
     * @param startSortSeq    시작 정렬 순서
     * @param endSortSeq      종료 정렬 순서
     * @param increaseSortSeq 증가 정렬 순서
     * @return Long 처리 건수
     */
    Long updateSortSeq(Integer startSortSeq, Integer endSortSeq, int increaseSortSeq);

}
