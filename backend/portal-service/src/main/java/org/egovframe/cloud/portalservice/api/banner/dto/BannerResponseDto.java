package org.egovframe.cloud.portalservice.api.banner.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egovframe.cloud.portalservice.domain.banner.Banner;

/**
 * org.egovframe.cloud.portalservice.api.content.dto.BannerResponseDto
 * <p>
 * 배너 상세 응답 DTO 클래스
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
@Getter
@NoArgsConstructor
public class BannerResponseDto {

    /**
     * 배너 번호
     */
    private Integer bannerNo;

    /**
     * 배너 구분 코드
     */
    private String bannerTypeCode;

    /**
     * 배너 제목
     */
    private String bannerTitle;

    /**
     * 첨부파일 코드
     */
    private String attachmentCode;

    /**
     * url 주소
     */
    private String urlAddr;

    /**
     * 새 창 여부
     */
    private Boolean newWindowAt;

    /**
     * 배너 내용
     */
    private String bannerContent;

    /**
     * 정렬 순서
     */
    private Integer sortSeq;

    /**
     * site Id
     */
    private Long siteId;

    /**
     * 배너 엔티티를 생성자로 주입 받아서 배너 상세 응답 DTO 속성 값 세팅
     *
     * @param entity 배너 엔티티
     */
    public BannerResponseDto(Banner entity) {
        this.bannerNo = entity.getBannerNo();
        this.bannerTypeCode = entity.getBannerTypeCode();
        this.bannerTitle = entity.getBannerTitle();
        this.attachmentCode = entity.getAttachmentCode();
        this.urlAddr = entity.getUrlAddr();
        this.newWindowAt = entity.getNewWindowAt();
        this.bannerContent = entity.getBannerContent();
        this.sortSeq = entity.getSortSeq();
        this.siteId = entity.getSite().getId();
    }

    /**
     * 배너 상세 응답 DTO 속성 값으로 배너 엔티티 빌더를 사용하여 객체 생성
     *
     * @return Banner 배너 엔티티
     */
    public Banner toEntity() {
        return Banner.builder()
                .bannerNo(bannerNo)
                .bannerTypeCode(bannerTypeCode)
                .bannerTitle(bannerTitle)
                .attachmentCode(attachmentCode)
                .urlAddr(urlAddr)
                .newWindowAt(newWindowAt)
                .bannerContent(bannerContent)
                .sortSeq(sortSeq)
                .build();
    }

}
