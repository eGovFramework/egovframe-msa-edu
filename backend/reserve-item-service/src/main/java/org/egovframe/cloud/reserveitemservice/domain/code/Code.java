package org.egovframe.cloud.reserveitemservice.domain.code;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.egovframe.cloud.reactive.domain.BaseEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * org.egovframe.cloud.boardservice.domain.org.egovframe.cloud.reserveitemservice.domain.org.egovframe.cloud.reserveitemservice.domain.code.Code
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
 *  2021/09/15    shinmj      r2dbc 변경
 * </pre>
 */
@Getter
@NoArgsConstructor
@ToString
@Table("code")
public class Code extends BaseEntity {
    @Id
    @Column
    private String codeId; // 코드ID

    @Column
    private String parentCodeId; // 상위 코드ID

    @Column
    private String codeName; // 코드 명

    @Builder
    public Code(String codeId, String parentCodeId, String codeName) {
        this.codeId = codeId;
        this.parentCodeId = parentCodeId;
        this.codeName = codeName;
    }
}
