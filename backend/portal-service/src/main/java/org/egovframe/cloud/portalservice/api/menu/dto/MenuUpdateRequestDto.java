package org.egovframe.cloud.portalservice.api.menu.dto;

import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import org.egovframe.cloud.common.exception.BusinessMessageException;
import org.springframework.util.StringUtils;

/**
 * org.egovframe.cloud.portalservice.api.menu.dto.MenuUpdateRequestDto
 * <p>
 * 메뉴관리 상세정보 요청 dto class
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
public class MenuUpdateRequestDto {
    @NotBlank(message = "{menu.name}{valid.required}")
    private String menuKorName;
    @NotBlank(message = "{menu.eng_name}{valid.required}")
    private String menuEngName;
    private String menuType;
    private String menuTypeName;
    private Integer connectId;
    private String urlPath;
    private Boolean isUse;
    private Boolean isShow;
    private Boolean isBlank;
    private String subName;
    private String description;
    private String icon;

    @Builder
    public MenuUpdateRequestDto(String menuKorName, String menuEngName, String menuType, String menuTypeName, Integer connectId, String urlPath, Boolean isUse, Boolean isShow, Boolean isBlank, String subName, String description, String icon) {
        this.menuKorName = menuKorName;
        this.menuEngName = menuEngName;
        this.menuType = menuType;
        this.menuTypeName = menuTypeName;
        this.connectId = connectId;
        this.urlPath = urlPath;
        this.isUse = isUse;
        this.isShow = isShow;
        this.isBlank = isBlank;
        this.subName = subName;
        this.description = description;
        this.icon = icon;
    }

    public boolean hasConnectId() {
        if ("contents".equals(menuType) || "board".equals(menuType)) {
            return Objects.nonNull(connectId);
        }
        return true;
    }

    public boolean hasUrlPath() {
        if ("inside".equals(menuType) || "outside".equals(menuType)) {
            return Objects.nonNull(urlPath) || StringUtils.hasText(urlPath);
        }
        return true;
    }
}
