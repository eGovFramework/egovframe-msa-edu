package org.egovframe.cloud.portalservice.service.statistics;

import lombok.RequiredArgsConstructor;
import org.egovframe.cloud.common.service.AbstractService;
import org.egovframe.cloud.common.util.LogUtil;
import org.egovframe.cloud.portalservice.api.statistics.dto.StatisticsResponseDto;
import org.egovframe.cloud.portalservice.api.statistics.dto.StatisticsYMRequestDto;
import org.egovframe.cloud.portalservice.domain.statistics.Statistics;
import org.egovframe.cloud.portalservice.domain.statistics.StatisticsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * org.egovframe.cloud.portalservice.service.statistics.StatisticsApiController
 * <p>
 * 통계 service class
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
@RequiredArgsConstructor
@Service
public class StatisticsService extends AbstractService {

    private final StatisticsRepository statisticsRepository;

    /**
     * 접속 통계 월별 조회
     *
     * @param siteId
     * @return
     */
    public List<StatisticsResponseDto> findMonthlyBySiteId(Long siteId) {
        return statisticsRepository.findMonthBySiteId(siteId);
    }

    /**
     * 접속 통계 일별 조회
     *
     * @param siteId
     * @param requestDto
     * @return
     */
    public List<StatisticsResponseDto> findDailyBySiteId(Long siteId, StatisticsYMRequestDto requestDto) {
        return statisticsRepository.findDayBySiteId(siteId, requestDto);
    }


    /**
     * 접속통계 log 입력
     *
     * @param request
     */
    @Transactional
    public void save(HttpServletRequest request, String statisticsId) {
        statisticsRepository.save(
                Statistics.builder()
                        .siteId(LogUtil.getSiteId(request))
                        .statisticsId(statisticsId)
                        .remoteIp(LogUtil.getUserIp())
                        .build()
        );
    }
}
