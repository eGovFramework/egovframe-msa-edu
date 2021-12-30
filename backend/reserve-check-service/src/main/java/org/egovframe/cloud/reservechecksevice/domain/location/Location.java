package org.egovframe.cloud.reservechecksevice.domain.location;

import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.egovframe.cloud.reactive.domain.BaseEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

/**
 * org.egovframe.cloud.reserveitemservice.domain.location.Location
 *
 * 예약 지역 도메인 클래스
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/09/06
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/06    shinmj       최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
@ToString
public class Location extends BaseEntity {

    @Id
    private Long locationId;

    @Size(max = 200)
    @Column
    private String locationName;
    @Column
    private Integer sortSeq;

    @Column("use_at")
    private Boolean isUse;

    @Builder
    public Location(Long locationId, String locationName, Integer sortSeq, Boolean isUse) {
        this.locationId = locationId;
        this.locationName = locationName;
        this.sortSeq = sortSeq;
        this.isUse = isUse;
    }
}
