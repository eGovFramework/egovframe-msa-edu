package org.egovframe.cloud.portalservice.api.code.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.egovframe.cloud.common.dto.RequestDto;

/**
 * org.egovframe.cloud.portalservice.api.code.dto.CodeDetailRequestDto
 * <p>
 * 공통코드 상세 조회 요청 파라미터 dto
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
@SuperBuilder
public class CodeDetailRequestDto extends RequestDto {
    private String parentCodeId; // 상위 공통코드 ID
}
