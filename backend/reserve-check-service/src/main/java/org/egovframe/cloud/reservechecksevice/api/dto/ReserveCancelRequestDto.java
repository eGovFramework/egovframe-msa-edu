package org.egovframe.cloud.reservechecksevice.api.dto;

import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * org.egovframe.cloud.reservechecksevice.api.dto.ReserveCancelRequestDto
 * <p>
 * 예약 취소 요청 dto class
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/10/06
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/10/06    shinmj      최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
@ToString
public class ReserveCancelRequestDto {
	@NotBlank
	private String reasonCancelContent;

	@Builder
	public ReserveCancelRequestDto(String reasonCancelContent) {
		this.reasonCancelContent = reasonCancelContent;
	}
}
