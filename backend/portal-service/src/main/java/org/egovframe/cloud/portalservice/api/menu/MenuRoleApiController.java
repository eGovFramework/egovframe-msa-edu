package org.egovframe.cloud.portalservice.api.menu;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.cloud.portalservice.api.menu.dto.MenuRoleRequestDto;
import org.egovframe.cloud.portalservice.api.menu.dto.MenuRoleResponseDto;
import org.egovframe.cloud.portalservice.api.menu.dto.MenuSideResponseDto;
import org.egovframe.cloud.portalservice.domain.user.User;
import org.egovframe.cloud.portalservice.service.menu.MenuRoleService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * org.egovframe.cloud.portalservice.api.menu.MenuRoleApiController
 * <p>
 * 권한별 메뉴관리 api controller 클래스
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/08/17
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/08/17    shinmj  최초 생성
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor // final이 선언된 모든 필드를 인자값으로 하는 생성자를 대신 생성하여, 빈을 생성자로 주입받게 한다.
@RestController
public class MenuRoleApiController {

    private final MenuRoleService menuRoleService;

    /**
     * 권한별 메뉴관리 트리 목록 조회
     *
     * @param roleId
     * @param siteId
     * @return
     */
    @GetMapping("/api/v1/menu-roles/{roleId}/{siteId}")
    public List<MenuRoleResponseDto> findTree(@PathVariable String roleId, @PathVariable Long siteId, User user) {
        return menuRoleService.fineTree(roleId.toUpperCase(), siteId);
    }

    /**
     * 권한별 메뉴 저장
     *
     * @param menuRoleRequestDtoList
     * @return
     */
    @PostMapping("/api/v1/menu-roles")
    @ResponseStatus(HttpStatus.CREATED)
    public String save(@RequestBody List<MenuRoleRequestDto> menuRoleRequestDtoList) {
        return menuRoleService.save(menuRoleRequestDtoList);
    }

    /**
     * 로그인 후 사용자 권한에 따른 메뉴 조회
     *
     * @param siteId
     * @return
     */
    @GetMapping("/api/v1/menu-roles/{siteId}")
    public List<MenuSideResponseDto> findMenus(@PathVariable Long siteId) {
        return menuRoleService.findMenus(siteId);

    }
 }
