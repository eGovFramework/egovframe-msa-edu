package org.egovframe.cloud.reserveitemservice.domain.reserveItem;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

/**
 * org.egovframe.cloud.reserveitemservice.domain.reserveItem.ReserveItemRepository
 *
 * 예약 물품 도메인 repository interface
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/09/09
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/09    shinmj       최초 생성
 * </pre>
 */
public interface ReserveItemRepository extends R2dbcRepository<ReserveItem, Long>, ReserveItemRepositoryCustom {

}
