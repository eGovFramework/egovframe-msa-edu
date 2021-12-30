package org.egovframe.cloud.reservechecksevice.client.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.egovframe.cloud.reservechecksevice.domain.ReserveItem;
import org.egovframe.cloud.reservechecksevice.domain.location.Location;

/**
 * org.egovframe.cloud.reservechecksevice.client.dto.ReserveItemRelationResponseDto
 * <p>
 * 얘약 물품 feign client 응답 dto class
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
@AllArgsConstructor
@Getter
@ToString
public class ReserveItemRelationResponseDto {
    private Long reserveItemId;    // 예약 물품 id
    private String reserveItemName;    //예약 물품 명
    private Long locationId;
    private Location location;
    private String categoryId;  //예약유형 - 공통코드 reserve-category
    private String categoryName;
    private Integer totalQty;   //총 재고/수용인원 수
    private Integer inventoryQty;   // 재고/수용인원 수
    private LocalDateTime operationStartDate;   //운영 시작 일
    private LocalDateTime operationEndDate;     //운영 종료 일
    private String reserveMethodId; // 예약 방법 - 공통코드 reserve-method
    private String reserveMethodName;
    private String reserveMeansId; // 예약 구분 (인터넷 예약 시) - 공통코드 reserve-means
    private String reserveMeansName;
    private LocalDateTime requestStartDate;     //예약 신청 시작 일시
    private LocalDateTime requestEndDate;       //예약 신청 종료 일시
    private Boolean isPeriod;       //기간 지정 가능 여부 - true: 지정 가능, false: 지정 불가
    private Integer periodMaxCount;    // 최대 예약 가능 일 수
    private String externalUrl;     //외부링크
    private String selectionMeansId;       //선별 방법 - 공통코드 reserve-selection
    private String selectionMeansName;
    private Boolean isPaid;         // 유/무료 - false: 무료, true: 유료
    private BigDecimal usageCost;          //이용 요금
    private Boolean isUse;          //사용여부
    private String purpose;         //용도
    private String address;         //주소
    private String targetId;        //이용 대상 - 공통코드 reserve-target
    private String targetName;
    private String excluded;        // 사용허가 제외대상
    private String homepage;        //홈페이지 주소
    private String contact;         //문의처
    private String managerDept;     //담당자 소속
    private String managerName;     //담당자 이름
    private String managerContact;  //담당자 연락처

    @Builder
    public ReserveItemRelationResponseDto(ReserveItem entity) {
        this.reserveItemId = entity.getReserveItemId();
        this.reserveItemName = entity.getReserveItemName();
        this.locationId = entity.getLocationId();
        this.location = entity.getLocation();
        this.categoryId = entity.getCategoryId();
        this.categoryName = entity.getCategoryName();
        this.totalQty = entity.getTotalQty();
        this.inventoryQty = entity.getInventoryQty();
        this.operationStartDate = entity.getOperationStartDate();
        this.operationEndDate = entity.getOperationEndDate();
        this.reserveMethodId = entity.getReserveMethodId();
        this.reserveMethodName = entity.getReserveMethodName();
        this.reserveMeansId = entity.getReserveMeansId();
        this.reserveMeansName = entity.getReserveMeansName();
        this.requestStartDate = entity.getRequestStartDate();
        this.requestEndDate = entity.getRequestEndDate();
        this.isPeriod = entity.getIsPeriod();
        this.periodMaxCount = entity.getPeriodMaxCount();
        this.externalUrl = entity.getExternalUrl();
        this.selectionMeansId = entity.getSelectionMeansId();
        this.selectionMeansName = entity.getSelectionMeansName();
        this.isPaid = entity.getIsPaid();
        this.usageCost = entity.getUsageCost();
        this.isUse = entity.getIsUse();
        this.purpose = entity.getPurpose();
        this.address = entity.getAddress();
        this.targetId = entity.getTargetId();
        this.targetName = entity.getTargetName();
        this.excluded = entity.getExcluded();
        this.homepage = entity.getHomepage();
        this.contact = entity.getContact();
        this.managerDept = entity.getManagerDept();
        this.managerName = entity.getManagerName();
        this.managerContact = entity.getManagerContact();
    }

    public ReserveItem toEntity() {
        return ReserveItem.builder()
                .reserveItemName(this.reserveItemName)
                .locationId(this.locationId)
                .location(this.location)
                .categoryId(this.categoryId)
                .categoryName(this.categoryName)
                .totalQty(this.totalQty)
                .inventoryQty(this.inventoryQty)
                .operationStartDate(this.operationStartDate)
                .operationEndDate(this.operationEndDate)
                .reserveMethodId(this.reserveMethodId)
                .reserveMethodName(this.reserveMethodName)
                .reserveMeansId(this.reserveMeansId)
                .reserveMeansName(this.reserveMeansName)
                .requestStartDate(this.requestStartDate)
                .requestEndDate(this.requestEndDate)
                .isPeriod(this.isPeriod)
                .periodMaxCount(this.periodMaxCount)
                .externalUrl(this.externalUrl)
                .selectionMeansId(this.selectionMeansId)
                .selectionMeansName(this.selectionMeansName)
                .isPaid(this.isPaid)
                .usageCost(this.usageCost)
                .isUse(this.isUse)
                .purpose(this.purpose)
                .address(this.address)
                .targetId(this.targetId)
                .targetName(this.targetName)
                .excluded(this.excluded)
                .homepage(this.homepage)
                .contact(this.contact)
                .managerDept(this.managerDept)
                .managerName(this.managerName)
                .managerContact(this.managerContact)
                .build();
    }
}
