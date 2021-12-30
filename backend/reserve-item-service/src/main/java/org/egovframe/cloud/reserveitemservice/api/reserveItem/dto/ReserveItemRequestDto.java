package org.egovframe.cloud.reserveitemservice.api.reserveItem.dto;

import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.egovframe.cloud.common.dto.RequestDto;

/**
 * org.egovframe.cloud.reserveitemservice.api.reserveItem.dto.ReserveItemRequestDto
 * <p>
 * 예약 목록 조회 요청 dto class
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/09/27
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/27    shinmj      최초 생성
 * </pre>
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ReserveItemRequestDto extends RequestDto {
    private Long locationId;
    private String categoryId;
    private Boolean isUse;
    private Boolean isPopup;

    public boolean hasLocationId() {
        return hasId(locationId);
    }

    public boolean hasCategoryId() {
        return hasId(categoryId);
    }

    private boolean hasId(Object id) {
        return Objects.nonNull(id) && !Objects.equals("null", id) && !Objects.equals("undefined", id);
    }

    public boolean isPopup() {
        return Boolean.TRUE.equals(isPopup);
    }
}
