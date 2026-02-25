package org.egovframe.cloud.portalservice.api.statistics;

import java.util.List;

import org.egovframe.cloud.portalservice.api.statistics.dto.StatisticsResponseDto;
import org.egovframe.cloud.portalservice.api.statistics.dto.StatisticsYMRequestDto;
import org.egovframe.cloud.portalservice.service.statistics.StatisticsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
/**
 * org.egovframe.cloud.portalservice.api.statistics.StatisticsApiController
 * <p>
 * 통계 api controller class
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
@Tag(name = "Statistics API", description = "통계 관리 API")
@RequiredArgsConstructor
@RestController
public class StatisticsApiController {
    private final StatisticsService statisticsService;

    /**
     * 접속 통계 월별
     *
     * @param siteId
     * @return
     */
    @GetMapping("/api/v1/statistics/monthly/{siteId}")
    public List<StatisticsResponseDto> findMonthlyBySiteId(@PathVariable("siteId") Long siteId) {
        return statisticsService.findMonthlyBySiteId(siteId);
    }

    /**
     * 접속 통계 일별
     *
     * @param siteId
     * @return
     */
    @GetMapping("/api/v1/statistics/daily/{siteId}")
    public List<StatisticsResponseDto> findDailyBySiteId(@PathVariable("siteId") Long siteId, StatisticsYMRequestDto requestDto) {
        return statisticsService.findDailyBySiteId(siteId, requestDto);
    }

    /**
     * 접속통계 등록
     *
     * @param statisticsId
     * @param request
     */
    @PostMapping("/api/v1/statistics/{statisticsId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@PathVariable("statisticsId") String statisticsId, HttpServletRequest request) {
        statisticsService.save(request, statisticsId);
    }
}
