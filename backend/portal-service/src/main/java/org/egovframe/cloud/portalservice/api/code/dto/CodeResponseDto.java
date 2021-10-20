package org.egovframe.cloud.portalservice.api.code.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egovframe.cloud.portalservice.domain.code.Code;

/**
 * org.egovframe.cloud.portalservice.api.code.dto.CodeResponseDto
 * <p>
 * 공통코드 조회 응답 dto
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
public class CodeResponseDto {
    private String codeId; // 코드ID
    private String codeName; // 코드 명
    private String codeDescription; // 코드 설명
    private Integer sortSeq; // 정렬 순서
    private Boolean useAt; // 사용 여부
    private Boolean readonly; // 수정하면 안되는 읽기전용 공통코드

    /**
     * Entity의 필드 중 일부만 사용하므로 생성자로 Entity를 받아 필드에 값을 넣는다. 굳이 모든 필드를 가진 생성자가 필요하지 않다.
     *
     * @param entity
     */
    public CodeResponseDto(Code entity) {
        this.codeId = entity.getCodeId();
        this.codeName = entity.getCodeName();
        this.codeDescription = entity.getCodeDescription();
        this.sortSeq = entity.getSortSeq();
        this.useAt = entity.getUseAt();
        this.readonly = entity.getReadonly();
    }

}
