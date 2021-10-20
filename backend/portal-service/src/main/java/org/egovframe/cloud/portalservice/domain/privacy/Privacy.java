package org.egovframe.cloud.portalservice.domain.privacy;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egovframe.cloud.servlet.domain.BaseEntity;

import javax.persistence.*;

/**
 * org.egovframe.cloud.portalservice.domain.privacy.Privacy
 * <p>
 * 개인정보처리방침 엔티티 클래스
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
@Getter
@NoArgsConstructor
@Entity
public class Privacy extends BaseEntity {

    /**
     * 개인정보처리방침 번호
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer privacyNo;

    /**
     * 개인정보처리방침 제목
     */
    @Column(nullable = false, length = 100)
    private String privacyTitle;

    /**
     * 개인정보처리방침 내용
     */
    @Column(nullable = false, columnDefinition = "longtext")
    private String privacyContent;

    /**
     * 사용 여부
     */
    @Column(nullable = false, columnDefinition = "tinyint(1) default '1'")
    private Boolean useAt;

    /**
     * 빌더 패턴 클래스 생성자
     *
     * @param privacyNo      개인정보처리방침 번호
     * @param privacyTitle   개인정보처리방침 제목
     * @param privacyContent 개인정보처리방침 내용
     * @param useAt          사용 여부
     */
    @Builder
    public Privacy(Integer privacyNo, String privacyTitle, String privacyContent, Boolean useAt) {
        this.privacyNo = privacyNo;
        this.privacyTitle = privacyTitle;
        this.privacyContent = privacyContent;
        this.useAt = useAt;
    }

    /**
     * 개인정보처리방침 속성 값 수정
     *
     * @param privacyTitle   개인정보처리방침 제목
     * @param privacyContent 개인정보처리방침 내용
     * @param useAt          사용 여부
     * @return Privacy 개인정보처리방침 엔티티
     */
    public Privacy update(String privacyTitle, String privacyContent, Boolean useAt) {
        this.privacyTitle = privacyTitle;
        this.privacyContent = privacyContent;
        this.useAt = useAt;

        return this;
    }

    /**
     * 개인정보처리방침 사용 여부 수정
     *
     * @param useAt 사용 여부
     * @return Privacy 개인정보처리방침 엔티티
     */
    public Privacy updateUseAt(Boolean useAt) {
        this.useAt = useAt;

        return this;
    }

}
