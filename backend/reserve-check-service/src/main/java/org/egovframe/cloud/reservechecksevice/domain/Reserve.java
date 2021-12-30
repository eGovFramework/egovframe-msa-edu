package org.egovframe.cloud.reservechecksevice.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.With;
import org.egovframe.cloud.common.exception.BusinessMessageException;
import org.egovframe.cloud.reactive.domain.BaseEntity;
import org.egovframe.cloud.reservechecksevice.api.dto.ReserveUpdateRequestDto;
import org.egovframe.cloud.reservechecksevice.client.dto.UserResponseDto;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * org.egovframe.cloud.reservechecksevice.domain.Reserve
 *
 * 예약 도메인 클래스
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/09/15
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/15    shinmj       최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
@ToString
@With
@Table("reserve")
public class Reserve extends BaseEntity {

    @Id
    @Column
    private String reserveId;     //예약 id

    @Column
    private Long reserveItemId; //예약 물품 id

    @Transient
    private ReserveItem reserveItem;

    @Setter
    @Column
    private Long locationId;    //지역 id

    @Setter
    @Column
    private String categoryId;  //예약유형 - 공통코드 reserve-category

    @Column
    private Integer reserveQty;    //예약 신청 인원/수량

    @Column
    private String reservePurposeContent;   //예약 목적

    @Column
    private String attachmentCode;  //첨부파일 코드

    @Column
    private LocalDateTime reserveStartDate; //예약 신청 시작일
    @Column
    private LocalDateTime reserveEndDate; //예약 신청 종료일

    @Column
    private String reserveStatusId;     //예약상태 - 공통코드(reserve-status)

    @Column
    private String reasonCancelContent; //예약 취소 사유

    @Column
    private String userId;  //예약자

    @Transient
    private UserResponseDto user;

    @Column
    private String userContactNo;   //예약자 연락처

    @Column("user_email_addr")
    private String userEmail;   //예약자 이메일

    @Builder
    public Reserve(String reserveId, Long reserveItemId,
        ReserveItem reserveItem, Long locationId, String categoryId, Integer reserveQty,
        String reservePurposeContent, String attachmentCode, LocalDateTime reserveStartDate,
        LocalDateTime reserveEndDate, String reserveStatusId, String reasonCancelContent, String userId,
        UserResponseDto user, String userContactNo, String userEmail) {
        this.reserveId = reserveId;
        this.reserveItemId = reserveItemId;
        this.reserveItem = reserveItem;
        this.locationId = locationId;
        this.categoryId = categoryId;
        this.reserveQty = reserveQty;
        this.reservePurposeContent = reservePurposeContent;
        this.attachmentCode = attachmentCode;
        this.reserveStartDate = reserveStartDate;
        this.reserveEndDate = reserveEndDate;
        this.reserveStatusId = reserveStatusId;
        this.reasonCancelContent = reasonCancelContent;
        this.userId = userId;
        this.user = user;
        this.userContactNo = userContactNo;
        this.userEmail = userEmail;
    }

    public boolean isReserveUser(String userId) {
        return this.userId.equals(userId);
    }

    /**
     * 물품 정보 세팅
     *
     * @param reserveItem
     * @return
     */
    public Reserve setReserveItem(ReserveItem reserveItem) {
        this.reserveItem = reserveItem;
        this.reserveItemId = reserveItem.getReserveItemId();
        return this;
    }

    /**
     * 예약자 정보 세팅
     *
     * @param user
     * @return
     */
    public Reserve setUser(UserResponseDto user) {
        this.user = user;
        this.userId = user.getUserId();
        return this;
    }

    /**
     * 예약 상태 업데이트
     *
     * @param reserveStatusId
     * @return
     */
    public Reserve updateStatus(String reserveStatusId) {
        this.reserveStatusId = reserveStatusId;
        return this;
    }

    /**
     * 취소 사유 업데이트
     *
     * @param reasonCancelContent
     * @return
     */
    public Reserve updateReasonCancel(String reasonCancelContent) {
        this.reasonCancelContent = reasonCancelContent;
        return this;
    }

    /**
     * 예약 정보 업데이트
     *
     * @param updateRequestDto
     * @return
     */
    public Reserve update(ReserveUpdateRequestDto updateRequestDto) {
        this.reserveQty = updateRequestDto.getReserveQty();
        this.reservePurposeContent = updateRequestDto.getReservePurposeContent();
        this.attachmentCode = updateRequestDto.getAttachmentCode();
        this.reserveStartDate = updateRequestDto.getReserveStartDate();
        this.reserveEndDate = updateRequestDto.getReserveEndDate();
        this.userId = updateRequestDto.getUserId();
        this.userEmail = updateRequestDto.getUserEmail();
        this.userContactNo = updateRequestDto.getUserContactNo();
        return this;
    }

    /**
     * create 정보 세팅
     * insert 시 필요
     *
     * @param createdDate
     * @param createdBy
     * @return
     */
    public Reserve setCreatedInfo(LocalDateTime createdDate, String createdBy) {
        this.createdBy = createdBy;
        this.createDate = createdDate;
        return this;
    }

    /**
     * 예약 수량 양수, 음수 변환
     * 예약 취소 시 재고 카운트를 위해
     *
     * @return
     */
    public Reserve conversionReserveQty() {
        if (this.reserveQty != null) {
            this.reserveQty = (this.reserveQty * -1);
        }

        return this;
    }

    public boolean isDone() {
        return ReserveStatus.DONE.isEquals(this.reserveStatusId);
    }

    public Reserve updateStatusCancel(String reason, String errorMessage) {
        if (isDone()) {
            throw new BusinessMessageException(errorMessage);
        }

        this.reserveStatusId = ReserveStatus.CANCEL.getKey();
        this.reasonCancelContent = reason;
        return this;
    }

    public boolean isEducation() {
        return Category.EDUCATION.isEquals(this.getCategoryId());
    }

    public boolean isRequest() {
        return ReserveStatus.REQUEST.isEquals(this.reserveStatusId);
    }
}
