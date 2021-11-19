package org.egovframe.cloud.portalservice.api.menu.dto;

import java.util.Comparator;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.egovframe.cloud.portalservice.domain.menu.Menu;

import java.util.List;
import java.util.stream.Collectors;

/**
 * org.egovframe.cloud.portalservice.api.menu.dto.MenuTreeResponseDto
 * <p>
 * 메뉴관리 tree 응답 dto class
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
public class MenuTreeResponseDto {
    private Long menuId;
    private String name;
    private Long parentId;
    private Integer sortSeq;
    private String icon;
    @ToString.Exclude
    private List<MenuTreeResponseDto> children;
    private Integer level;

    @Builder
    public MenuTreeResponseDto(Menu entity) {
        this.menuId = entity.getId();
        this.name = entity.getMenuKorName();
        if (entity.getParent() != null) {
            this.parentId = entity.getParent().getId();
        }

        this.sortSeq = entity.getSortSeq();
        this.icon = entity.getIcon();
        this.level = entity.getLevel();
        this.children = entity.getChildren().stream()
                .map(children -> new MenuTreeResponseDto(children))
                .sorted(Comparator.comparing(MenuTreeResponseDto::getSortSeq))
                .collect(Collectors.toList());

    }

}
