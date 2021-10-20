package org.egovframe.cloud.portalservice.domain.menu;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * org.egovframe.cloud.portalservice.domain.menu.MenuRepository
 * <p>
 * 메뉴관리 > Menu 엔티티를 위한 Repository
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
public interface MenuRepository extends JpaRepository<Menu, Long>, MenuRepositoryCustom {
}
