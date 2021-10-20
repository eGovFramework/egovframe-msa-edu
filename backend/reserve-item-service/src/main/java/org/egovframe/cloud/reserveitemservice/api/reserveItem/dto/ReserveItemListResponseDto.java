package org.egovframe.cloud.reserveitemservice.api.reserveItem.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.egovframe.cloud.reserveitemservice.domain.reserveItem.ReserveItem;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;


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
    public ReserveItemListResponseDto(ReserveItem reserveItem) {
        this.reserveItemId = reserveItem.getReserveItemId();
        this.reserveItemName = reserveItem.getReserveItemName();
        this.locationId = reserveItem.getLocationId();
        this.locationName = reserveItem.getLocation().getLocationName();
        this.categoryId = reserveItem.getCategoryId();
        this.categoryName = reserveItem.getCategoryName();
        this.totalQty = reserveItem.getTotalQty();
        this.inventoryQty = reserveItem.getInventoryQty();
        this.isUse = reserveItem.getIsUse();
        this.createDate = reserveItem.getCreateDate();
        this.isPossible = isReservationPossible(reserveItem);
    }

    /**
     * 예약 가능 여부 체크
     *
     * @param reserveItem
     * @return
     */
    private boolean isReservationPossible(ReserveItem reserveItem) {
        LocalDateTime now = LocalDateTime.now();
        if (!reserveItem.getIsUse()) {
            return false;
        }

        if (reserveItem.getInventoryQty() <= 0) {
            return false;
        }

        if (reserveItem.getIsPeriod()) {
            if (reserveItem.getRequestStartDate().isBefore(now) && reserveItem.getRequestEndDate().isAfter(now)) {
                return true;
            }else {
                return false;
            }
        } else {
            if (reserveItem.getOperationStartDate().isBefore(now) && reserveItem.getOperationEndDate().isAfter(now)) {
                return true;
            }else {
                return false;
            }
        }
    }

}
