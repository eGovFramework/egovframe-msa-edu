package org.egovframe.cloud.portalservice.api.content.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egovframe.cloud.portalservice.domain.content.Content;

/**
 * org.egovframe.cloud.portalservice.api.content.dto.ContentResponseDto
 * <p>
 * 컨텐츠 상세 응답 DTO 클래스
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
public class ContentResponseDto {

    /**
     * 컨텐츠 번호
     */
    private Integer contentNo;

    /**
     * 컨텐츠 명
     */
    private String contentName;

    /**
     * 컨텐츠 비고
     */
    private String contentRemark;

    /**
     * 컨텐츠 값
     */
    private String contentValue;

    /**
     * 컨텐츠 엔티티를 생성자로 주입 받아서 컨텐츠 상세 응답 DTO 속성 값 세팅
     *
     * @param entity 컨텐츠 엔티티
     */
    public ContentResponseDto(Content entity) {
        this.contentNo = entity.getContentNo();
        this.contentName = entity.getContentName();
        this.contentRemark = entity.getContentRemark();
        this.contentValue = entity.getContentValue();
    }

    /**
     * 컨텐츠 상세 응답 DTO 속성 값으로 컨텐츠 엔티티 빌더를 사용하여 객체 생성
     *
     * @return Content 컨텐츠 엔티티
     */
    public Content toEntity() {
        return Content.builder()
                .contentNo(contentNo)
                .contentName(contentName)
                .contentRemark(contentRemark)
                .contentValue(contentValue)
                .build();
    }

}
