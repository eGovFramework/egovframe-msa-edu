package org.egovframe.cloud.reservechecksevice.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.egovframe.cloud.common.util.MessageUtil;
import org.egovframe.cloud.reservechecksevice.api.dto.ReserveSaveRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.validation.ConstraintValidatorContext;

/**
 * {@link ReserveSaveValidator}가 여러 요청에서 동시에 호출돼도 판정 결과를 서로 침범하지 않는지 검증한다.
 */
class ReserveSaveValidatorConcurrencyTest {

    private static final int THREADS = 16;
    private static final int ROUNDS = 300;

    private ReserveSaveValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ReserveSaveValidator();
        MessageUtil messageUtil = mock(MessageUtil.class);
        when(messageUtil.getMessage(anyString())).thenReturn("message");
        when(messageUtil.getMessage(anyString(), any(Object[].class))).thenReturn("message");
        ReflectionTestUtils.setField(validator, "messageUtil", messageUtil);
    }

    /** 예약 시작일이 종료일보다 늦어 반드시 거부돼야 하는 요청. */
    private ReserveSaveRequestDto invalidDto() {
        return ReserveSaveRequestDto.builder()
                .categoryId("equipment")
                .reserveQty(1)
                .reserveStartDate(LocalDateTime.of(2026, 3, 2, 0, 0))
                .reserveEndDate(LocalDateTime.of(2026, 3, 1, 0, 0))
                .build();
    }

    /** 모든 조건을 만족해 통과해야 하는 요청. */
    private ReserveSaveRequestDto validDto() {
        return ReserveSaveRequestDto.builder()
                .categoryId("equipment")
                .reserveQty(1)
                .reserveStartDate(LocalDateTime.of(2026, 3, 1, 0, 0))
                .reserveEndDate(LocalDateTime.of(2026, 3, 2, 0, 0))
                .build();
    }

    @Test
    @DisplayName("한 인스턴스를 동시에 호출해도 잘못된 요청이 통과하지 않는다")
    void concurrentValidationDoesNotLeakVerdicts() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(THREADS);
        AtomicInteger invalidAcceptedCount = new AtomicInteger();
        AtomicInteger validRejectedCount = new AtomicInteger();

        for (int t = 0; t < THREADS; t++) {
            final boolean useInvalid = t % 2 == 0;
            pool.execute(() -> {
                try {
                    start.await();
                    for (int i = 0; i < ROUNDS; i++) {
                        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class, RETURNS_DEEP_STUBS);
                        boolean result = validator.isValid(useInvalid ? invalidDto() : validDto(), context);
                        if (useInvalid && result) {
                            invalidAcceptedCount.incrementAndGet();
                        }
                        if (!useInvalid && !result) {
                            validRejectedCount.incrementAndGet();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        assertThat(done.await(60, TimeUnit.SECONDS)).isTrue();
        pool.shutdownNow();

        assertThat(invalidAcceptedCount.get())
                .as("예약 기간이 뒤집힌 요청이 통과한 횟수")
                .isZero();
        assertThat(validRejectedCount.get())
                .as("정상 요청이 거부된 횟수")
                .isZero();
    }

    @Test
    @DisplayName("단일 스레드에서는 요청별 판정이 그대로 유지된다")
    void singleThreadedValidationKeepsVerdicts() {
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class, RETURNS_DEEP_STUBS);

        assertThat(validator.isValid(invalidDto(), context)).isFalse();
        assertThat(validator.isValid(validDto(), context)).isTrue();
        assertThat(validator.isValid(invalidDto(), context)).isFalse();
    }
}
