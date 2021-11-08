package org.egovframe.cloud.portalservice.api.statistics;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.egovframe.cloud.portalservice.api.statistics.dto.StatisticsResponseDto;
import org.egovframe.cloud.portalservice.domain.statistics.Statistics;
import org.egovframe.cloud.portalservice.domain.statistics.StatisticsRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles(profiles = "test")
class StatisticsApiControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private StatisticsRepository statisticsRepository;

    @BeforeEach
    public void setup() {
        for (int i = 0; i < 10; i++) {
            statisticsRepository.save(Statistics.builder()
                    .siteId(1L)
                    .remoteIp("testip")
                    .build());
        }

    }

    @AfterEach
    public void tearDown() {
        statisticsRepository.deleteAll();
    }

    @Test
    public void 월별접속통계_조회_성공() throws Exception {
        Long siteId = 1L;
        // when
        ResponseEntity< List<StatisticsResponseDto>> responseEntity =
                restTemplate.exchange("/api/v1/statistics/monthly/"+siteId,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<StatisticsResponseDto>>(){});

        responseEntity.getBody().forEach(System.out::println);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().size()).isEqualTo(1);
        assertThat(responseEntity.getBody().get(0).getY()).isEqualTo(10);
    }

    @Test
    public void 일별접속통계_조회_성공() throws Exception {
        Long siteId = 1L;

        LocalDate now = LocalDate.now();

        // when
        ResponseEntity< List<StatisticsResponseDto>> responseEntity =
                restTemplate.exchange("/api/v1/statistics/daily/"+siteId+"?year="+now.getYear()+"&month="+now.getMonthValue(),
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<StatisticsResponseDto>>(){});

        responseEntity.getBody().forEach(System.out::println);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().size()).isEqualTo(1);
        assertThat(responseEntity.getBody().get(0).getY()).isEqualTo(10);
    }

}