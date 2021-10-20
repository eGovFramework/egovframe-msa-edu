package org.egovframe.cloud.reserveitemservice.api.reserveItem.dto;

import java.time.LocalDateTime;

import org.egovframe.cloud.reserveitemservice.domain.reserveItem.ReserveItem;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class ReserveItemMainResponseDto {

	private Long reserveItemId;    // 예약 물품 id
	private String reserveItemName;    //예약 물품 명
	private String categoryId;  //예약유형 - 공통코드 reserve-category
	private String categoryName;
	private LocalDateTime startDate;   //운영 시작 일 or 예약 신청 시작일
	private LocalDateTime endDate;     //운영 종료 일 or 예약 신청 종료일
	private Boolean isPossible;

	@Builder
	public ReserveItemMainResponseDto (ReserveItem entity) {
		this.reserveItemId = entity.getReserveItemId();
		this.reserveItemName = entity.getReserveItemName();
		this.categoryId = entity.getCategoryId();
		this.categoryName = entity.getCategoryName();
		this.startDate = entity.getOperationStartDate();
		this.endDate = entity.getOperationEndDate();
		if (entity.getReserveMethodId().equals("internet")) {
			if (entity.getReserveMeansId().equals("realtime")) {
				this.startDate = entity.getRequestStartDate();
				this.endDate = entity.getRequestEndDate();
			}
		}
		this.isPossible = isReservationPossible(entity);
	}

	/**
	 * 예약 가능 여부 체크
	 *
	 * @param entity
	 * @return
	 */
	private boolean isReservationPossible(ReserveItem entity) {
		LocalDateTime now = LocalDateTime.now();
		if (!entity.getIsUse()) {
			return false;
		}

		if (entity.getInventoryQty() <= 0) {
			return false;
		}

		if (entity.getIsPeriod()) {
			if (entity.getRequestStartDate().isBefore(now) && entity.getRequestEndDate().isAfter(now)) {
				return true;
			}else {
				return false;
			}
		} else {
			if (entity.getOperationStartDate().isBefore(now) && entity.getOperationEndDate().isAfter(now)) {
				return true;
			}else {
				return false;
			}
		}
	}

}
