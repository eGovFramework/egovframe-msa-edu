package org.egovframe.cloud.reserverequestservice.api.dto;

import java.util.UUID;
import lombok.*;
import org.egovframe.cloud.reserverequestservice.domain.Reserve;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import org.egovframe.cloud.reserverequestservice.domain.ReserveStatus;

/**
 * org.egovframe.cloud.reserverequestservice.api.dto.ReserveSaveRequestDto
 * <p>
 * 예약 신청 저장 요청 dto class
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
public class ReserveSaveRequestDto {

    @Setter
    private String reserveId;
    @NotNull
    private Long reserveItemId;
    private Long locationId;
    private String categoryId;
    private Integer totalQty;
    private String reserveMethodId;
    private String reserveMeansId;
    private LocalDateTime operationStartDate;
    private LocalDateTime operationEndDate;
    private LocalDateTime requestStartDate;
    private LocalDateTime requestEndDate;
    private Boolean isPeriod;
    private Integer periodMaxCount;

    private Integer reserveQty;    //예약 신청 인원/수량
    @NotNull
    private String reservePurposeContent;   //예약 목적
    private String attachmentCode;  //첨부파일 코드
    private LocalDateTime reserveStartDate; //예약 신청 시작일
    private LocalDateTime reserveEndDate; //예약 신청 종료일
    @Setter
    private String reserveStatusId;     //예약상태 - 공통코드(reserve-status)
    @NotNull
    private String userId;  //예약자
    @NotNull
    private String userContactNo;   //예약자 연락처
    @NotNull
    private String userEmail;   //예약자 이메일

    @Builder
    public ReserveSaveRequestDto(String reserveId, Long reserveItemId, Long locationId, String categoryId,
        Integer totalQty, String reserveMethodId, String reserveMeansId, LocalDateTime operationStartDate,
        LocalDateTime operationEndDate, LocalDateTime requestStartDate, LocalDateTime requestEndDate,
        Boolean isPeriod, Integer periodMaxCount, Integer reserveQty, String reservePurposeContent,
        String attachmentCode, LocalDateTime reserveStartDate, LocalDateTime reserveEndDate,
        String reserveStatusId, String userId, String userContactNo, String userEmail) {
        this.reserveId = reserveId;
        this.reserveItemId = reserveItemId;
        this.locationId = locationId;
        this.categoryId = categoryId;
        this.totalQty = totalQty;
        this.reserveMethodId = reserveMethodId;
        this.reserveMeansId = reserveMeansId;
        this.operationStartDate = operationStartDate;
        this.operationEndDate = operationEndDate;
        this.requestStartDate = requestStartDate;
        this.requestEndDate = requestEndDate;
        this.isPeriod = isPeriod;
        this.periodMaxCount = periodMaxCount;
        this.reserveQty = reserveQty;
        this.reservePurposeContent = reservePurposeContent;
        this.attachmentCode = attachmentCode;
        this.reserveStartDate = reserveStartDate;
        this.reserveEndDate = reserveEndDate;
        this.reserveStatusId = reserveStatusId;
        this.userId = userId;
        this.userContactNo = userContactNo;
        this.userEmail = userEmail;
    }

    public Reserve createRequestReserve() {
        this.reserveId = String.valueOf(UUID.randomUUID());
        this.reserveStatusId = ReserveStatus.REQUEST.getKey();
        return toEntity();
    }

    public Reserve createApproveReserve() {
        this.reserveId = String.valueOf(UUID.randomUUID());
        this.reserveStatusId = ReserveStatus.APPROVE.getKey();
        return toEntity();
    }

    public Reserve toEntity() {
        return Reserve.builder()
                .reserveId(this.reserveId)
                .reserveItemId(this.reserveItemId)
                .locationId(this.locationId)
                .categoryId(this.categoryId)
                .reserveQty(this.reserveQty)
                .reservePurposeContent(this.reservePurposeContent)
                .attachmentCode(this.attachmentCode)
                .reserveStartDate(this.reserveStartDate)
                .reserveEndDate(this.reserveEndDate)
                .reserveStatusId(this.reserveStatusId)
                .userId(this.userId)
                .userContactNo(this.userContactNo)
                .userEmail(this.userEmail)
                .build();
    }

}
