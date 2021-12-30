package org.egovframe.cloud.portalservice.api.menu.dto;

import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.egovframe.cloud.portalservice.domain.menu.Menu;

import java.util.List;
import java.util.stream.Collectors;
import org.egovframe.cloud.portalservice.domain.menu.MenuRole;

/**
 * org.egovframe.cloud.portalservice.api.menu.dto.MenuSideResponseDto
 * <p>
 * 로그인 후 사용자 권한별 메뉴 응답 dto class
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/08/13
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/08/13    shinmj  최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
@ToString
public class MenuSideResponseDto {
    private Long id;
    private String korName;
    private String engName;
    private Long parentId;
    private Integer sortSeq;
    private String icon;
    private Integer level;
    private String urlPath;
    private Integer connectId;
    private String menuType;
    private Boolean isShow;
    @ToString.Exclude
    private List<MenuSideResponseDto> children;

    /**
     * 생성자
     * 계층구조를 만들기 위해 querydsl에서 사용
     *
     * @param menu
     */
    public MenuSideResponseDto (Menu menu, String roleId) {
        this.id = menu.getId();
        this.korName = menu.getMenuKorName();
        this.engName = menu.getMenuEngName();
        if (menu.getParent() != null) {
            this.parentId = menu.getParent().getId();
        }
        this.sortSeq = menu.getSortSeq();
        this.icon = menu.getIcon();
        this.level = menu.getLevel();
        this.menuType = menu.getMenuType();
        this.connectId = menu.getConnectId();
        this.urlPath = menu.getUrlPath();
        this.isShow = menu.getIsShow();

        this.children = menu.getChildren().stream()
                .filter(Menu::getIsUse)
                .filter(children -> children.getMenuRole(roleId).isPresent())
                .map(children -> new MenuSideResponseDto(children, roleId))
                .collect(Collectors.toList());
    }

    /**
     * 메뉴유형이 게시판이나 컨텐츠인 경우 urlPath 지정
     *
     * @param urlPath
     */
    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    public boolean hasChildren() {
        return Objects.nonNull(children) || children.size() > 0;
    }

    public boolean isRequiredUrlPath() {
        return "board".equals(menuType) || "contents".equals(menuType);
    }
}
