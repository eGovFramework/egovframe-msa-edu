package org.egovframe.cloud.portalservice.domain.statistics;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.egovframe.cloud.portalservice.api.statistics.dto.StatisticsResponseDto;
import org.egovframe.cloud.portalservice.api.statistics.dto.StatisticsYMRequestDto;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

import static org.egovframe.cloud.portalservice.domain.statistics.QStatistics.statistics;

/**
 * org.egovframe.cloud.portalservice.domain.statistics.StatisticsRepositoryImpl
 * <p>
 * 통계 Querydsl 구현 클래스
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
@Repository
public class StatisticsRepositoryImpl implements StatisticsRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 현재 년도의 접속통계 월별 조회
     *
     * @param siteId
     * @return
     */
    @Override
    public List<StatisticsResponseDto> findMonthBySiteId(Long siteId) {
        LocalDateTime now = LocalDateTime.now();

        List<Tuple> tuples = jpaQueryFactory.select(
                Expressions.constant(now.getYear()),
                statistics.createdDate.month().as("month"),
                Expressions.constant(0),
                statistics.createdDate.month().as("x"),
                statistics.id.count())
                .from(statistics)
                .where(
                        statistics.siteId.eq(siteId),
                        statistics.createdDate.between(
                        LocalDateTime.of(now.getYear(), Month.JANUARY, 1, 0, 0),
                        LocalDateTime.of(now.getYear(), Month.DECEMBER, 31, 23, 59)))
                .groupBy(statistics.createdDate.month())
                .orderBy(statistics.createdDate.month().asc())
                .fetch();
        return tuples.stream()
                .map(this::convertDto)
                .collect(Collectors.toList());
    }

    /**
     * 접속통계 일별 조회
     *
     * @param siteId
     * @param requestDto
     * @return
     */
    @Override
    public List<StatisticsResponseDto> findDayBySiteId(Long siteId, StatisticsYMRequestDto requestDto) {
        YearMonth yearMonth = YearMonth.of(requestDto.getYear(), requestDto.getMonth());

        List<Tuple> tuples = jpaQueryFactory.select(
                Expressions.constant(yearMonth.getYear()),
                Expressions.constant(yearMonth.getMonth().getValue()),
                statistics.createdDate.dayOfMonth().as("day"),
                statistics.createdDate.dayOfMonth().as("x"),
                statistics.id.count())
                .from(statistics)
                .where(
                        statistics.siteId.eq(siteId),
                        statistics.createdDate.between(
                        LocalDateTime.of(yearMonth.getYear(), yearMonth.getMonth(), 1, 0, 0),
                        LocalDateTime.of(yearMonth.getYear(), yearMonth.getMonth(), yearMonth.lengthOfMonth(), 23, 59)))
                .groupBy(statistics.createdDate.dayOfMonth())
                .orderBy(statistics.createdDate.dayOfMonth().asc())
                .fetch();

        return tuples.stream()
                .map(this::convertDto)
                .collect(Collectors.toList());
    }

    /**
     * 조회된 tuple을 응답 dto 형태로 변환
     *
     * @param tuple
     * @return
     */
    private StatisticsResponseDto convertDto(Tuple tuple) {
        return StatisticsResponseDto.builder()
                .year(tuple.get(0, Integer.class))
                .month(tuple.get(1, Integer.class))
                .day(tuple.get(2, Integer.class))
                .x(tuple.get(3, Integer.class))
                .y(tuple.get(4, Long.class))
                .build();
    }
}
