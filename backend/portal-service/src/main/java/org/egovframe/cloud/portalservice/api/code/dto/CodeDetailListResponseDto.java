package org.egovframe.cloud.portalservice.api.code.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * org.egovframe.cloud.portalservice.api.code.dto.CodeDetailListResponseDto
 * <p>
 * 공통코드 상세 목록 조회 응답 dto
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
public class CodeDetailListResponseDto {
    private String parentCodeId; // 상위 코드ID
    private String codeId; // 코드ID
    private String codeName; // 코드 명
    private Integer sortSeq; // 정렬 순서
    private Boolean useAt; // 사용 여부
    private Boolean readonly; // 수정하면 안되는 읽기전용 공통코드
}
