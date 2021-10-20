package org.egovframe.cloud.portalservice.domain.banner;

import java.util.List;
import java.util.Optional;

import org.egovframe.cloud.portalservice.api.banner.dto.BannerImageResponseDto;
import org.egovframe.cloud.portalservice.api.banner.dto.BannerListResponseDto;
import org.egovframe.cloud.portalservice.api.banner.dto.BannerRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * org.egovframe.cloud.portalservice.domain.banner.BannerRepositoryCustom
 * <p>
 * 배너 Querydsl 인터페이스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/08/18
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *    수정일       수정자              수정내용
 *  ----------    --------    ---------------------------
 *  2021/08/18    jooho       최초 생성
 * </pre>
 */
public interface BannerRepositoryCustom {

    /**
     * 배너 페이지 목록 조회
     *
     * @param requestDto 배너 목록 요청 DTO
     * @param pageable   페이지 정보
     * @return Page<BannerListResponseDto> 페이지 배너 목록 응답 DTO
     */
    Page<BannerListResponseDto> findPage(BannerRequestDto requestDto, Pageable pageable);

    /**
     * 배너 목록 조회
     *
     * @param bannerTypeCode 배너 유형 코드
     * @param bannerCount    배너 수
     * @param useAt          사용 여부
     * @return List<BannerImageResponseDto> 배너 이미지 응답 DTO List
     */
    List<BannerImageResponseDto> findList(String bannerTypeCode, Integer bannerCount, Boolean useAt, Long siteId);

    /**
     * 배너 다음 정렬 순서 조회
     *
     * @return Integer 다음 정렬 순서
     */
    Integer findNextSortSeq(Long siteId);

    /**
     * 배너 정렬 순서 수정
     *
     * @param startSortSeq    시작 정렬 순서
     * @param endSortSeq      종료 정렬 순서
     * @param increaseSortSeq 증가 정렬 순서
     * @return Long 처리 건수
     */
    Long updateSortSeq(Integer startSortSeq, Integer endSortSeq, int increaseSortSeq, Long siteId);

    /**
     * 정렬 순서로 배너 단건 조회
     *
     * @param sortSeq 정렬 순서
     * @return Banner 배너 엔티티
     */
    Optional<Banner> findBySortSeqAndSiteId(Integer sortSeq, Long siteId);

}
