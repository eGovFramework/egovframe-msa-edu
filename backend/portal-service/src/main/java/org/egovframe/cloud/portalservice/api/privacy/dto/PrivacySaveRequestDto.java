package org.egovframe.cloud.portalservice.api.privacy.dto;

import lombok.Getter;
import org.egovframe.cloud.portalservice.domain.privacy.Privacy;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * org.egovframe.cloud.portalservice.api.privacy.dto.PrivacySaveRequestDto
 * <p>
 * 개인정보처리방침 등록 요청 DTO 클래스
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
public class PrivacySaveRequestDto {

    /**
     * 개인정보처리방침 제목
     */
    @NotBlank(message = "{privacy.privacy_title} {err.required}")
    private String privacyTitle;

    /**
     * 개인정보처리방침 내용
     */
    @NotBlank(message = "{privacy.privacy_content} {err.required}")
    private String privacyContent;

    /**
     * 사용 여부
     */
    @NotNull(message = "{common.use_at} {err.required}")
    private Boolean useAt;

    /**
     * 개인정보처리방침 등록 요청 DTO 속성 값으로 개인정보처리방침 엔티티 빌더를 사용하여 객체 생성
     *
     * @return Privacy 개인정보처리방침 엔티티
     */
    public Privacy toEntity() {
        return Privacy.builder()
                .privacyTitle(privacyTitle)
                .privacyContent(privacyContent)
                .useAt(useAt)
                .build();
    }

}
