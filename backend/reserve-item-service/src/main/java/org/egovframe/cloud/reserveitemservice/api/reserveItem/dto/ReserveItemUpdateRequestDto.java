package org.egovframe.cloud.reserveitemservice.api.reserveItem.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.egovframe.cloud.reserveitemservice.domain.reserveItem.ReserveItem;
import org.egovframe.cloud.reserveitemservice.validator.annotation.ReserveItemSaveValid;

/**
 * org.egovframe.cloud.reserveitemservice.api.reserveItem.dto.ReserveItemUpdateRequestDto
 * <p>
 * 예약 물품 수정 요청 dto class
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
@ReserveItemSaveValid
public class ReserveItemUpdateRequestDto {
    @NotBlank
    @Size(max = 200)
    private String reserveItemName;    //예약 물품 명
    @NotNull
    private Long locationId;
    @NotBlank
    private String categoryId;  //예약유형 - 공통코드 reserve-category
    @NotNull
    @PositiveOrZero
    private Integer totalQty;   //총 재고/수용인원 수
    @NotNull
    @PositiveOrZero
    private Integer inventoryQty;   //재고/수용인원 수
    @NotNull
    private LocalDateTime operationStartDate;   //운영 시작 일
    @NotNull
    private LocalDateTime operationEndDate;     //운영 종료 일
    @NotBlank
    private String reserveMethodId; // 예약 방법 - 공통코드 reserve-method
    private String reserveMeansId; // 예약 구분 (인터넷 예약 시) - 공통코드 reserve-means
    private LocalDateTime requestStartDate;     //예약 신청 시작 일시
    private LocalDateTime requestEndDate;       //예약 신청 종료 일시
    private Boolean isPeriod;       //기간 지정 가능 여부 - true: 지정 가능, false: 지정 불가
    private Integer periodMaxCount;    // 최대 예약 가능 일 수
    @Size(max = 500)
    private String externalUrl;     //외부링크
    @NotBlank
    private String selectionMeansId;       //선별 방법 - 공통코드 reserve-selection
    private Boolean isPaid;         // 유/무료 - false: 무료, true: 유료
    private BigDecimal usageCost;          //이용 요금
    private Boolean isUse;          //사용여부
    @Size(max = 4000)
    private String purpose;         //용도
    @Size(max = 500)
    private String address;         //주소
    private String targetId;        //이용 대상 - 공통코드 reserve-target
    @Size(max = 2000)
    private String excluded;        // 사용허가 제외대상
    @Size(max = 500)
    private String homepage;        //홈페이지 주소
    @Size(max = 50)
    private String contact;         //문의처
    @Size(max = 200)
    private String managerDept;     //담당자 소속
    @Size(max = 200)
    private String managerName;     //담당자 이름
    @Size(max = 50)
    private String managerContact;  //담당자 연락처

    @Builder
    public ReserveItemUpdateRequestDto(String reserveItemName, Long locationId, String categoryId,
        Integer totalQty, Integer inventoryQty, LocalDateTime operationStartDate, LocalDateTime operationEndDate,
        String reserveMethodId, String reserveMeansId, LocalDateTime requestStartDate,
        LocalDateTime requestEndDate, Boolean isPeriod, Integer periodMaxCount, String externalUrl,
        String selectionMeansId, Boolean isPaid, BigDecimal usageCost, Boolean isUse, String purpose,
        String address, String targetId, String excluded, String homepage, String contact, String managerDept,
        String managerName, String managerContact) {
        this.reserveItemName = reserveItemName;
        this.locationId = locationId;
        this.categoryId = categoryId;
        this.totalQty = totalQty;
        this.inventoryQty = inventoryQty;
        this.operationStartDate = operationStartDate;
        this.operationEndDate = operationEndDate;
        this.reserveMethodId = reserveMethodId;
        this.reserveMeansId = reserveMeansId;
        this.requestStartDate = requestStartDate;
        this.requestEndDate = requestEndDate;
        this.isPeriod = isPeriod;
        this.periodMaxCount = periodMaxCount;
        this.externalUrl = externalUrl;
        this.selectionMeansId = selectionMeansId;
        this.isPaid = isPaid;
        this.usageCost = usageCost;
        this.isUse = isUse;
        this.purpose = purpose;
        this.address = address;
        this.targetId = targetId;
        this.excluded = excluded;
        this.homepage = homepage;
        this.contact = contact;
        this.managerDept = managerDept;
        this.managerName = managerName;
        this.managerContact = managerContact;
    }

    public ReserveItem toEntity() {
        return ReserveItem.builder()
                .reserveItemName(this.reserveItemName)
                .locationId(this.locationId)
                .categoryId(this.categoryId)
                .totalQty(this.totalQty)
                .inventoryQty(this.inventoryQty)
                .operationStartDate(this.operationStartDate)
                .operationEndDate(this.operationEndDate)
                .reserveMethodId(this.reserveMethodId)
                .reserveMeansId(this.reserveMeansId)
                .requestStartDate(this.requestStartDate)
                .requestEndDate(this.requestEndDate)
                .isPeriod(this.isPeriod)
                .periodMaxCount(this.periodMaxCount)
                .externalUrl(this.externalUrl)
                .selectionMeansId(this.selectionMeansId)
                .isPaid(this.isPaid)
                .usageCost(this.usageCost)
                .isUse(this.isUse)
                .purpose(this.purpose)
                .address(this.address)
                .targetId(this.targetId)
                .excluded(this.excluded)
                .homepage(this.homepage)
                .contact(this.contact)
                .managerDept(this.managerDept)
                .managerName(this.managerName)
                .managerContact(this.managerContact)
                .build();
    }
}
