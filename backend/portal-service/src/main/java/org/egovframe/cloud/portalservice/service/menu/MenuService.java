package org.egovframe.cloud.portalservice.service.menu;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.cloud.common.exception.BusinessException;
import org.egovframe.cloud.common.exception.BusinessMessageException;
import org.egovframe.cloud.common.exception.EntityNotFoundException;
import org.egovframe.cloud.common.service.AbstractService;
import org.egovframe.cloud.portalservice.api.menu.dto.*;
import org.egovframe.cloud.portalservice.domain.menu.Menu;
import org.egovframe.cloud.portalservice.domain.menu.MenuRepository;
import org.egovframe.cloud.portalservice.domain.menu.Site;
import org.egovframe.cloud.portalservice.domain.menu.SiteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * org.egovframe.cloud.portalservice.service.menu.MenuService
 * <p>
 * 메뉴관리 서비스 클래스
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
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MenuService extends AbstractService {

    private final MenuRepository menuRepository;
    private final SiteRepository siteRepository;

    /**
     * 메뉴 트리 목록 조회
     *
     * @param siteId
     * @return
     */
    public List<MenuTreeResponseDto> findTreeBySiteId(Long siteId) {
        return menuRepository.findTreeBySiteId(siteId);
    }

    /**
     * 메뉴 한건 조회
     *
     * @param menuId
     * @return
     */
    public MenuResponseDto findById(Long menuId) {
        return menuRepository.findByIdWithConnectName(menuId);
    }

    /**
     * 메뉴 트리 한건 추가
     *
     * @param menuTreeRequestDto
     * @return
     */
    @Transactional
    public MenuTreeResponseDto save(MenuTreeRequestDto menuTreeRequestDto) {
        Site site = siteRepository.findById(menuTreeRequestDto.getSiteId())
                .orElseThrow(() ->
                        new EntityNotFoundException(getMessage("valid.notexists.format", new Object[]{getMessage("menu.site")}) + " ID= " + menuTreeRequestDto.getSiteId()));

        Menu parent = null;

        if (menuTreeRequestDto.getParentId() != null) {
            parent = menuRepository.findById(menuTreeRequestDto.getParentId())
                    .orElseThrow(() ->
                            new EntityNotFoundException(getMessage("valid.notexists.format", new Object[]{getMessage("menu")}) + " ID= " + menuTreeRequestDto.getParentId()));
        }

        Menu menu = menuRepository.save(Menu.builder()
                .parent(parent)
                .site(site)
                .menuKorName(menuTreeRequestDto.getName())
                .sortSeq(menuTreeRequestDto.getSortSeq())
                .level(menuTreeRequestDto.getLevel())
                .isShow(menuTreeRequestDto.getIsShow())
                .isUse(menuTreeRequestDto.getIsUse())
                .build());
        return MenuTreeResponseDto.builder()
                .entity(menu).build();
    }

    /**
     * 메뉴 명 변경
     *
     * @param menuId
     * @param name
     * @return
     */
    @Transactional
    public MenuTreeResponseDto updateName(Long menuId, String name) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() ->
                        new EntityNotFoundException(getMessage("valid.notexists.format", new Object[]{getMessage("menu")}) + " ID= " + menuId));

        menu.updateName(name);

        return MenuTreeResponseDto.builder()
                .entity(menu).build();
    }

    /**
     * 메뉴 상세 정보 변경
     *
     * @param menuId
     * @param updateRequestDto
     * @return
     */
    @Transactional
    public MenuResponseDto update(Long menuId, MenuUpdateRequestDto updateRequestDto) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() ->
                        new EntityNotFoundException(getMessage("valid.notexists.format", new Object[]{getMessage("menu")}) + " ID= " + menuId));

        //컨텐츠 or 게시판인 경우 connectId 필수
        if ("contents".equals(updateRequestDto.getMenuType()) || "board".equals(updateRequestDto.getMenuType())) {
            if (updateRequestDto.getConnectId() == null || updateRequestDto.getConnectId().equals("")) {
                //컨텐츠 or 게시판을 선택해 주세요
                throw new BusinessMessageException(getMessage("valid.selection.format", new Object[]{updateRequestDto.getMenuTypeName()}));
            }
        }else if ("inside".equals(updateRequestDto.getMenuType()) || "outside".equals(updateRequestDto.getMenuType())) {
            // 내부링크 or 외부링크인 경우 링크 url 필수
            if (updateRequestDto.getUrlPath() == null || updateRequestDto.getUrlPath().equals("")) {
                //링크 Url 값은 필수 입니다.
                throw new BusinessMessageException(getMessage("valid.required", new Object[]{getMessage("menu.url_path")}));

            }
        }

        menu.updateDetail(updateRequestDto);

        return MenuResponseDto.builder()
                .entity(menu).build();
    }

    /**
     * 메뉴 한건 삭제
     *
     * @param menuId
     */
    @Transactional
    public void delete(Long menuId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() ->
                        new EntityNotFoundException(getMessage("valid.notexists.format", new Object[]{getMessage("menu")}) + " ID= " + menuId));
        menuRepository.delete(menu);
    }

    /**
     * 트리 드래그 앤드 드랍 시 children 데이터 재귀호출 저장
     *
     * @param dto
     * @param parent
     * @param sortSeq
     * @param level
     */
    private void recursive(MenuDnDRequestDto dto, Menu parent, Integer sortSeq, Integer level) {
        Menu menu = menuRepository.findById(dto.getMenuId())
                .orElseThrow(() ->
                        new EntityNotFoundException(getMessage("valid.notexists.format", new Object[]{getMessage("menu")}) + " ID= " + dto.getMenuId()));
        menu.updateDnD(parent, sortSeq, level);
        if (dto.getChildren() == null || dto.getChildren().size() <= 0) {
            return;
        }

        for (int i = 0; i < dto.getChildren().size(); i++) {
            MenuDnDRequestDto child = dto.getChildren().get(i);
            recursive(child, menu, child.getSortSeq(), menu.getLevel()+1);
        }
    }

    /**
     * 트리 드래그 앤드 드랍 저장
     *
     * @param siteId
     * @param menuDnDRequestDtoList
     * @return
     */
    @Transactional
    public Long updateDnD(Long siteId, List<MenuDnDRequestDto> menuDnDRequestDtoList) {
        for (int i = 0; i < menuDnDRequestDtoList.size(); i++) {
            recursive(menuDnDRequestDtoList.get(i), null, i+1, 1);
        }
        return siteId;
    }
}
