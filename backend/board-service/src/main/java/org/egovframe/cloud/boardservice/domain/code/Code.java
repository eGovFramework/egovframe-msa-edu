package org.egovframe.cloud.boardservice.domain.code;

import lombok.NoArgsConstructor;
import org.egovframe.cloud.servlet.domain.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * org.egovframe.cloud.boardservice.domain.code.Code
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
@NoArgsConstructor
@Entity
public class Code extends BaseEntity {

    @Id
    @Column(insertable = false, updatable = false)
    private String codeId; // 코드ID

    @Column(insertable = false, updatable = false)
    private String parentCodeId; // 상위 코드ID

    @Column(insertable = false, updatable = false)
    private String codeName; // 코드 명

}
