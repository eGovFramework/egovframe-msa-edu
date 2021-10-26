package org.egovframe.cloud.portalservice.api.banner.dto;

import lombok.Getter;
import org.egovframe.cloud.portalservice.domain.banner.Banner;
import org.egovframe.cloud.portalservice.domain.menu.Site;

import javax.validation.constraints.NotBlank;

/**
 * org.egovframe.cloud.portalservice.api.content.dto.BannerSaveRequestDto
 * <p>
 * 배너 등록 요청 DTO 클래스
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
public class BannerSaveRequestDto {

    /**
     * 배너 구분 코드
     */
    @NotBlank(message = "{banner.banner_type_code} {err.required}")
    private String bannerTypeCode;

    /**
     * 배너 제목
     */
    @NotBlank(message = "{banner.banner_title} {err.required}")
    private String bannerTitle;

    /**
     * 첨부파일 코드
     */
    @NotBlank(message = "{banner.attachment_code} {err.required}")
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
     * siteId
     */
    private Long siteId;

    /**
     * 배너 등록 요청 DTO 속성 값으로 배너 엔티티 빌더를 사용하여 객체 생성
     *
     * @return Banner 배너 엔티티
     */
    public Banner toEntity(Site site) {
        return Banner.builder()
                .bannerTypeCode(bannerTypeCode)
                .bannerTitle(bannerTitle)
                .attachmentCode(attachmentCode)
                .urlAddr(urlAddr)
                .newWindowAt(newWindowAt)
                .bannerContent(bannerContent)
                .sortSeq(sortSeq)
                .site(site)
                .build();
    }

}
