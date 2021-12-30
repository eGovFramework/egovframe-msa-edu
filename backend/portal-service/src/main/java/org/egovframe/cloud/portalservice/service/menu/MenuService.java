package org.egovframe.cloud.portalservice.service.menu;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.cloud.common.exception.BusinessMessageException;
import org.egovframe.cloud.common.exception.EntityNotFoundException;
import org.egovframe.cloud.common.service.AbstractService;
import org.egovframe.cloud.portalservice.api.menu.dto.MenuDnDRequestDto;
import org.egovframe.cloud.portalservice.api.menu.dto.MenuResponseDto;
import org.egovframe.cloud.portalservice.api.menu.dto.MenuTreeRequestDto;
import org.egovframe.cloud.portalservice.api.menu.dto.MenuTreeResponseDto;
import org.egovframe.cloud.portalservice.api.menu.dto.MenuUpdateRequestDto;
import org.egovframe.cloud.portalservice.domain.menu.Menu;
import org.egovframe.cloud.portalservice.domain.menu.MenuRepository;
import org.egovframe.cloud.portalservice.domain.menu.Site;
import org.egovframe.cloud.portalservice.domain.menu.SiteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional
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
    @Transactional(readOnly = true)
    public List<MenuTreeResponseDto> findTreeBySiteId(Long siteId) {
        return menuRepository.findTreeBySiteId(siteId);
    }

    /**
     * 메뉴 한건 조회
     *
     * @param menuId
     * @return
     */
    @Transactional(readOnly = true)
    public MenuResponseDto findMenuResponseDtoById(Long menuId) {
        return menuRepository.findByIdWithConnectName(menuId);
    }

    /**
     * 메뉴 트리 한건 추가
     *
     * @param menuTreeRequestDto
     * @return
     */
    public MenuTreeResponseDto save(MenuTreeRequestDto menuTreeRequestDto) {
        Site site = findSite(menuTreeRequestDto.getSiteId());

        Optional<Menu> parentMenu = findParentMenu(menuTreeRequestDto.getParentId());

        Menu menu = menuRepository.save(menuTreeRequestDto.toEntity(parentMenu, site));
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
    public MenuTreeResponseDto updateName(Long menuId, String name) throws EntityNotFoundException {
        Menu menu = findById(menuId);

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
    public MenuResponseDto update(Long menuId, MenuUpdateRequestDto updateRequestDto) throws EntityNotFoundException, BusinessMessageException {
        Menu menu = findById(menuId);

        validateUpdate(updateRequestDto);

        menu.updateDetail(updateRequestDto);

        return MenuResponseDto.builder()
                .entity(menu).build();
    }

    /**
     * 메뉴 한건 삭제
     *
     * @param menuId
     */
    public void delete(Long menuId) {
        menuRepository.delete(findById(menuId));
    }

    /**
     * 트리 드래그 앤드 드랍 저장
     *
     * @param siteId
     * @param menuDnDRequestDtoList
     * @return
     */
    public Long updateDnD(Long siteId, List<MenuDnDRequestDto> menuDnDRequestDtoList) {
        for (int i = 0; i < menuDnDRequestDtoList.size(); i++) {
            MenuDnDRequestDto requestDto = menuDnDRequestDtoList.get(i);
            Optional<Menu> parentMenu = findParentMenu(requestDto.getParentId());

            recursive(requestDto, parentMenu, requestDto.getSortSeq(), requestDto.getLevel());
        }
        return siteId;
    }

    private Optional<Menu> findParentMenu(Long parentId) {
        if (Objects.isNull(parentId)) {
            return Optional.empty();
        }
        return menuRepository.findById(parentId);
    }

    private Menu findById(Long id) {
        return menuRepository.findById(id)
            .orElseThrow(() ->
                new EntityNotFoundException(getMessage("valid.notexists.format", new Object[]{getMessage("menu")}) + " ID= " + id));
    }

    private Site findSite(Long id) {
        return siteRepository.findById(id)
            .orElseThrow(() ->
                new EntityNotFoundException(getMessage("valid.notexists.format", new Object[]{getMessage("menu.site")}) + " ID= " + id));
    }

    /**
     * 메뉴 정합성 체크
     *
     * @param updateRequestDto
     */
    private void validateUpdate(MenuUpdateRequestDto updateRequestDto) {
        //컨텐츠 or 게시판인 경우 connectId 필수
        if (!updateRequestDto.hasConnectId()) {
            //컨텐츠 or 게시판을 선택해 주세요
            throw new BusinessMessageException(getMessage("valid.selection.format", new Object[]{updateRequestDto.getMenuTypeName()}));
        }

        // 내부링크 or 외부링크인 경우 링크 url 필수
        if (!updateRequestDto.hasUrlPath()) {
            //링크 Url 값은 필수 입니다.
            throw new BusinessMessageException(getMessage("valid.required", new Object[]{getMessage("menu.url_path")}));
        }
    }

    /**
     * 트리 드래그 앤드 드랍 시 children 데이터 재귀호출 저장
     *
     * @param dto
     * @param parent
     * @param sortSeq
     * @param level
     */
    private void recursive(MenuDnDRequestDto dto, Optional<Menu> parent, Integer sortSeq, Integer level) {
        Menu menu = findById(dto.getMenuId());

        menu.updateDnD(parent, sortSeq, level);

        if (Objects.isNull(dto.getChildren()) || dto.getChildren().size() <= 0) {
            return;
        }

        for (int i = 0; i < dto.getChildren().size(); i++) {
            MenuDnDRequestDto child = dto.getChildren().get(i);
            recursive(child, Optional.of(menu), child.getSortSeq(), menu.getLevel()+1);
        }
    }
}
