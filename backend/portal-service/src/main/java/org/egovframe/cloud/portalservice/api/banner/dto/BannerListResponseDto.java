package org.egovframe.cloud.portalservice.api.banner.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * org.egovframe.cloud.portalservice.api.content.dto.BannerListResponseDto
 * <p>
 * 배너 목록 응답 DTO 클래스
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
public class BannerListResponseDto implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2944698753371861587L;

    /**
     * 배너 번호
     */
    private Integer bannerNo;

    /**
     * 배너 구분 코드
     */
    private String bannerTypeCode;

    /**
     * 배너 구분 코드 명
     */
    private String bannerTypeCodeName;

    /**
     * 배너 제목
     */
    private String bannerTitle;

    /**
     * 사용 여부
     */
    private Boolean useAt;

    /**
     * 수정 일시
     */
    private LocalDateTime createdDate;

    /**
     * site 명
     */
    private String siteName;

    /**
     * 배너 목록 응답 DTO 생성자
     *
     * @param bannerNo           배너 번호
     * @param bannerTypeCode     배너 구분 코드
     * @param bannerTypeCodeName 배너 구분 코드 명
     * @param bannerTitle        배너 제목
     * @param useAt              사용 여부
     * @param createdDate        생성 일시
     */
    @QueryProjection
    public BannerListResponseDto(Integer bannerNo, String bannerTypeCode, String bannerTypeCodeName, String bannerTitle, Boolean useAt, LocalDateTime createdDate, String siteName) {
        this.bannerNo = bannerNo;
        this.bannerTypeCode = bannerTypeCode;
        this.bannerTypeCodeName = bannerTypeCodeName;
        this.bannerTitle = bannerTitle;
        this.useAt = useAt;
        this.createdDate = createdDate;
        this.siteName = siteName;
    }

}
