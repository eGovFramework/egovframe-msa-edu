package org.egovframe.cloud.userservice.domain.role;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * org.egovframe.cloud.userservice.domain.role.RoleAuthorizationId
 * <p>
 * 권한 인가 엔티티 복합키 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/09
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/09    jooho       최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
@Embeddable
public class RoleAuthorizationId implements Serializable {

    /**
     * SerialVersionUID
     */
    private static final long serialVersionUID = 7191831905023135716L;

    /**
     * 권한 id
     */
    @Column(length = 20)
    private String roleId;

    /**
     * 인가 번호
     */
    private Integer authorizationNo; // @MapsId("authorizationNo")로 매핑

    /**
     * 빌드 패턴 클래스 생성자
     *
     * @param roleId          권한 id
     * @param authorizationNo 인가 번호
     */
    @Builder
    public RoleAuthorizationId(String roleId, Integer authorizationNo) {
        this.roleId = roleId;
        this.authorizationNo = authorizationNo;
    }

    /**
     * Returns a hash code value for the object. This method is supported for the benefit of hash tables such as those provided by java.util.HashMap.
     *
     * @return int a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(roleId, authorizationNo);
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param   object   the reference object with which to compare.
     * @return  {@code true} if this object is the same as the obj
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof RoleAuthorizationId)) return false;
        RoleAuthorizationId that = (RoleAuthorizationId) object;
        return Objects.equals(roleId, that.getRoleId()) &&
                Objects.equals(authorizationNo, that.getAuthorizationNo());
    }

}
