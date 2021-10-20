package org.egovframe.cloud.reserverequestservice.domain;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.egovframe.cloud.reactive.domain.BaseEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * org.egovframe.cloud.reserverequestservice.domain.Reserve
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
@Table("reserve")
public class Reserve extends BaseEntity {

    @Id
    @Column
    private String reserveId;     //예약 id

    @Column
    private Long reserveItemId; //예약 물품 id

    @Column
    private Long locationId;    //예약 물품 - 지역 id

    @Column
    private String categoryId; //예약 물품 - 유형 id

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
    private String userId;  //예약자

    @Column
    private String userContactNo;   //예약자 연락처

    @Column("user_email_addr")
    private String userEmail;   //예약자 이메일

    @Builder
    public Reserve(String reserveId, Long reserveItemId, Long locationId, String categoryId, Integer reserveQty, String reservePurposeContent, String attachmentCode, LocalDateTime reserveStartDate, LocalDateTime reserveEndDate, String reserveStatusId, String userId, String userContactNo, String userEmail) {
        this.reserveId = reserveId;
        this.reserveItemId = reserveItemId;
        this.locationId = locationId;
        this.categoryId = categoryId;
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

}