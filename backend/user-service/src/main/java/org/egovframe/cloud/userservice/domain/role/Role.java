package org.egovframe.cloud.userservice.domain.role;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * org.egovframe.cloud.userservice.domain.role.Role
 * <p>
 * 권한 엔티티 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/07
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/07    jooho       최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class) // Auditing 기능 포함
public class Role {

    /**
     * 권한 id
     */
    @Id
    @Column(nullable = false, length = 20, unique = true)
    private String roleId;

    /**
     * 권한 명
     */
    @Column(nullable = false, length = 50)
    private String roleName;

    /**
     * 권한 내용
     */
    @Column(length = 200)
    private String roleContent;

    /**
     * 정렬 순서
     */
    @Column
    private Integer sortSeq;

    /**
     * 생성 일시
     */
    @CreatedDate
    @Column
    private LocalDateTime createdDate;

    /**
     * 빌드 패턴 클래스 생성자
     *
     * @param roleId      권한 id
     * @param roleName    권한 명
     * @param roleContent 권한 내용
     * @param sortSeq     정렬 순서
     */
    @Builder
    public Role(String roleId, String roleName, String roleContent, Integer sortSeq) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.roleContent = roleContent;
        this.sortSeq = sortSeq;
    }

}
