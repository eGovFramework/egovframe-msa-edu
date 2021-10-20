package org.egovframe.cloud.portalservice.domain.content;

import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.portalservice.api.content.dto.ContentListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * org.egovframe.cloud.portalservice.domain.content.ContentRepositoryCustom
 * <p>
 * 컨텐츠 Querydsl 인터페이스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/23
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/23    jooho       최초 생성
 * </pre>
 */
public interface ContentRepositoryCustom {

    /**
     * 컨텐츠 페이지 목록 조회
     *
     * @param requestDto 컨텐츠 목록 요청 DTO
     * @param pageable   페이지 정보
     * @return Page<ContentListResponseDto> 페이지 컨텐츠 목록 응답 DTO
     */
    Page<ContentListResponseDto> findPage(RequestDto requestDto, Pageable pageable);

}
