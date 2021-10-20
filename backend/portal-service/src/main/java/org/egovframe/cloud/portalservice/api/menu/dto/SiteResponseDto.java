package org.egovframe.cloud.portalservice.api.menu.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.egovframe.cloud.portalservice.domain.menu.Site;

/**
 * org.egovframe.cloud.portalservice.api.menu.dto.SiteResponseDto
 * <p>
 * 메뉴관리 사이트 응답 dto class
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/07/21
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/21    shinmj  최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
@ToString
public class SiteResponseDto {
    private Long id;
    private String name;
    private Boolean isUse;

    @Builder
    public SiteResponseDto(Site entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.isUse = entity.getIsUse();
    }
}
