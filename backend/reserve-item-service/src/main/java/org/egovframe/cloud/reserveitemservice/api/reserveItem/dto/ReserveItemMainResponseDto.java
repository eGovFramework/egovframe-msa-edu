package org.egovframe.cloud.reserveitemservice.api.reserveItem.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.egovframe.cloud.reserveitemservice.domain.reserveItem.ReserveItem;

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
		this.isPossible = entity.isReservationPossible();
	}

}
