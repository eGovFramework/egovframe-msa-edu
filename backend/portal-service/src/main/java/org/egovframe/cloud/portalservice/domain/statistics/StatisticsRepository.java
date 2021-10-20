package org.egovframe.cloud.portalservice.domain.statistics;

import org.egovframe.cloud.portalservice.api.statistics.dto.StatisticsResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * org.egovframe.cloud.portalservice.domain.statistics.StatisticsRepository
 * <p>
 * 통계 entity를 위한 repository class
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/09/07
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/07    shinmj      최초 생성
 * </pre>
 */
public interface StatisticsRepository extends JpaRepository<Statistics, String>, StatisticsRepositoryCustom {

}
