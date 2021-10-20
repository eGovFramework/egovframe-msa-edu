package org.egovframe.cloud.portalservice.api.policy.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.egovframe.cloud.portalservice.domain.policy.Policy;

import java.time.ZonedDateTime;

/**
 * org.egovframe.cloud.portalservice.api.policy.dto.PolicyResponseDto
 * <p>
 * 이용약관/개인정보수집동의(Policy) 응답 dto
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
@ToString
public class PolicyResponseDto {
    private Long id;
    private String type;
    private String title;
    private Boolean isUse;
    private ZonedDateTime regDate;
    private String contents;

    @Builder
    public PolicyResponseDto(Policy policy){
        this.id = policy.getId();
        this.type = policy.getType();
        this.title = policy.getTitle();
        this.isUse = policy.getIsUse();
        this.regDate = policy.getRegDate();
        this.contents = policy.getContents();
    }

}
