package org.egovframe.cloud.portalservice.domain.menu;

import org.egovframe.cloud.portalservice.api.menu.dto.MenuRoleResponseDto;
import org.egovframe.cloud.portalservice.api.menu.dto.MenuSideResponseDto;
import org.egovframe.cloud.portalservice.api.menu.dto.MenuTreeResponseDto;

import java.util.List;

/**
 * org.egovframe.cloud.portalservice.domain.menu.MenuRoleRepositoryCustom
 * <p>
 * 메뉴관리 > 권한별 메뉴 querydsl interface
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/08/12
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/08/12    shinmj  최초 생성
 * </pre>
 */
public interface MenuRoleRepositoryCustom {
    List<MenuRoleResponseDto> findTree(String roleId, Long siteId);
    List<MenuSideResponseDto> findMenu(String roleId, Long siteId);
}
