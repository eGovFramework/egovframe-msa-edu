package org.egovframe.cloud.portalservice.api.code.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egovframe.cloud.portalservice.domain.code.Code;

/**
 * org.egovframe.cloud.portalservice.api.code.dto.CodeListResponseDto
 * <p>
 * 공통코드 목록 조회 응답 dto
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
public class CodeListResponseDto {
    private String codeId; // 코드ID
    private String codeName; // 코드 명
    private String codeDescription; // 코드 설명
    private Boolean useAt; // 사용 여부
    private Boolean readonly; // 수정하면 안되는 읽기전용 공통코드
    private Long codeDetailCount; // 코드상세수
}
