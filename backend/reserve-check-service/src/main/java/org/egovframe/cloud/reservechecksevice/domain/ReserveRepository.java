package org.egovframe.cloud.reservechecksevice.domain;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

/**
 * org.egovframe.cloud.reservechecksevice.domain.ReserveRepository
 *
 * 예약 도메인 Repository interface
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/09/15
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/15    shinmj       최초 생성
 * </pre>
 */
public interface ReserveRepository extends R2dbcRepository<Reserve, String>, ReserveRepositoryCustom {
}
