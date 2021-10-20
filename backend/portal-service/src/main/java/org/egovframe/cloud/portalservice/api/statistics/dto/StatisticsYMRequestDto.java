package org.egovframe.cloud.portalservice.api.statistics.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class StatisticsYMRequestDto {
    private Integer year;
    private Integer month;

    @Builder
    public StatisticsYMRequestDto(Integer year, Integer month) {
        this.year = year;
        this.month = month;
    }
}
