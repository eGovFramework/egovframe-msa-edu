package org.egovframe.cloud.reserveitemservice.api.location.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.egovframe.cloud.reserveitemservice.domain.location.Location;

/**
 * org.egovframe.cloud.reserveitemservice.api.location.dto.LocationResponseDto
 * <p>
 * 예약 지역 응답 dto class
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
 *  2021/09/06    shinmj      최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
@ToString
public class LocationResponseDto {
    private Long locationId;
    private String locationName;
    private Integer sortSeq;
    private Boolean isUse;
    private LocalDateTime createDate;

    @Builder
    public LocationResponseDto(Location entity) {
        this.locationId = entity.getLocationId();
        this.locationName = entity.getLocationName();
        this.sortSeq = entity.getSortSeq();
        this.isUse = entity.getIsUse();
        this.createDate = entity.getCreateDate();
    }
}
