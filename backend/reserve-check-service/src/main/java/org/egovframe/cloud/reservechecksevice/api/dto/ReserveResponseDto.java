package org.egovframe.cloud.reservechecksevice.api.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.egovframe.cloud.reservechecksevice.client.dto.ReserveItemRelationResponseDto;
import org.egovframe.cloud.reservechecksevice.domain.Reserve;

/**
 * org.egovframe.cloud.reservechecksevice.api.dto.ReserveResponseDto
 * <p>
 * 예약 확인(신청) 응답 dto class
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
public class ReserveResponseDto {

    private String reserveId;

    private Long reserveItemId;
    private ReserveItemRelationResponseDto reserveItem;

    private Integer reserveQty;
    private LocalDateTime reserveStartDate;
    private LocalDateTime reserveEndDate;
    private String reservePurposeContent;
    private String attachmentCode;

    private String reserveStatusId;

    private String userId;
    private String userName;
    private String userContactNo;
    private String userEmail;

    @Builder
    public ReserveResponseDto(Reserve entity) {
        this.reserveId = entity.getReserveId();
        this.reserveItemId = entity.getReserveItemId();
        this.reserveItem = ReserveItemRelationResponseDto.builder().entity(entity.getReserveItem()).build();
        this.reserveQty = entity.getReserveQty();
        this.reserveStartDate = entity.getReserveStartDate();
        this.reserveEndDate = entity.getReserveEndDate();
        this.reservePurposeContent = entity.getReservePurposeContent();
        this.attachmentCode = entity.getAttachmentCode();
        this.reserveStatusId = entity.getReserveStatusId();
        this.userId = entity.getUserId();
        this.userName = entity.getUser().getUserName();
        this.userContactNo = entity.getUserContactNo();
        this.userEmail = entity.getUserEmail();
    }
}
