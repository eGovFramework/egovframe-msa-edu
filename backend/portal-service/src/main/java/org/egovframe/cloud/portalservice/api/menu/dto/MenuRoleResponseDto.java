package org.egovframe.cloud.portalservice.api.menu.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.egovframe.cloud.portalservice.domain.menu.Menu;
import org.egovframe.cloud.portalservice.domain.menu.MenuRole;

import java.util.List;
import java.util.stream.Collectors;

/**
 * org.egovframe.cloud.portalservice.api.menu.dto.MenuRoleResponseDto
 * <p>
 * 권한별 메뉴관리 응답 dto class
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
public class MenuRoleResponseDto {
    private Long menuRoleId;
    private String roleId;
    private Boolean isChecked;
    private Long id;
    private String korName;
    private String engName;
    private Long parentId;
    private Integer sortSeq;
    private String icon;
    private Integer level;
    @ToString.Exclude
    private List<MenuRoleResponseDto> children;


    /**
     * 생성자
     * querydsl 사용시 Menu Entity로 생성
     * roleId에 해당하는 권한별 메뉴 데이터만 조회
     *
     * @param menu
     * @param roleId
     */
    public MenuRoleResponseDto (Menu menu, String roleId) {

        MenuRole menuRole = menu.getMenuRole(roleId);
        if (menuRole == null) {
            this.isChecked = false;
            this.roleId = roleId;
        }else {
            this.menuRoleId = menuRole.getId();
            this.roleId = menuRole.getRoleId();
            this.isChecked = true;
        }

        this.id = menu.getId();
        this.korName = menu.getMenuKorName();
        this.engName = menu.getMenuEngName();
        if (menu.getParent() != null) {
            this.parentId = menu.getParent().getId();
        }

        this.sortSeq = menu.getSortSeq();
        this.icon = menu.getIcon();
        this.level = menu.getLevel();
        this.children = menu.getChildren().stream()
                .map(children -> new MenuRoleResponseDto(children, roleId))
                .collect(Collectors.toList());
    }

}
