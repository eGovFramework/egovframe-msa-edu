package org.egovframe.cloud.reserveitemservice.api.location.dto;

import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.egovframe.cloud.reserveitemservice.domain.location.Location;
/**
 * org.egovframe.cloud.reserveitemservice.api.location.dto.LocationUpdateRequestDto
 * <p>
 * 예약 지역 수정 요청 dto class
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/09/08
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/08    shinmj      최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
@ToString
public class LocationUpdateRequestDto {
    @NotNull
    private String locationName;
    private Integer sortSeq;
    private Boolean isUse;

    @Builder
    public LocationUpdateRequestDto(String locationName, Integer sortSeq, Boolean isUse) {
        this.locationName = locationName;
        this.sortSeq = sortSeq;
        this.isUse = isUse;
    }

    /**
     * dto -> entity
     *
     * @return
     */
    public Location toEntity() {
        return Location.builder()
                .locationName(this.locationName)
                .sortSeq(this.sortSeq)
                .isUse(this.isUse)
                .build();
    }
}
