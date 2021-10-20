package org.egovframe.cloud.portalservice.api.statistics.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * org.egovframe.cloud.portalservice.api.statistics.dto.StatisticsResponseDto
 * <p>
 * 통계 차트 응답 dto class
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
@Getter
@NoArgsConstructor
@ToString
public class StatisticsResponseDto {
    private Integer year;
    private Integer month;
    private Integer day;
    private String x;
    private Long y;

    @Builder
    public StatisticsResponseDto(Integer year, Integer month, Integer day, Integer x, Long y) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.x = convertLabel(x);
        this.y = y;
    }

    /**
     * 일/월 에 대한 라벨 설정
     * 01,02....11...31
     *
     * @param x
     * @return
     */
    private String convertLabel(Integer x) {
        if (x < 10) {
            return "0"+x;
        }
        return ""+x;
    }
}
