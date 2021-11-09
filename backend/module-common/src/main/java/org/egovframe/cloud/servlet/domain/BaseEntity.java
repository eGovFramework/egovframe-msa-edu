package org.egovframe.cloud.servlet.domain;

import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

/**
 * org.egovframe.cloud.servlet.domain.BaseTimeEntity
 * <p>
 * JPA Entity 클래스들이 BaseEntity 를 상속할 경우 createdBy, lastModifiedBy 필드들과
 * BaseTimeEntity 필드들(createdDate, modifiedDate)까지 컬럼으로 인식된다.
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
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class) // Auditing 기능 포함
public abstract class BaseEntity extends BaseTimeEntity {

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;
}
