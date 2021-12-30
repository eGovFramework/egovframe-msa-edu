package org.egovframe.cloud.reservechecksevice.api.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.egovframe.cloud.reservechecksevice.domain.Reserve;

/**
 * org.egovframe.cloud.reservechecksevice.api.dto.ReserveListResponseDto
 * <p>
 * 예약 목록 응답 dto class
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/09/17
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/17    shinmj      최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
@ToString
public class ReserveListResponseDto {

    private String reserveId;

    private Long locationId;
    private String categoryId;
    private Long reserveItemId;
    private String reserveItemName;
    private Integer totalQty;
    private Integer reserveQty;

    private String userId;
    private String userName;

    private String reserveStatusId;
    private LocalDateTime createDate;

    @Builder
    public ReserveListResponseDto(Reserve entity) {
        this.reserveId = entity.getReserveId();
        this.locationId = entity.getLocationId();
        this.categoryId = entity.getCategoryId();
        this.reserveItemId = entity.getReserveItemId();
        this.reserveItemName = entity.getReserveItem().getReserveItemName();
        this.totalQty = entity.getReserveItem().getTotalQty();
        this.reserveQty = entity.getReserveQty();
        this.userId = entity.getUserId();
        this.userName = entity.getUser().getUserName();
        this.reserveStatusId = entity.getReserveStatusId();
        this.createDate = entity.getCreateDate();
    }

}


