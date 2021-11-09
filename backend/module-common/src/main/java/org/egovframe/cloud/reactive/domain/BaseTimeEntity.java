package org.egovframe.cloud.reactive.domain;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

/**
 * org.egovframe.cloud.servlet.domain.BaseTimeEntity
 * <p>
 * JPA Entity 클래스들이 BaseTimeEntity을 상속할 경우 필드들(createdDate, modifiedDate)도 컬럼으로 인식된다.
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
public abstract class BaseTimeEntity {
    @CreatedDate
    protected LocalDateTime createDate;

    @LastModifiedDate
    protected LocalDateTime modifiedDate;

}
