package org.egovframe.cloud.portalservice.domain.statistics;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StatisticsRepositoryTest {
    @Autowired
    EntityManager em;

    @Autowired
    StatisticsRepository statisticsRepository;

    /**
     * 단위 테스트가 끝날때마다 수행되는 메소드
     * 테스트 데이터간 침범을 막기 위해 사용
     */
    @AfterEach
    public void cleanUp() {
        statisticsRepository.deleteAll();
    }

    @Test
    public void 접속통계로그_입력된다() throws Exception {
        // given
        String statisticsId = UUID.randomUUID().toString();
        Statistics log = Statistics.builder()
                .statisticsId(statisticsId)
                .siteId(1L)
                .build();

        // when
        Statistics analyticsLog = statisticsRepository.save(log);

        // then
        assertThat(analyticsLog.getStatisticsId()).isEqualTo(statisticsId);
    }
}