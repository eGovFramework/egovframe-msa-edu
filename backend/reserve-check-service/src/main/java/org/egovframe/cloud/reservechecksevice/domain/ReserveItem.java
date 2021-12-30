package org.egovframe.cloud.reservechecksevice.domain;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.egovframe.cloud.reactive.domain.BaseEntity;
import org.egovframe.cloud.reservechecksevice.domain.location.Location;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;

/**
 * org.egovframe.cloud.reserveitemservice.domain.reserveItem.ReserveItem
 *
 * 예약 물품 도메인 클래스
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/09/09
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/09    shinmj       최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
@ToString
public class ReserveItem extends BaseEntity {

    @Id
    @Column("reserve_item_id")
    private Long reserveItemId;    // 예약 물품 id

    @Size(max = 200)
    @NotNull
    @Column("reserve_item_name")
    private String reserveItemName;    //예약 물품 명

    @ToString.Exclude
    private Long locationId;

    @ToString.Exclude
    @Transient
    private Location location;  //지역

    @Size(max = 20)
    @NotNull
    @Column
    private String categoryId;  //예약유형 - 공통코드 reserve-category

    @Transient
    private String categoryName;

    @Size(max = 5)
    @NotNull
    @Column
    private Integer totalQty;   //총 재고/수용인원 수

    @Size(max = 5)
    @Column
    private Integer inventoryQty;   //현재 재고/수용인원 수

    @Column
    private LocalDateTime operationStartDate;   //운영 시작 일

    @Column
    private LocalDateTime operationEndDate;     //운영 종료 일

    @Size(max = 20)
    @NotNull
    @Column
    private String reserveMethodId; // 예약 방법 - 공통코드 reserve-method

    @Transient
    private String reserveMethodName;

    @Size(max = 20)
    @Column
    private String reserveMeansId; // 예약 구분 (인터넷 예약 시) - 공통코드 reserve-means

    @Transient
    private String reserveMeansName;

    @Column
    private LocalDateTime requestStartDate;     //예약 신청 시작 일시

    @Column
    private LocalDateTime requestEndDate;       //예약 신청 종료 일시

    @Column("period_at")
    private Boolean isPeriod;       //기간 지정 가능 여부 - true: 지정 가능, false: 지정 불가

    @Size(max = 3)
    @Column
    private Integer periodMaxCount;    // 최대 예약 가능 일 수

    @Size(max = 500)
    @Column
    private String externalUrl;     //외부링크

    @Size(max = 20)
    @NotNull
    @Column
    private String selectionMeansId;       //선별 방법 - 공통코드 reserve-selection-means

    @Transient
    private String selectionMeansName;

    @Column("paid_at")
    private Boolean isPaid;         // 유/무료 - false: 무료, true: 유료

    @Column
    private BigDecimal usageCost;          //이용 요금

    @Column("use_at")
    private Boolean isUse;          //사용여부

    @Size(max = 4000)
    @Column("purpose_content")
    private String purpose;         //용도

    @Size(max = 500)
    @Column("item_addr")
    private String address;         //주소

    @Size(max = 20)
    @Column
    private String targetId;        //이용 대상 - 공통코드 reserve-target

    @Transient
    private String targetName;

    @Size(max = 2000)
    @Column("excluded_content")
    private String excluded;        // 사용허가 제외대상

    @Size(max = 500)
    @Column("homepage_url")
    private String homepage;        //홈페이지 주소

    @Size(max = 50)
    @Column("contact_no")
    private String contact;         //문의처

    @Size(max = 200)
    @Column("manager_dept_name")
    private String managerDept;     //담당자 소속

    @Size(max = 200)
    @Column("manager_name")
    private String managerName;     //담당자 이름

    @Size(max = 50)
    @Column("manager_contact_no")
    private String managerContact;  //담당자 연락처

    @Builder
    public ReserveItem(Long reserveItemId, String reserveItemName, Long locationId, Location location, String categoryId, String categoryName, Integer totalQty, Integer inventoryQty, LocalDateTime operationStartDate, LocalDateTime operationEndDate, String reserveMethodId, String reserveMethodName, String reserveMeansId, String reserveMeansName, LocalDateTime requestStartDate, LocalDateTime requestEndDate, Boolean isPeriod, Integer periodMaxCount, String externalUrl, String selectionMeansId, String selectionMeansName, Boolean isPaid, BigDecimal usageCost, Boolean isUse, String purpose, String address, String targetId, String targetName, String excluded, String homepage, String contact, String managerDept, String managerName, String managerContact) {
        this.reserveItemId = reserveItemId;
        this.reserveItemName = reserveItemName;
        this.locationId = locationId;
        this.location = location;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.totalQty = totalQty;
        this.inventoryQty = inventoryQty;
        this.operationStartDate = operationStartDate;
        this.operationEndDate = operationEndDate;
        this.reserveMethodId = reserveMethodId;
        this.reserveMethodName = reserveMethodName;
        this.reserveMeansId = reserveMeansId;
        this.reserveMeansName = reserveMeansName;
        this.requestStartDate = requestStartDate;
        this.requestEndDate = requestEndDate;
        this.isPeriod = isPeriod;
        this.periodMaxCount = periodMaxCount;
        this.externalUrl = externalUrl;
        this.selectionMeansId = selectionMeansId;
        this.selectionMeansName = selectionMeansName;
        this.isPaid = isPaid;
        this.usageCost = usageCost;
        this.isUse = isUse;
        this.purpose = purpose;
        this.address = address;
        this.targetId = targetId;
        this.targetName = targetName;
        this.excluded = excluded;
        this.homepage = homepage;
        this.contact = contact;
        this.managerDept = managerDept;
        this.managerName = managerName;
        this.managerContact = managerContact;
    }
}


