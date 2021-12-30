package org.egovframe.cloud.portalservice.api.menu.dto;


import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * org.egovframe.cloud.portalservice.api.menu.dto.MenuRoleRequestDto
 * <p>
 * 권한별 메뉴관리 저장 요청 dto class
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
public class MenuRoleRequestDto {
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
    private List<MenuRoleRequestDto> children;

    @Builder
    public MenuRoleRequestDto(Long menuRoleId, String roleId, Boolean isChecked, Long id, String korName, String engName, Long parentId, Integer sortSeq, String icon, Integer level, List<MenuRoleRequestDto> children) {
        this.menuRoleId = menuRoleId;
        this.roleId = roleId;
        this.isChecked = isChecked;
        this.id = id;
        this.korName = korName;
        this.engName = engName;
        this.parentId = parentId;
        this.sortSeq = sortSeq;
        this.icon = icon;
        this.level = level;
        this.children = children == null ? null : new ArrayList<>(children);
    }

    public boolean hasMenuRoleId() {
        return Objects.nonNull(menuRoleId);
    }
}
