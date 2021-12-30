package org.egovframe.cloud.reservechecksevice.api.dto;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.egovframe.cloud.reservechecksevice.validator.annotation.ReserveSaveValid;

/**
 * org.egovframe.cloud.reservechecksevice.api.dto.ReserveUpdateRequestDto
 * <p>
 * 예약 신청 정보 수정 요청 dto class
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
@ReserveSaveValid
public class ReserveUpdateRequestDto {

    @NotNull
    private Long reserveItemId;
    private String categoryId;
    private Integer reserveQty;    //예약 신청 인원/수량

    @NotNull
    private String reservePurposeContent;   //예약 목적
    private String attachmentCode;  //첨부파일 코드
    private LocalDateTime reserveStartDate; //예약 신청 시작일
    private LocalDateTime reserveEndDate; //예약 신청 종료일

    @NotNull
    private String userId;  //예약자

    @NotNull
    private String userContactNo;   //예약자 연락처

    @NotNull
    private String userEmail;   //예약자 이메일

    @Builder
    public ReserveUpdateRequestDto(Long reserveItemId, String categoryId, Integer reserveQty, String reservePurposeContent, String attachmentCode, LocalDateTime reserveStartDate, LocalDateTime reserveEndDate, String userId, String userContactNo, String userEmail) {
        this.reserveItemId = reserveItemId;
        this.categoryId = categoryId;
        this.reserveQty = reserveQty;
        this.reservePurposeContent = reservePurposeContent;
        this.attachmentCode = attachmentCode;
        this.reserveStartDate = reserveStartDate;
        this.reserveEndDate = reserveEndDate;
        this.userId = userId;
        this.userContactNo = userContactNo;
        this.userEmail = userEmail;
    }
}
