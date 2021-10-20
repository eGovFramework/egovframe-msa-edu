package org.egovframe.cloud.portalservice.api.banner.dto;

import org.egovframe.cloud.common.dto.RequestDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
/**
 * org.egovframe.cloud.portalservice.api.content.dto.BannerRequestDto
 * <p>
 * 배너 목록 요청 DTO 클래스
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/10/12
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/10/12    shinmj       최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
public class BannerRequestDto extends RequestDto {
	private Long siteId;
}
