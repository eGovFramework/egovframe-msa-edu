package org.egovframe.cloud.boardservice.domain.user;

import java.io.Serializable;

import org.egovframe.cloud.servlet.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * org.egovframe.cloud.boardservice.domain.user.User
 * <p>
 * 사용자 정보 엔티티
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/06/30
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/06/30    jaeyeolkim  최초 생성
 * </pre>
 */
@NoArgsConstructor
@Entity
@Getter
public class User extends BaseEntity implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 8328774953218952698L;

    @Id
    @Column(name = "user_no", insertable = false, updatable = false)
    private Long id;

    @Column(insertable = false, updatable = false)
    private String userId;

    @Column(insertable = false, updatable = false)
    private String userName;

}
