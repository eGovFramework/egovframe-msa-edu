package org.egovframe.cloud.portalservice.api.policy.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egovframe.cloud.portalservice.domain.policy.Policy;

import javax.validation.constraints.NotBlank;
import java.time.ZonedDateTime;

/**
 * org.egovframe.cloud.portalservice.api.policy.dto.PolicySaveRequestDto
 * <p>
 * 이용약관/개인정보수집동의(Policy) 등록 시 요청 dto
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
public class PolicySaveRequestDto {
    @NotBlank(message = "{common.type}{valid.required}")
    private String type;
    @NotBlank(message = "{policy.title}{valid.required}")
    private String title;
    private Boolean isUse;
    private ZonedDateTime regDate;
    private String contents;

    @Builder
    public PolicySaveRequestDto(String type, String title, Boolean isUse, ZonedDateTime regDate, String contents){
        this.type = type;
        this.title = title;
        this.isUse = isUse;
        this.regDate = regDate;
        this.contents = contents;
    }

    /**
     * 저장 요청 dto -> 이용약관 entity
     *
     * @return
     */
    public Policy toEntity(){
        return Policy.builder()
                .type(this.type)
                .title(this.title)
                .isUse(this.isUse)
                .regDate(this.regDate)
                .contents(this.contents)
                .build();
    }
}
