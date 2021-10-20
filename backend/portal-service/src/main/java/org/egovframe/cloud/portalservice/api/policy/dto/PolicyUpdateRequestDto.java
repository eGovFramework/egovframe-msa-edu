package org.egovframe.cloud.portalservice.api.policy.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * org.egovframe.cloud.portalservice.api.policy.dto.PolicyUpdateRequestDto
 * <p>
 * 이용약관/개인정보수집동의(Policy) 수정 시 요청 dto
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/07/06
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/06    shinmj  최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
public class PolicyUpdateRequestDto {

    private String title;
    private Boolean isUse;
    private String contents;

    @Builder
    public PolicyUpdateRequestDto(String title, Boolean isUse, String contents){
        this.title = title;
        this.isUse = isUse;
        this.contents = contents;
    }
}
