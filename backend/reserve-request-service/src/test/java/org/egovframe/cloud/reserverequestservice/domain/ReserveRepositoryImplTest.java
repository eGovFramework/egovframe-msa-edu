package org.egovframe.cloud.reserverequestservice.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

import io.r2dbc.h2.H2ConnectionConfiguration;
import io.r2dbc.h2.H2ConnectionFactory;
import io.r2dbc.h2.H2ConnectionOption;

/**
 * ReserveRepositoryImpl 단위 테스트
 *
 * findAllByReserveDate가 취소(cancel) 건을 조회 결과에서 제외하는지 검증한다.
 * 같은 파일의 findAllByReserveDateCount는 이미 이 필터를 적용하고 있다.
 *
 * Spring 컨텍스트를 띄우지 않고 ReserveRepositoryImpl을 직접 생성해 임베디드 H2에
 * 연결한다 - src/test/resources/schema-h2.sql은 별도 결함(COMMENT 절 · BIGINT(18))으로
 * H2에서 파싱되지 않아 @SpringBootTest 컨텍스트 기동 자체가 실패하는 상태이며,
 * 이 결함은 이 테스트가 검증하려는 대상(D-5)과 무관하므로 여기서는 우회한다.
 */
class ReserveRepositoryImplTest {

    private R2dbcEntityTemplate entityTemplate;
    private ReserveRepositoryImpl reserveRepositoryImpl;

    private static final Long RESERVE_ITEM_ID = 100L;
    private static final LocalDateTime QUERY_START = LocalDateTime.of(2026, 8, 10, 0, 0);
    private static final LocalDateTime QUERY_END = LocalDateTime.of(2026, 8, 12, 0, 0);

    @BeforeEach
    void setUp() {
        H2ConnectionFactory connectionFactory = new H2ConnectionFactory(
            H2ConnectionConfiguration.builder()
                .inMemory("reserve_repository_impl_test")
                .property(H2ConnectionOption.DB_CLOSE_DELAY, "-1")
                .username("sa")
                .build());

        entityTemplate = new R2dbcEntityTemplate(connectionFactory);

        entityTemplate.getDatabaseClient()
            .sql("CREATE TABLE reserve ("
                + "reserve_id VARCHAR(255) NOT NULL,"
                + "reserve_item_id BIGINT,"
                + "reserve_qty INT,"
                + "reserve_start_date TIMESTAMP,"
                + "reserve_end_date TIMESTAMP,"
                + "reserve_status_id VARCHAR(20),"
                + "PRIMARY KEY (reserve_id))")
            .then()
            .block();

        reserveRepositoryImpl = new ReserveRepositoryImpl(entityTemplate);
    }

    @AfterEach
    void tearDown() {
        entityTemplate.getDatabaseClient().sql("DROP TABLE reserve").then().block();
    }

    private void insertReserve(String reserveId, String reserveStatusId, int reserveQty) {
        reserveRepositoryImpl.insert(Reserve.builder()
            .reserveId(reserveId)
            .reserveItemId(RESERVE_ITEM_ID)
            .reserveQty(reserveQty)
            .reserveStartDate(QUERY_START)
            .reserveEndDate(QUERY_END)
            .reserveStatusId(reserveStatusId)
            .build()).block();
    }

    @Test
    void 취소된_예약은_기간_겹침_조회_결과에서_제외된다() {
        insertReserve("cancelled-1", ReserveStatus.CANCEL.getKey(), 5);
        insertReserve("active-1", ReserveStatus.REQUEST.getKey(), 3);

        List<Reserve> result = reserveRepositoryImpl
            .findAllByReserveDate(RESERVE_ITEM_ID, QUERY_START, QUERY_END)
            .collectList()
            .block();

        assertThat(result)
            .extracting(Reserve::getReserveId)
            .containsExactly("active-1");
    }

    @Test
    void 취소된_예약만_있으면_조회_결과가_비어있다() {
        insertReserve("cancelled-1", ReserveStatus.CANCEL.getKey(), 5);

        List<Reserve> result = reserveRepositoryImpl
            .findAllByReserveDate(RESERVE_ITEM_ID, QUERY_START, QUERY_END)
            .collectList()
            .block();

        assertThat(result).isEmpty();
    }
}
