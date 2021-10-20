package org.egovframe.cloud.reserverequestservice.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.egovframe.cloud.reserverequestservice.domain.Reserve;

import java.time.LocalDateTime;

/**
 * org.egovframe.cloud.reserverequestservice.api.dto.ReserveResponseDto
 * <p>
 * 예약 신청 응답 dto class
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
    private Long locationId;
    private String categoryId;

    private Integer reserveQty;
    private LocalDateTime reserveStartDate;
    private LocalDateTime reserveEndDate;
    private String reservePurposeContent;
    private String attachmentCode;

    private String userId;
    private String userContactNo;
    private String userEmail;

    @Builder
    public ReserveResponseDto(Reserve entity) {
        this.reserveId = entity.getReserveId();
        this.reserveItemId = entity.getReserveItemId();
        this.locationId = entity.getLocationId();
        this.categoryId = entity.getCategoryId();
        this.reserveQty = entity.getReserveQty();
        this.reserveStartDate = entity.getReserveStartDate();
        this.reserveEndDate = entity.getReserveEndDate();
        this.reservePurposeContent = entity.getReservePurposeContent();
        this.attachmentCode = entity.getAttachmentCode();
        this.userId = entity.getUserId();
        this.userContactNo = entity.getUserContactNo();
        this.userEmail = entity.getUserEmail();
    }
}
