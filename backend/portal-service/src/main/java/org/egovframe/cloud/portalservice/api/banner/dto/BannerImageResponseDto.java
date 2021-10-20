package org.egovframe.cloud.portalservice.api.banner.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * org.egovframe.cloud.portalservice.api.banner.dto.BannerImageResponseDto
 * <p>
 * 배너 이미지 응답 DTO 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/09/03
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *    수정일       수정자              수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/03    jooho       최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
public class BannerImageResponseDto implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -5701020612682455280L;

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

    private String uniqueId;

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
     * 배너 목록 응답 DTO 생성자
     *
     * @param bannerNo       배너 번호
     * @param bannerTypeCode 배너 구분 코드
     * @param bannerTitle    배너 제목
     * @param attachmentCode 첨부파일 코드
     * @param uniqueId     첨부파일 unique 코드
     * @param urlAddr        url 주소
     * @param newWindowAt    새 창 여부
     * @param bannerContent  배너 내용
     */
    @QueryProjection
    public BannerImageResponseDto(Integer bannerNo, String bannerTypeCode, String bannerTitle, String attachmentCode, String uniqueId, String urlAddr, Boolean newWindowAt, String bannerContent) {
        this.bannerNo = bannerNo;
        this.bannerTypeCode = bannerTypeCode;
        this.bannerTitle = bannerTitle;
        this.attachmentCode = attachmentCode;
        this.uniqueId = uniqueId;
        this.urlAddr = urlAddr;
        this.newWindowAt = newWindowAt;
        this.bannerContent = bannerContent;
    }

}
