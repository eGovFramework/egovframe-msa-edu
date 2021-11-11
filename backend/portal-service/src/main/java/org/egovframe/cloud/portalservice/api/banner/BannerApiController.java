package org.egovframe.cloud.portalservice.api.banner;

import lombok.RequiredArgsConstructor;
import org.egovframe.cloud.portalservice.api.banner.dto.*;
import org.egovframe.cloud.portalservice.service.banner.BannerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * org.egovframe.cloud.portalservice.api.banner.BannerApiController
 * <p>
 * 배너 Rest API 컨트롤러 클래스
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
@RequiredArgsConstructor
@RestController
public class BannerApiController {

    /**
     * 배너 서비스
     */
    private final BannerService bannerService;

    /**
     * 배너 페이지 목록 조회
     *
     * @param requestDto 요청 DTO
     * @param pageable   페이지 정보
     * @return Page<BannerListResponseDto> 페이지 배너 목록 응답 DTO
     */
    @GetMapping("/api/v1/banners")
    public Page<BannerListResponseDto> findPage(BannerRequestDto requestDto,
                                                @PageableDefault(sort = "banner_no", direction = Sort.Direction.DESC) Pageable pageable) {
        return bannerService.findPage(requestDto, pageable);
    }

    /**
     * 유형별 배너 목록 조회
     *
     * @param siteId          사이트 ID
     * @param bannerTypeCodes 배너 유형 코드 목록
     * @param bannerCount     배너 수
     * @return Map<String, List<BannerImageResponseDto>> 배너 유형 코드별 배너 이미지 응답 DTO Map
     */
    @GetMapping("/api/v1/{siteId}/banners/{bannerTypeCodes}/{bannerCount}")
    public Map<String, List<BannerImageResponseDto>> findUseList(@PathVariable Long siteId, @PathVariable List<String> bannerTypeCodes, @PathVariable Integer bannerCount) {
        return bannerService.findList(bannerTypeCodes, bannerCount, true, siteId);
    }

    /**
     * 배너 단건 조회
     *
     * @param bannerNo 배너 번호
     * @return BannerResponseDto 배너 상세 응답 DTO
     */
    @GetMapping("/api/v1/banners/{bannerNo}")
    public BannerResponseDto findById(@PathVariable Integer bannerNo) {
        return bannerService.findById(bannerNo);
    }

    /**
     * 배너 다음 정렬 순서 조회
     *
     * @param siteId  siteId
     * @return Integer 다음 정렬 순서
     */
    @GetMapping("/api/v1/banners/{siteId}/sort-seq/next")
    public Integer findNextSortSeq(@PathVariable Long siteId) {
        return bannerService.findNextSortSeq(siteId);
    }

    /**
     * 배너 등록
     *
     * @param requestDto 배너 등록 요청 DTO
     * @return BannerResponseDto 배너 상세 응답 DTO
     */
    @PostMapping("/api/v1/banners")
    @ResponseStatus(HttpStatus.CREATED)
    public BannerResponseDto save(@RequestBody @Valid BannerSaveRequestDto requestDto) {
        return bannerService.save(requestDto);
    }

    /**
     * 배너 수정
     *
     * @param bannerNo   배너 번호
     * @param requestDto 배너 수정 요청 DTO
     * @return BannerResponseDto 배너 상세 응답 DTO
     */
    @PutMapping("/api/v1/banners/{bannerNo}")
    public BannerResponseDto update(@PathVariable Integer bannerNo, @RequestBody @Valid BannerUpdateRequestDto requestDto) {
        return bannerService.update(bannerNo, requestDto);
    }

    /**
     * 배너 사용 여부 수정
     *
     * @param bannerNo 배너 번호
     * @param useAt    사용 여부
     * @return BannerResponseDto 배너 상세 응답 DTO
     */
    @PutMapping("/api/v1/banners/{bannerNo}/{useAt}")
    public BannerResponseDto updateUseAt(@PathVariable Integer bannerNo, @PathVariable Boolean useAt) {
        return bannerService.updateUseAt(bannerNo, useAt);
    }

    /**
     * 배너 삭제
     *
     * @param bannerNo 배너 번호
     */
    @DeleteMapping("/api/v1/banners/{bannerNo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer bannerNo) {
        bannerService.delete(bannerNo);
    }

}
