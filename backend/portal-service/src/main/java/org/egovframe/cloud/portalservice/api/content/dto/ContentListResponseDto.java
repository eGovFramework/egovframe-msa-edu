package org.egovframe.cloud.portalservice.api.content.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * org.egovframe.cloud.portalservice.api.content.dto.ContentListResponseDto
 * <p>
 * 컨텐츠 목록 응답 DTO 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/22
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/22    jooho       최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
public class ContentListResponseDto implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -1902534539945283321L;

    /**
     * 컨텐츠 번호
     */
    private Integer contentNo;

    /**
     * 컨텐츠 명
     */
    private String contentName;

    /**
     * 수정자
     */
    private String lastModifiedBy;

    /**
     * 수정 일시
     */
    private LocalDateTime modifiedDate;

    /**
     * 컨텐츠 목록 응답 DTO 생성자
     *
     * @param contentNo 컨텐츠 번호
     * @param contentName 컨텐츠 명
     * @param lastModifiedBy 수정자
     * @param modifiedDate 수정 일시
     */
    @QueryProjection
    public ContentListResponseDto(Integer contentNo, String contentName, String lastModifiedBy, LocalDateTime modifiedDate) {
        this.contentNo = contentNo;
        this.contentName = contentName;
        this.lastModifiedBy = lastModifiedBy;
        this.modifiedDate = modifiedDate;
    }

}
