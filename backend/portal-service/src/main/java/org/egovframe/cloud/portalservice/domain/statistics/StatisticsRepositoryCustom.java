package org.egovframe.cloud.portalservice.domain.statistics;

import org.egovframe.cloud.portalservice.api.statistics.dto.StatisticsResponseDto;
import org.egovframe.cloud.portalservice.api.statistics.dto.StatisticsYMRequestDto;

import java.util.List;

/**
 * org.egovframe.cloud.portalservice.domain.statistics.StatisticsRepositoryCustom
 * <p>
 * 접속통계 Querydsl interface
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/09/07
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/07    jaeyeolkim  최초 생성
 * </pre>
 */
public interface StatisticsRepositoryCustom {
    List<StatisticsResponseDto> findMonthBySiteId(Long siteId);
    List<StatisticsResponseDto> findDayBySiteId(Long siteId, StatisticsYMRequestDto requestDto);
}
