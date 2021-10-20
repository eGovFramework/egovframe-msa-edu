package org.egovframe.cloud.portalservice.api.privacy.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * org.egovframe.cloud.portalservice.api.privacy.dto.PrivacyListResponseDto
 * <p>
 * 개인정보처리방침 목록 응답 DTO 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/23
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/23    jooho       최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
public class PrivacyListResponseDto implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -6649931248096772439L;

    /**
     * 개인정보처리방침 번호
     */
    private Integer privacyNo;

    /**
     * 개인정보처리방침 제목
     */
    private String privacyTitle;

    /**
     * 사용 여부
     */
    private Boolean useAt;

    /**
     * 생성 일시
     */
    private LocalDateTime createdDate;

    /**
     * 개인정보처리방침 목록 응답 DTO 생성자
     *
     * @param privacyNo 개인정보처리방침 번호
     * @param privacyTitle 개인정보처리방침 제목
     * @param useAt 사용 여부
     * @param createdDate 생성 일시
     */
    @QueryProjection
    public PrivacyListResponseDto(Integer privacyNo, String privacyTitle, Boolean useAt, LocalDateTime createdDate) {
        this.privacyNo = privacyNo;
        this.privacyTitle = privacyTitle;
        this.useAt = useAt;
        this.createdDate = createdDate;
    }

}
