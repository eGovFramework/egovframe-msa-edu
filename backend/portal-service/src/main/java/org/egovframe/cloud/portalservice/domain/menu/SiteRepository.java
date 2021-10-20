package org.egovframe.cloud.portalservice.domain.menu;

import org.egovframe.cloud.portalservice.api.menu.dto.SiteResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * org.egovframe.cloud.portalservice.domain.menu.SiteRepository
 * <p>
 * 메뉴관리 > Site 도메인 Repository
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
public interface SiteRepository extends JpaRepository<Site, Long> {
    List<SiteResponseDto> findAllByIsUseTrueOrderBySortSeq();
}
