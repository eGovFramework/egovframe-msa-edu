package org.egovframe.cloud.reservechecksevice.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.egovframe.cloud.reservechecksevice.client.ReserveItemServiceClient;
import org.egovframe.cloud.reservechecksevice.client.UserServiceClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.r2dbc.h2.H2ConnectionConfiguration;
import io.r2dbc.h2.H2ConnectionFactory;
import io.r2dbc.h2.H2ConnectionOption;

/**
 * {@link ReserveRepositoryImpl#updateStatusIfCurrentStatusIn}가 실제 DB(H2, in-memory) 위에서
 * 진짜 동시 호출에도 정확히 한 번만 상태를 반영하는지 검증한다.
 * <p>
 * Spring ApplicationContext 없이 H2ConnectionFactory 를 직접 만들어 붙인다 - 이 테스트가 증명하려는 것은
 * "findById 후 메모리에서 상태를 확인하는 방식(check-then-act)"과 달리, WHERE 조건이 포함된 단일 UPDATE
 * 문은 DB가 대상 행에 거는 잠금 범위 안에서 조건 확인과 반영을 함께 처리하므로 동시 호출에도 최대 1건만
 * 성공한다는 것이다. Mockito로 순서를 제어하는 단위 테스트로는 이 보장을 증명할 수 없다 - 실제 DB가 필요하다.
 */
class ReserveRepositoryImplConcurrencyTest {

    private static final int ROUNDS = 50;

    private static H2ConnectionFactory connectionFactory;
    private static R2dbcEntityTemplate entityTemplate;
    private static ReserveRepositoryImpl repository;

    @BeforeAll
    static void setUpDatabase() {
        connectionFactory = new H2ConnectionFactory(H2ConnectionConfiguration.builder()
                .inMemory("reserve_concurrency_testdb")
                .property(H2ConnectionOption.DB_CLOSE_DELAY, "-1")
                .username("sa")
                .build());

        // 이 테스트에서 필요한 컬럼만 담은 최소 스키마. 서비스 전체가 함께 쓰는
        // src/test/resources/schema-h2.sql 은 손대지 않는다 - D-3 범위 밖이다.
        DatabaseClient.create(connectionFactory)
                .sql("CREATE TABLE reserve ("
                        + "reserve_id VARCHAR(255) NOT NULL PRIMARY KEY,"
                        + "reserve_item_id BIGINT,"
                        + "category_id VARCHAR(255),"
                        + "reserve_qty BIGINT,"
                        + "reserve_purpose_content VARCHAR(4000),"
                        + "reserve_start_date DATETIME,"
                        + "reserve_end_date DATETIME,"
                        + "reserve_status_id VARCHAR(20),"
                        + "reason_cancel_content VARCHAR(4000),"
                        + "user_id VARCHAR(255),"
                        + "user_contact_no VARCHAR(50),"
                        + "user_email_addr VARCHAR(500),"
                        + "create_date DATETIME,"
                        + "created_by VARCHAR(255),"
                        + "modified_date DATETIME,"
                        + "last_modified_by VARCHAR(255)"
                        + ")")
                .then()
                .block();

        entityTemplate = new R2dbcEntityTemplate(connectionFactory);
        repository = new ReserveRepositoryImpl(
                entityTemplate,
                mock(ReserveItemServiceClient.class),
                mock(UserServiceClient.class),
                CircuitBreakerRegistry.ofDefaults());
    }

    @AfterAll
    static void tearDownDatabase() {
        entityTemplate.delete(Reserve.class).all().block();
    }

    private Reserve newRequestReserve(String reserveId) {
        return Reserve.builder()
                .reserveId(reserveId)
                .reserveItemId(1L)
                .categoryId("education")
                .reserveQty(4)
                .reservePurposeContent("test")
                .reserveStartDate(LocalDateTime.of(2026, 3, 1, 0, 0))
                .reserveEndDate(LocalDateTime.of(2026, 3, 2, 0, 0))
                .reserveStatusId(ReserveStatus.REQUEST.getKey())
                .userId("user")
                .userEmail("user@email.com")
                .userContactNo("contact")
                .build();
    }

    @Test
    @DisplayName("순차 호출 - 두 번째 승인 호출은 반영되지 않는다")
    void sequentialDoubleApproveOnlyFirstSucceeds() {
        String reserveId = "seq-" + System.nanoTime();
        entityTemplate.insert(newRequestReserve(reserveId)).block();

        Long first = repository.updateStatusIfCurrentStatusIn(
                reserveId, List.of(ReserveStatus.REQUEST.getKey()), ReserveStatus.APPROVE.getKey()).block();
        Long second = repository.updateStatusIfCurrentStatusIn(
                reserveId, List.of(ReserveStatus.REQUEST.getKey()), ReserveStatus.APPROVE.getKey()).block();

        assertThat(first).isEqualTo(1L);
        assertThat(second).isEqualTo(0L);
    }

    @Test
    @DisplayName("진짜 동시 호출 - 승인 요청 두 건이 정확히 같은 순간에 들어와도 정확히 1건만 반영된다")
    void concurrentDoubleApproveExactlyOneSucceeds() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        AtomicInteger bothSucceededRounds = new AtomicInteger();
        AtomicInteger bothFailedRounds = new AtomicInteger();
        AtomicInteger exactlyOneSucceededRounds = new AtomicInteger();

        try {
            for (int round = 0; round < ROUNDS; round++) {
                String reserveId = "conc-" + round + "-" + System.nanoTime();
                entityTemplate.insert(newRequestReserve(reserveId)).block();

                CyclicBarrier barrier = new CyclicBarrier(2);

                Future<Long> callerA = pool.submit(() -> callApprove(reserveId, barrier));
                Future<Long> callerB = pool.submit(() -> callApprove(reserveId, barrier));

                long updatedByA = callerA.get(10, TimeUnit.SECONDS);
                long updatedByB = callerB.get(10, TimeUnit.SECONDS);
                long totalUpdated = updatedByA + updatedByB;

                if (totalUpdated == 2) {
                    bothSucceededRounds.incrementAndGet();
                } else if (totalUpdated == 0) {
                    bothFailedRounds.incrementAndGet();
                } else {
                    exactlyOneSucceededRounds.incrementAndGet();
                }

                // 최종 DB 상태도 approve 정확히 1회 반영을 재확인한다 - 재고 이중 반영 여부의 근거.
                Reserve finalState = entityTemplate
                        .selectOne(org.springframework.data.relational.core.query.Query
                                .query(org.springframework.data.relational.core.query.Criteria
                                        .where("reserve_id").is(reserveId)), Reserve.class)
                        .block();
                assertThat(finalState.getReserveStatusId())
                        .as("라운드 %d - 두 번 반영됐다면 approve 대신 다른 상태가 남거나 데이터가 오염될 수 있다", round)
                        .isEqualTo(ReserveStatus.APPROVE.getKey());
            }
        } finally {
            pool.shutdownNow();
        }

        assertThat(bothSucceededRounds.get())
                .as("두 호출이 동시에 성공한(재고 이중 반영에 해당) 라운드 수 - 반드시 0이어야 한다")
                .isZero();
        assertThat(exactlyOneSucceededRounds.get())
                .as("정확히 한 쪽만 성공한 라운드 수 - 전체 라운드 수와 같아야 한다")
                .isEqualTo(ROUNDS);
        assertThat(bothFailedRounds.get())
                .as("둘 다 실패한(정상 승인 자체가 막힌) 라운드 수 - 반드시 0이어야 한다")
                .isZero();
    }

    private long callApprove(String reserveId, CyclicBarrier barrier) {
        try {
            barrier.await(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        Long updated = repository.updateStatusIfCurrentStatusIn(
                reserveId, List.of(ReserveStatus.REQUEST.getKey()), ReserveStatus.APPROVE.getKey()).block();
        return updated == null ? 0L : updated;
    }
}
