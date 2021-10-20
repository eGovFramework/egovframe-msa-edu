package org.egovframe.cloud.portalservice.api.code.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egovframe.cloud.portalservice.domain.code.Code;

import javax.validation.constraints.NotBlank;

/**
 * org.egovframe.cloud.portalservice.api.code.dto.CodeUpdateRequestDto
 * <p>
 * 공통코드 수정 요청 dto
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/07/12
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/12    jaeyeolkim  최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
public class CodeUpdateRequestDto {
    @NotBlank(message = "{code.code_id}{valid.required}")
    private String codeId; // 코드ID

    @NotBlank(message = "{code.code_name}{valid.required}")
    private String codeName; // 코드 명

    private String codeDescription; // 코드 설명
    private Integer sortSeq; // 정렬 순서
    private Boolean useAt; // 사용 여부

    @Builder
    public CodeUpdateRequestDto(String codeId, String codeName, String codeDescription, Integer sortSeq, Boolean useAt) {
        this.codeId = codeId;
        this.codeName = codeName;
        this.codeDescription = codeDescription;
        this.sortSeq = sortSeq;
        this.useAt = useAt;
    }

    /**
     * SaveRequestDto 의 필드 값을 Entity 빌더를 사용하여 주입 후 Entity 를 리턴한다.
     * SaveRequestDto 가 가지고 있는 Entity 의 필드만 세팅할 수 있게 된다.
     *
     * @return
     */
    public Code toEntity() {
        return Code.builder()
                .codeId(codeId)
                .codeName(codeName)
                .codeDescription(codeDescription)
                .useAt(useAt)
                .build();
    }
}
