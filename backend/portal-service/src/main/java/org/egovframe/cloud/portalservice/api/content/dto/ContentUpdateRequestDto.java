package org.egovframe.cloud.portalservice.api.content.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

/**
 * org.egovframe.cloud.portalservice.api.content.dto.ContentUpdateRequestDto
 * <p>
 * 컨텐츠 수정 요청 DTO 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/08
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/08    jooho       최초 생성
 * </pre>
 */
@Getter
public class ContentUpdateRequestDto {

    /**
     * 컨텐츠 명
     */
    @NotBlank(message = "{content.content_name} {err.required}")
    private String contentName;

    /**
     * 컨텐츠 비고
     */
    private String contentRemark;

    /**
     * 컨텐츠 값
     */
    @NotBlank(message = "{content.content_value} {err.required}")
    private String contentValue;

}
