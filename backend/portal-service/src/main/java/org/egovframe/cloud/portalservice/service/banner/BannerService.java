package org.egovframe.cloud.portalservice.service.banner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.egovframe.cloud.common.dto.AttachmentEntityMessage;
import org.egovframe.cloud.common.exception.EntityNotFoundException;
import org.egovframe.cloud.common.service.AbstractService;
import org.egovframe.cloud.portalservice.api.banner.dto.BannerImageResponseDto;
import org.egovframe.cloud.portalservice.api.banner.dto.BannerListResponseDto;
import org.egovframe.cloud.portalservice.api.banner.dto.BannerRequestDto;
import org.egovframe.cloud.portalservice.api.banner.dto.BannerResponseDto;
import org.egovframe.cloud.portalservice.api.banner.dto.BannerSaveRequestDto;
import org.egovframe.cloud.portalservice.api.banner.dto.BannerUpdateRequestDto;
import org.egovframe.cloud.portalservice.domain.banner.Banner;
import org.egovframe.cloud.portalservice.domain.banner.BannerRepository;
import org.egovframe.cloud.portalservice.domain.menu.Site;
import org.egovframe.cloud.portalservice.domain.menu.SiteRepository;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * org.egovframe.cloud.portalservice.service.banner.BannerService
 * <p>
 * 배너 서비스 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/08/18
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/08/18    jooho       최초 생성
 * </pre>
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BannerService extends AbstractService {

    /**
     * 배너 레파지토리 인터페이스
     */
    private final BannerRepository bannerRepository;
    private final SiteRepository siteRepository;

    /**
     * 이벤트 메시지 발행하기 위한 spring cloud stream 유틸리티 클래스
     */
    private final StreamBridge streamBridge;

    /**
     * 조회 조건에 일치하는 배너 페이지 목록 조회
     *
     * @param requestDto 요청 DTO
     * @param pageable   페이지 정보
     * @return Page<BannerListResponseDto> 페이지 배너 목록 응답 DTO
     */
    public Page<BannerListResponseDto> findPage(BannerRequestDto requestDto, Pageable pageable) {
        return bannerRepository.findPage(requestDto, pageable);
    }

    /**
     * 유형별 배너 목록 조회
     *
     * @param bannerTypeCodes 배너 유형 코드 목록
     * @param bannerCount     배너 수
     * @param useAt           사용 여부
     * @return Map<String, List<BannerImageResponseDto>> 배너 유형 코드별 배너 이미지 응답 DTO Map
     */
    public Map<String, List<BannerImageResponseDto>> findList(List<String> bannerTypeCodes, Integer bannerCount, Boolean useAt, Long siteId) {
        Map<String, List<BannerImageResponseDto>> bannerMap = new HashMap<>();

        for (String bannerTypeCode : bannerTypeCodes) {
            bannerMap.put(bannerTypeCode, bannerRepository.findList(bannerTypeCode, bannerCount, useAt, siteId));
        }

        return bannerMap;
    }

    /**
     * 배너 단건 조회
     *
     * @param bannerNo 배너 번호
     * @return BannerResponseDto 배너 응답 DTO
     */
    public BannerResponseDto findById(Integer bannerNo) {
        Banner entity = findBanner(bannerNo);

        return new BannerResponseDto(entity);
    }

    /**
     * 배너 다음 정렬 순서 조회
     *
     * @param siteId  siteId
     * @return Integer 다음 정렬 순서
     */
    public Integer findNextSortSeq(Long siteId) {
        return bannerRepository.findNextSortSeq(siteId);
    }

    /**
     * 배너 등록
     *
     * @param requestDto 배너 등록 요청 DTO
     * @return BannerResponseDto 배너 응답 DTO
     */
    @Transactional
    public BannerResponseDto save(BannerSaveRequestDto requestDto) {
        //site 정보 조회
        Site site = siteRepository.findById(requestDto.getSiteId())
            .orElseThrow(() ->
                new EntityNotFoundException(getMessage("valid.notexists.format", new Object[]{getMessage("menu.site")}) + " ID= " + requestDto.getSiteId()));

        // 동일한 정렬 순서가 존재할 경우 +1
        Optional<Banner> authorization = bannerRepository.findBySortSeqAndSiteId(requestDto.getSortSeq(), requestDto.getSiteId());
        if (authorization.isPresent()) {
            bannerRepository.updateSortSeq(requestDto.getSortSeq(), null, 1, requestDto.getSiteId());
        }

        Banner entity = bannerRepository.save(requestDto.toEntity(site));

        //첨부파일 entity 정보 업데이트 하기 위해 이벤트 메세지 발행
        sendAttachment(entity);

        return new BannerResponseDto(entity);
    }

    public void sendAttachment(Banner entity) {
        sendAttachmentEntityInfo(streamBridge,
            AttachmentEntityMessage.builder()
                .attachmentCode(entity.getAttachmentCode())
                .entityName(entity.getClass().getName())
                .entityId(String.valueOf(entity.getBannerNo()))
                .build());
    }

    /**
     * 배너 수정
     *
     * @param bannerNo   배너 번호
     * @param requestDto 배너 수정 요청 DTO
     * @return BannerResponseDto 배너 응답 DTO
     */
    @Transactional
    public BannerResponseDto update(Integer bannerNo, BannerUpdateRequestDto requestDto) {
        Banner entity = findBanner(bannerNo);

        //site 정보 조회
        Site site = siteRepository.findById(requestDto.getSiteId())
            .orElseThrow(() ->
                new EntityNotFoundException(getMessage("valid.notexists.format", new Object[]{getMessage("menu.site")}) + " ID= " + requestDto.getSiteId()));

        // 동일한 정렬 순서가 존재할 경우 +1
        Optional<Banner> authorization = bannerRepository.findBySortSeqAndSiteId(requestDto.getSortSeq(), requestDto.getSiteId());
        if (authorization.isPresent()) {
            bannerRepository.updateSortSeq(requestDto.getSortSeq(), null, 1, requestDto.getSiteId());
        }

        // 수정
        entity.update(requestDto.getBannerTypeCode(), requestDto.getBannerTitle(), requestDto.getAttachmentCode(),
                requestDto.getUrlAddr(), requestDto.getNewWindowAt(), requestDto.getBannerContent(), requestDto.getSortSeq(), site);

        return new BannerResponseDto(entity);
    }

    /**
     * 배너 사용 여부 수정
     *
     * @param bannerNo 배너 번호
     * @param useAt    사용 여부
     * @return BannerResponseDto 배너 응답 DTO
     */
    @Transactional
    public BannerResponseDto updateUseAt(Integer bannerNo, Boolean useAt) {
        Banner entity = findBanner(bannerNo);

        // 수정
        entity.updateUseAt(useAt);

        return new BannerResponseDto(entity);
    }

    /**
     * 배너 삭제
     *
     * @param bannerNo 배너 번호
     */
    @Transactional
    public void delete(Integer bannerNo) {
        Banner entity = findBanner(bannerNo);

        // 삭제
        bannerRepository.delete(entity);
    }

    /**
     * 배너 번호로 배너 엔티티 조회
     *
     * @param bannerNo 배너 번호
     * @return Banner 배너 엔티티
     */
    private Banner findBanner(Integer bannerNo) {
        return bannerRepository.findById(bannerNo)
                .orElseThrow(() -> new EntityNotFoundException(getMessage("valid.notexists.format", new Object[]{getMessage("banner")})));
    }

}
