package org.egovframe.cloud.portalservice.domain.menu;

import org.egovframe.cloud.portalservice.api.menu.dto.MenuResponseDto;
import org.egovframe.cloud.portalservice.api.menu.dto.MenuTreeResponseDto;

import java.util.List;

/**
 * org.egovframe.cloud.portalservice.domain.menu.MenuRepositoryCustom
 * <p>
 * 메뉴관리 > Menu querydsl interface
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
public interface MenuRepositoryCustom {
    List<MenuTreeResponseDto> findTreeBySiteId(Long siteId);
    MenuResponseDto findByIdWithConnectName(Long menuId);
}
