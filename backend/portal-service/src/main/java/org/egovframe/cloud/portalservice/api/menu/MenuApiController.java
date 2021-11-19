package org.egovframe.cloud.portalservice.api.menu;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.cloud.portalservice.api.menu.dto.*;
import org.egovframe.cloud.portalservice.domain.menu.SiteRepository;
import org.egovframe.cloud.portalservice.service.menu.MenuService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * org.egovframe.cloud.portalservice.api.menu.MenuApiController
 * <p>
 * 메뉴관리 api controller 클래스
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
@RequiredArgsConstructor // final이 선언된 모든 필드를 인자값으로 하는 생성자를 대신 생성하여, 빈을 생성자로 주입받게 한다.
@RestController
public class MenuApiController {

    private final MenuService menuService;

    private final SiteRepository siteRepository;

    /**
     * 사이트 목록 조회
     *
     * @return
     */
    @GetMapping("/api/v1/sites")
    public List<SiteResponseDto> findAllSites() {
        return siteRepository.findAllByIsUseTrueOrderBySortSeq();
    }

    /**
     * 관리자 메뉴 트리 목록 조회
     *
     * @param siteId
     * @return
     */
    @GetMapping("/api/v1/menus/{siteId}/tree")
    public List<MenuTreeResponseDto> findTreeBySiteId(@PathVariable Long siteId) {
        return menuService.findTreeBySiteId(siteId);
    }

    /**
     * 메뉴 상세 정보 한건 조회
     *
     * @param menuId
     * @return
     */
    @GetMapping("/api/v1/menus/{menuId}")
    public MenuResponseDto findById(@PathVariable Long menuId) {
        return menuService.findMenuResponseDtoById(menuId);
    }

    /**
     * 트리 메뉴 한건 추가
     *
     * @param menuTreeRequestDto
     * @return
     */
    @PostMapping(value = "/api/v1/menus")
    @ResponseStatus(HttpStatus.CREATED)
    public MenuTreeResponseDto save(@RequestBody @Valid MenuTreeRequestDto menuTreeRequestDto) {
        return menuService.save(menuTreeRequestDto);
    }

    /**
     * 트리 드래그 앤드 드랍 저장
     *
     * @param siteId
     * @param menuDnDRequestDtoList
     * @return
     */
    @PutMapping(value = "/api/v1/menus/{siteId}/tree")
    public Long saveDnD(@PathVariable Long siteId,  @RequestBody List<MenuDnDRequestDto> menuDnDRequestDtoList) {
        return menuService.updateDnD(siteId, menuDnDRequestDtoList);
    }

    /**
     * 트리에서 메뉴명 변경
     *
     * @param menuId
     * @param name
     * @return
     */
    @PutMapping(value = "/api/v1/menus/{menuId}/{name}")
    public MenuTreeResponseDto updateName(@PathVariable Long menuId, @PathVariable String name) {
        return menuService.updateName(menuId, name);
    }

    /**
     * 메뉴 상세 정보 변경
     *
     * @param menuId
     * @param updateRequestDto
     * @return
     */
    @PutMapping(value = "/api/v1/menus/{menuId}")
    public MenuResponseDto update(@PathVariable Long menuId, @RequestBody MenuUpdateRequestDto updateRequestDto) {
        return menuService.update(menuId, updateRequestDto);
    }

    /**
     * 메뉴 삭제
     *
     * @param menuId
     */
    @DeleteMapping(value = "/api/v1/menus/{menuId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long menuId) {
        menuService.delete(menuId);
    }

}
