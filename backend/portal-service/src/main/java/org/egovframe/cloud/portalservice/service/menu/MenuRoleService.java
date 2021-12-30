package org.egovframe.cloud.portalservice.service.menu;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.cloud.common.exception.EntityNotFoundException;
import org.egovframe.cloud.common.service.AbstractService;
import org.egovframe.cloud.portalservice.api.menu.dto.MenuRoleRequestDto;
import org.egovframe.cloud.portalservice.api.menu.dto.MenuRoleResponseDto;
import org.egovframe.cloud.portalservice.api.menu.dto.MenuSideResponseDto;
import org.egovframe.cloud.portalservice.client.BoardServiceClient;
import org.egovframe.cloud.portalservice.client.dto.BoardResponseDto;
import org.egovframe.cloud.portalservice.domain.menu.Menu;
import org.egovframe.cloud.portalservice.domain.menu.MenuRepository;
import org.egovframe.cloud.portalservice.domain.menu.MenuRole;
import org.egovframe.cloud.portalservice.domain.menu.MenuRoleRepository;
import org.egovframe.cloud.portalservice.domain.user.Role;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * org.egovframe.cloud.portalservice.service.menu.MenuRoleService
 * <p>
 * 권한별 메뉴 관리 서비스 클래스
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
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MenuRoleService extends AbstractService {

    private final MenuRoleRepository menuRoleRepository;
    private final MenuRepository menuRepository;
    private final BoardServiceClient boardServiceClient;
    private final CircuitBreakerFactory circuitBreakerFactory;

    /**
     * 권한별 메뉴 트리 조회
     *
     * @param roleId
     * @param siteId
     * @return
     */
    public List<MenuRoleResponseDto> fineTree(String roleId, Long siteId) {
        return menuRoleRepository.findTree(roleId, siteId);
    }

    /**
     * 권한별 메뉴 저장
     *
     * @param menuRoleRequestDtoList
     * @return
     */
    @Transactional
    public String save(List<MenuRoleRequestDto> menuRoleRequestDtoList) {

        for (MenuRoleRequestDto menuRoleRequestDto : menuRoleRequestDtoList) {
            recursiveSave(menuRoleRequestDto);
        }

        return "Success";
    }


    /**
     * 로그인한 사용자의 권한에 맞는 메뉴 조회
     *
     * @param siteId
     * @return
     */
    public List<MenuSideResponseDto> findMenus(Long siteId) {
        List<MenuSideResponseDto> menuSideResponseDtoList = findMenu(siteId);

        for (MenuSideResponseDto menuSideResponseDto : menuSideResponseDtoList) {
            recursiveSetUrlPath(menuSideResponseDto);
        }

        return menuSideResponseDtoList;
    }

    /**
     * 계층구조 메뉴 조회 로그인 사용자의 권한으로 조회하고 로그인 사용자가 없는 경우 손님(ROLE_ANONYMOUS) 로 조회한다.
     *
     * @param siteId
     * @return
     */
    private List<MenuSideResponseDto> findMenu(Long siteId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
            || authentication instanceof AnonymousAuthenticationToken) {
            return menuRoleRepository.findMenu(Role.ANONYMOUS.getKey(), siteId);
        }
        String role = authentication.getAuthorities().stream()
            .map(GrantedAuthority::toString)
            .collect(Collectors.toList())
            .get(0);
        return menuRoleRepository.findMenu(role, siteId);
    }

    /**
     * 메뉴 유형이 게시판인 경우 해당 게시판의 스킨타입으로 url을 만들어 준다.
     *
     * @param menuSideResponseDto
     */
    private void recursiveSetUrlPath(MenuSideResponseDto menuSideResponseDto) {
        if (Objects.nonNull(menuSideResponseDto.getConnectId()) &&
            menuSideResponseDto.isRequiredUrlPath()) {
            menuSideResponseDto.setUrlPath(getUrlPath(menuSideResponseDto));
        }

        if (!menuSideResponseDto.hasChildren()) {
            return;
        }

        for (MenuSideResponseDto child : menuSideResponseDto.getChildren()) {
            recursiveSetUrlPath(child);
        }
    }

    /**
     * 권한별 메뉴 저장 children 데이터 재귀 호출 checked 인 경우 저장 unchecked 인 경우 삭제
     *
     * @param menuRoleRequestDto
     */
    private void recursiveSave(MenuRoleRequestDto menuRoleRequestDto) {
        saveMenu(menuRoleRequestDto);

        if (Objects.isNull(menuRoleRequestDto.getChildren())
            || menuRoleRequestDto.getChildren().size() <= 0) {
            return;
        }

        for (int i = 0; i < menuRoleRequestDto.getChildren().size(); i++) {
            MenuRoleRequestDto child = menuRoleRequestDto.getChildren().get(i);
            recursiveSave(child);
        }
    }


    /**
     * urlPath 설정
     *
     * @param responseDto
     * @return
     */
    private String getUrlPath(MenuSideResponseDto responseDto) {
        if ("contents".equals(responseDto.getMenuType())) {
            return "/content/" + responseDto.getConnectId();
        }

        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("board");
        BoardResponseDto board = circuitBreaker.run(() ->
                boardServiceClient.findById(responseDto.getConnectId()),
            throwable -> new BoardResponseDto());

        return "/board/" + board.getSkinTypeCode() + "/" + responseDto.getConnectId();

    }

    private Menu findMenuById(Long id) {
        return menuRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                getMessage("valid.notexists.format", new Object[]{getMessage("menu")}) + " ID= "
                    + id));
    }

    /**
     * menuRole 저장
     *
     * @param menuRoleRequestDto
     */
    private void saveMenu(MenuRoleRequestDto menuRoleRequestDto) {
        if (menuRoleRequestDto.getIsChecked()) {
            saveCheckedMenu(menuRoleRequestDto);
            return;
        }

        //unchecked 인 경우 menurole 삭제
        if (menuRoleRequestDto.hasMenuRoleId()) {
            Optional<MenuRole> menuRole = menuRoleRepository
                .findById(menuRoleRequestDto.getMenuRoleId());
            menuRole.ifPresent(it -> menuRoleRepository.delete(it));
        }
    }

    /**
     * checked 인 경우 menuRole 저장
     *
     * @param menuRoleRequestDto
     */
    private void saveCheckedMenu(MenuRoleRequestDto menuRoleRequestDto) {
        Menu menu = findMenuById(menuRoleRequestDto.getId());

        if (!menuRoleRequestDto.hasMenuRoleId()) {
            MenuRole menuRole = MenuRole.builder()
                .roleId(menuRoleRequestDto.getRoleId())
                .menu(menu)
                .build();
            menuRole.setMenu(menu);
            menuRoleRepository.save(menuRole);
            return;
        }

        Optional<MenuRole> menuRole = menuRoleRepository
            .findById(menuRoleRequestDto.getMenuRoleId());
        menuRole.ifPresent(it -> it.setMenu(menu));

    }
}
