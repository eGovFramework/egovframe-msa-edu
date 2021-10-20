package org.egovframe.cloud.portalservice.domain.policy;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.egovframe.cloud.servlet.domain.BaseEntity;

import javax.persistence.*;
import java.time.ZonedDateTime;

/**
 * org.egovframe.cloud.portalservice.domain.policy.Policy
 * <p>
 * 이용약관/개인정보수집동의(Policy) 도메인 class
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/07/06
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/06    shinmj  최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
@ToString
@Entity
public class Policy extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policy_id")
    private Long id;

    @Column(nullable = false, name = "type_id", length = 20)
    private String type;

    @Column(name = "policy_title", length = 200)
    private String title;

    @Column(name = "use_at", columnDefinition = "boolean default true")
    private Boolean isUse;

    @Column(name = "reg_timestamp")
    private ZonedDateTime regDate;

    @Column(name = "policy_content", columnDefinition = "LONGTEXT")
    private String contents;

    @Builder
    public Policy(String type, String title, Boolean isUse, ZonedDateTime regDate, String contents) {
        this.type = type;
        this.title = title;
        this.isUse = isUse;
        this.regDate = regDate;
        this.contents = contents;
    }

    /**
     * 내용 수정
     *
     * @param title
     * @param isUse
     * @param contents
     * @return this
     */
    public Policy update(String title, Boolean isUse, String contents) {
        this.title = title;
        this.isUse = isUse;
        this.contents = contents;
        return this;
    }

    /**
     * 사용여부 변경
     *
     * @param isUse
     * @return this
     */
    public Policy updateIsUSe(Boolean isUse) {
        this.isUse = isUse;
        return this;
    }
}
