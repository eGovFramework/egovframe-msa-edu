package org.egovframe.cloud.reservechecksevice.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egovframe.cloud.common.dto.RequestDto;

/**
 * org.egovframe.cloud.reservechecksevice.api.dto.ReserveRequestDto
 * <p>
 * 얘약 목록 요청 dto class
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/09/27
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/27    shinmj      최초 생성
 * </pre>
 */
@NoArgsConstructor
@Getter
public class ReserveRequestDto extends RequestDto {
    private Long locationId;
    private String categoryId;
}
