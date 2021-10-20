package org.egovframe.cloud.userservice.domain.role;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * org.egovframe.cloud.userservice.domain.role.RoleAuthorization
 * <p>
 * 권한 인가 엔티티 클래스
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
@Entity
@EntityListeners(AuditingEntityListener.class) // Auditing 기능 포함
public class RoleAuthorization {

    /**
     * 권한 인가 복합키
     */
    @EmbeddedId
    private RoleAuthorizationId roleAuthorizationId;

    /**
     * 인가 엔티티
     */
    @MapsId("authorizationNo") // RoleAuthorizationId.authorizationNo 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authorization_no")
    private Authorization authorization;

    /**
     * 생성자 id
     */
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    /**
     * 생성 일시
     */
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    /**
     * 빌드 패턴 클래스 생성자
     *
     * @param roleId          권한 id
     * @param authorizationNo 인가 번호
     */
    @Builder
    public RoleAuthorization(String roleId, Integer authorizationNo) {
        this.roleAuthorizationId = RoleAuthorizationId.builder()
                .roleId(roleId)
                .authorizationNo(authorizationNo)
                .build();
        this.authorization = Authorization.builder()
                .authorizationNo(authorizationNo).build();
    }

}
