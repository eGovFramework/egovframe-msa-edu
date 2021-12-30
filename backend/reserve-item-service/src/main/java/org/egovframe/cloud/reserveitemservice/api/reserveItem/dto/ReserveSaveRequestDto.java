package org.egovframe.cloud.reserveitemservice.api.reserveItem.dto;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
    public ReserveSaveRequestDto(Long reserveItemId, Integer reserveQty, String reservePurposeContent, String attachmentCode, LocalDateTime reserveStartDate, LocalDateTime reserveEndDate, String reserveStatusId, String userId, String userContactNo, String userEmail) {
        this.reserveItemId = reserveItemId;
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


}
