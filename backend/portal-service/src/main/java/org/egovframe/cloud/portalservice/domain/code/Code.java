package org.egovframe.cloud.portalservice.domain.code;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egovframe.cloud.servlet.domain.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * org.egovframe.cloud.portalservice.domain.code.Code
 * <p>
 * 공통코드 엔티티
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/07/12
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/12    jaeyeolkim  최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
@Entity
public class Code extends BaseEntity {

    @Id
    @Column(length = 20)
    private String codeId; // 코드ID

    @Column(length = 20)
    private String parentCodeId; // 상위 코드ID

    @Column(nullable = false, length = 500)
    private String codeName; // 코드 명

    @Column(length = 500)
    private String codeDescription; // 코드 설명

    @Column(columnDefinition = "SMALLINT(3)")
    private Integer sortSeq; // 정렬 순서

    @Column(nullable = false)
    private Boolean useAt; // 사용 여부

    @Column(nullable = false, name = "readonly_at")
    private Boolean readonly; // 수정하면 안되는 읽기전용 공통코드

    @Builder
    public Code(String codeId, String parentCodeId, String codeName, String codeDescription, Integer sortSeq, Boolean useAt, Boolean readonly) {
        this.codeId = codeId;
        this.parentCodeId = parentCodeId;
        this.codeName = codeName;
        this.codeDescription = codeDescription;
        this.sortSeq = sortSeq;
        this.useAt = useAt;
        this.readonly = readonly;
    }

    /**
     * 공통코드 정보를 수정한다.
     *
     * @param codeName
     * @param codeDescription
     * @param sortSeq
     * @param useAt
     * @return
     */
    public Code update(String codeName, String codeDescription, Integer sortSeq, Boolean useAt) {
        this.codeName = codeName;
        this.codeDescription = codeDescription;
        this.sortSeq = sortSeq;
        this.useAt = useAt;

        return this;
    }

    /**
     * 상세공통코드 정보를 수정한다.
     *
     * @param codeName
     * @param codeDescription
     * @param sortSeq
     * @param useAt
     * @return
     */
    public Code updateDetail(String parentCodeId, String codeName, String codeDescription, Integer sortSeq, Boolean useAt) {
        this.parentCodeId = parentCodeId;
        this.codeName = codeName;
        this.codeDescription = codeDescription;
        this.sortSeq = sortSeq;
        this.useAt = useAt;

        return this;
    }

    /**
     * 공통코드 사용여부를 수정한다.
     *
     * @param useAt
     * @return
     */
    public Code updateUseAt(boolean useAt) {
        this.useAt = useAt;

        return this;
    }
}
