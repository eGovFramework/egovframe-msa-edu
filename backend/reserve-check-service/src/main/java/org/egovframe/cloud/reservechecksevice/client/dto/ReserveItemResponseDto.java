package org.egovframe.cloud.reservechecksevice.client.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.egovframe.cloud.reservechecksevice.domain.ReserveItem;

/**
 * org.egovframe.cloud.reserveitemservice.api.reserveItem.dto.ReserveItemResponseDto
 * <p>
 * 예약 물품 응답 dto class
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
public class ReserveItemResponseDto {
    private static final int MIN_QTY = 0;

    private Long reserveItemId;    // 예약 물품 id
    private String reserveItemName;    //예약 물품 명
    private Long locationId;
    private String categoryId;  //예약유형 - 공통코드 reserve-category
    private Integer totalQty;   //총 재고/수용인원 수
    private Integer inventoryQty;   // 재고/수용인원 수
    private LocalDateTime operationStartDate;   //운영 시작 일
    private LocalDateTime operationEndDate;     //운영 종료 일
    private String reserveMethodId; // 예약 방법 - 공통코드 reserve-method
    private String reserveMeansId; // 예약 구분 (인터넷 예약 시) - 공통코드 reserve-means
    private LocalDateTime requestStartDate;     //예약 신청 시작 일시
    private LocalDateTime requestEndDate;       //예약 신청 종료 일시
    private Boolean isPeriod;       //기간 지정 가능 여부 - true: 지정 가능, false: 지정 불가
    private Integer periodMaxCount;    // 최대 예약 가능 일 수
    private String externalUrl;     //외부링크
    private String selectionMeansId;       //선별 방법 - 공통코드 reserve-selection
    private Boolean isPaid;         // 유/무료 - true: 무료, false: 유료
    private BigDecimal usageCost;          //이용 요금
    private Boolean isUse;          //사용여부
    private String purpose;         //용도
    private String address;         //주소
    private String targetId;        //이용 대상 - 공통코드 reserve-target
    private String excluded;        // 사용허가 제외대상
    private String homepage;        //홈페이지 주소
    private String contact;         //문의처
    private String managerDept;     //담당자 소속
    private String managerName;     //담당자 이름
    private String managerContact;  //담당자 연락처

    @Builder
    public ReserveItemResponseDto(ReserveItem reserveItem) {
        this.reserveItemId = reserveItem.getReserveItemId();
        this.reserveItemName = reserveItem.getReserveItemName();
        this.locationId = reserveItem.getLocationId();
        this.categoryId = reserveItem.getCategoryId();
        this.totalQty = reserveItem.getTotalQty();
        this.inventoryQty = reserveItem.getInventoryQty();
        this.operationStartDate = reserveItem.getOperationStartDate();
        this.operationEndDate = reserveItem.getOperationEndDate();
        this.reserveMethodId = reserveItem.getReserveMethodId();
        this.reserveMeansId = reserveItem.getReserveMeansId();
        this.requestStartDate = reserveItem.getRequestStartDate();
        this.requestEndDate = reserveItem.getRequestEndDate();
        this.isPeriod = reserveItem.getIsPeriod();
        this.periodMaxCount = reserveItem.getPeriodMaxCount();
        this.externalUrl = reserveItem.getExternalUrl();
        this.selectionMeansId = reserveItem.getSelectionMeansId();
        this.isPaid = reserveItem.getIsPaid();
        this.usageCost = reserveItem.getUsageCost();
        this.isUse = reserveItem.getIsUse();
        this.purpose = reserveItem.getPurpose();
        this.address = reserveItem.getAddress();
        this.targetId = reserveItem.getTargetId();
        this.excluded = reserveItem.getExcluded();
        this.homepage = reserveItem.getHomepage();
        this.contact = reserveItem.getContact();
        this.managerDept = reserveItem.getManagerDept();
        this.managerName = reserveItem.getManagerName();
        this.managerContact = reserveItem.getManagerContact();
    }

    public boolean isPossibleQty(Integer max, Integer reserveQty) {
        return (totalQty - max) >= reserveQty;
    }

    public boolean isPositiveInventory() {
        return inventoryQty > MIN_QTY;
    }

    public boolean isPossibleInventoryQty(Integer reserveQty) {
        return inventoryQty >= reserveQty;
    }
}
