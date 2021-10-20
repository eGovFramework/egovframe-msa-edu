package org.egovframe.cloud.portalservice.domain.banner;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.egovframe.cloud.portalservice.domain.menu.Site;
import org.egovframe.cloud.servlet.domain.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * org.egovframe.cloud.portalservice.domain.banner.Banner
 * <p>
 * 배너 엔티티 클래스
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
@Getter
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Entity
public class Banner extends BaseEntity {

    /**
     * 배너 번호
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bannerNo;

    /**
     * 배너 구분 코드
     */
    @Column(nullable = false, length = 20)
    private String bannerTypeCode;

    /**
     * 배너 제목
     */
    @Column(nullable = false, length = 100)
    private String bannerTitle;

    /**
     * 첨부파일 코드
     */
    @Column(nullable = false, length = 255)
    private String attachmentCode;

    /**
     * url 주소
     */
    @Column(length = 500)
    private String urlAddr;

    /**
     * 새 창 여부
     */
    @Column(nullable = false, columnDefinition = "tinyint(1) default '0'")
    private Boolean newWindowAt;

    /**
     * 배너 내용
     */
    @Column(length = 2000)
    private String bannerContent;

    /**
     * 정렬 순서
     */
    @Column(nullable = false, columnDefinition = "mediumint(5) default '99999'")
    private Integer sortSeq;

    /**
     * 사용 여부
     */
    @Column(nullable = false, columnDefinition = "tinyint(1) default '1'")
    private Boolean useAt;

    /**
     * 사이트 정보
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id")
    private Site site;

    /**
     * 빌더 패턴 클래스 생성자
     *
     * @param bannerNo       배너 번호
     * @param bannerTypeCode 배너 구분 코드
     * @param bannerTitle    배너 제목
     * @param attachmentCode 첨부파일 코드
     * @param urlAddr        url 주소
     * @param newWindowAt    새 창 여부
     * @param bannerContent  배너 내용
     * @param sortSeq        정렬 순서
     * @param useAt          사용 여부
     */
    @Builder
    public Banner(Integer bannerNo, String bannerTypeCode, String bannerTitle, String attachmentCode,
                  String urlAddr, Boolean newWindowAt, String bannerContent, Integer sortSeq, Boolean useAt, Site site) {
        this.bannerNo = bannerNo;
        this.bannerTypeCode = bannerTypeCode;
        this.bannerTitle = bannerTitle;
        this.attachmentCode = attachmentCode;
        this.urlAddr = urlAddr;
        this.newWindowAt = newWindowAt;
        this.bannerContent = bannerContent;
        this.sortSeq = sortSeq;
        this.useAt = useAt;
        this.site = site;
    }

    /**
     * 배너 속성 값 수정
     *
     * @param bannerTypeCode 배너 구분 코드
     * @param bannerTitle    배너 제목
     * @param attachmentCode 첨부파일 코드
     * @param urlAddr        url 주소
     * @param bannerContent  배너 내용
     * @return Banner 배너 엔티티
     */
    public Banner update(String bannerTypeCode, String bannerTitle, String attachmentCode,
                         String urlAddr, Boolean newWindowAt, String bannerContent, Integer sortSeq, Site site) {
        this.bannerTypeCode = bannerTypeCode;
        this.bannerTitle = bannerTitle;
        this.attachmentCode = attachmentCode;
        this.urlAddr = urlAddr;
        this.newWindowAt = newWindowAt;
        this.bannerContent = bannerContent;
        this.sortSeq = sortSeq;
        this.site = site;

        return this;
    }

    /**
     * 배너 사용 여부 수정
     *
     * @param useAt 사용 여부
     * @return Banner 배너 엔티티
     */
    public Banner updateUseAt(Boolean useAt) {
        this.useAt = useAt;

        return this;
    }

}
