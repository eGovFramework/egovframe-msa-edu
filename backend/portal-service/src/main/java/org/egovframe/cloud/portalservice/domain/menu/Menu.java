package org.egovframe.cloud.portalservice.domain.menu;

import java.util.Objects;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.egovframe.cloud.portalservice.api.menu.dto.MenuUpdateRequestDto;
import org.egovframe.cloud.servlet.domain.BaseEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * org.egovframe.cloud.portalservice.domain.menu.Menu
 * <p>
 * 메뉴관리 > Menu 도메인 class
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
@Entity
@ToString
public class Menu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long id;    // id

    @Column( length = 100)
    private String menuKorName;    // 메뉴명 kor

    @Column( length = 100)
    private String menuEngName;    // 메뉴명 eng

    @Column(name = "type_id", length = 20)
    private String menuType;  // 메뉴 유형 공통 코드 id

    @Column
    private Integer connectId;   // 연결된 컨텐츠 or 게시판 id

    @Column(length = 200)
    private String urlPath; // 링크 url

    @Column(name = "use_at", columnDefinition = "boolean default true")
    private Boolean isUse;  // 사용 여부

    @Column(name = "show_at", columnDefinition = "boolean default true")
    private Boolean isShow; // 출력 여부

    @Column(name = "blank_at", columnDefinition = "boolean default false")
    private Boolean isBlank; // 연결 형태 (새창/현재창)

    @Column(name = "sub_name", length = 200)
    private String subName;  // 메뉴 서브명

    @Column(name = "menu_description", length = 500)
    private String description; // 메뉴 설명

    @Column(columnDefinition = "SMALLINT(3)")
    private Integer sortSeq; // 정렬 순서

    @Column(name = "icon_name", length = 100)
    private String icon;    // 아이콘 class

    @Column(name = "level_no", length = 15)
    private Integer level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_menu_id")
    private Menu parent;

    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Menu> children = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id")
    private Site site;

    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "menu", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<MenuRole> menuRoles = new ArrayList<>();


    @Builder
    public Menu(String menuKorName, Optional<Menu> parent, Integer sortSeq, Site site, Integer level, Boolean isUse, Boolean isShow) {
        this.menuKorName = menuKorName;
        this.sortSeq = sortSeq;
        this.site = site;
        this.level = level;
        this.isShow = isShow;
        this.isUse = isUse;
        if (Objects.nonNull(parent)) {
            parent.ifPresent(it -> this.parent = it);
        }
    }

    /**
     * 메뉴명 변경
     *
     * @param menuKorName
     * @return
     */
    public Menu updateName(String menuKorName) {
        this.menuKorName = menuKorName;
        return this;
    }

    /**
     * 드래그앤드랍으로 변경 시 상위메뉴, 정렬순서 수정
     *
     * @param parent
     * @param sortSeq
     * @return
     */
    public Menu updateDnD(Optional<Menu> parent, Integer sortSeq, Integer level) {
        this.sortSeq = sortSeq;
        this.level = level;

        if (!parent.isPresent()) {
            return updateOldParent();
        }

        if (parent.equals(this.parent)) {
            return this;
        }

        this.parent = parent.get();
        parent.get().getChildren().add(this);
        return this;
    }

    private Menu updateOldParent() {
        if (Objects.isNull(this.parent)) {
            return this;
        }

        Optional<Menu> oldMenu = this.parent.getChildren().stream()
            .filter(it -> it.getId().equals(this.id))
            .findAny();

        oldMenu.ifPresent(it -> this.parent.getChildren().remove(it));
        this.parent = null;
        return this;
    }


    /**
     * 메뉴 기본 설정 저장
     *
     * @param updateRequestDto
     * @return
     */
    public Menu updateDetail(MenuUpdateRequestDto updateRequestDto) {
        this.menuKorName = updateRequestDto.getMenuKorName();
        this.menuEngName = updateRequestDto.getMenuEngName();
        this.menuType = updateRequestDto.getMenuType();
        this.connectId = updateRequestDto.getConnectId();
        this.urlPath = updateRequestDto.getUrlPath();
        this.isUse = updateRequestDto.getIsUse();
        this.isShow = updateRequestDto.getIsShow();
        this.isBlank = updateRequestDto.getIsBlank();
        this.subName = updateRequestDto.getSubName();
        this.description = updateRequestDto.getDescription();
        this.icon = updateRequestDto.getIcon();

        return this;
    }

    /**
     * 부모 메뉴 설정 및 양방향 관계 처리
     *
     * @param parent
     */
    public void setParentMenu(Menu parent) {
        this.parent = parent;
        parent.getChildren().add(this);
    }

    /**
     * 연관관계 데이터(권한별 메뉴) 조회
     *
     * @param roleId
     * @return
     */
    public Optional<MenuRole> getMenuRole(String roleId) {
        return this.getMenuRoles()
                .stream()
                .filter(menuRole ->
                        menuRole.getRoleId().equals(roleId))
                .findAny();
    }

    public boolean hasParent() {
        return Objects.nonNull(this.parent);
    }

}
