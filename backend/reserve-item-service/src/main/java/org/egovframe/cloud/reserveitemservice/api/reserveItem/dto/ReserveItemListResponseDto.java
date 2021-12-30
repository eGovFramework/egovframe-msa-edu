package org.egovframe.cloud.reserveitemservice.api.reserveItem.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.egovframe.cloud.reserveitemservice.domain.reserveItem.ReserveItem;


/**
 * org.egovframe.cloud.reserveitemservice.api.reserveItem.dto.ReserveItemListResponseDto
 * <p>
 * 예약 물품 목록 응답 dto class
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/09/13
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/13    shinmj      최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
@ToString
public class ReserveItemListResponseDto {
    private Long reserveItemId;                    // 예약 물품 id
    private String reserveItemName;                //예약 물품 명
    private Long locationId;            //지역 id
    private String locationName;
    private String categoryId;          //예약유형 - 공통코드 reserve-category
    private String categoryName;
    private Integer totalQty;           //총 재고/수용인원 수
    private Integer inventoryQty;           //총 재고/수용인원 수
    private Boolean isUse;              //사용여부
    private LocalDateTime createDate;   //등록일
    private Boolean isPossible;         //예약 가능 여부

    @Builder
    public ReserveItemListResponseDto(ReserveItem entity) {
        this.reserveItemId = entity.getReserveItemId();
        this.reserveItemName = entity.getReserveItemName();
        this.locationId = entity.getLocationId();
        this.locationName = entity.getLocation().getLocationName();
        this.categoryId = entity.getCategoryId();
        this.categoryName = entity.getCategoryName();
        this.totalQty = entity.getTotalQty();
        this.inventoryQty = entity.getInventoryQty();
        this.isUse = entity.getIsUse();
        this.createDate = entity.getCreateDate();
        this.isPossible = entity.isReservationPossible();
    }

}
