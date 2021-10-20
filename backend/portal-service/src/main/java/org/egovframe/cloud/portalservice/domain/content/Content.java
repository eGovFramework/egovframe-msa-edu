package org.egovframe.cloud.portalservice.domain.content;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egovframe.cloud.servlet.domain.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * org.egovframe.cloud.portalservice.domain.content.Content
 * <p>
 * 컨텐츠 엔티티 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/22
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/22    jooho       최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
@Entity
@DynamicInsert
@DynamicUpdate
public class Content extends BaseEntity {

    /**
     * 컨텐츠 번호
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer contentNo;

    /**
     * 컨텐츠 명
     */
    @Column(nullable = false, length = 100)
    private String contentName;

    /**
     * 컨텐츠 비고
     */
    @Column(length = 200)
    private String contentRemark;

    /**
     * 컨텐츠 값
     */
    @Column(nullable = false, columnDefinition = "longtext")
    private String contentValue;

    /**
     * 빌더 패턴 클래스 생성자
     *
     * @param contentNo     컨텐츠 번호
     * @param contentName   컨텐츠 명
     * @param contentRemark 컨텐츠 비고
     * @param contentValue  컨텐츠 값
     */
    @Builder
    public Content(Integer contentNo, String contentName, String contentRemark, String contentValue) {
        this.contentNo = contentNo;
        this.contentName = contentName;
        this.contentRemark = contentRemark;
        this.contentValue = contentValue;
    }

    /**
     * 컨텐츠 속성 값 수정
     *
     * @param contentName   컨텐츠 명
     * @param contentRemark 컨텐츠 비고
     * @param contentValue  컨텐츠 값
     * @return Content 컨텐츠 엔티티
     */
    public Content update(String contentName, String contentRemark, String contentValue) {
        this.contentName = contentName;
        this.contentRemark = contentRemark;
        this.contentValue = contentValue;

        return this;
    }

}
