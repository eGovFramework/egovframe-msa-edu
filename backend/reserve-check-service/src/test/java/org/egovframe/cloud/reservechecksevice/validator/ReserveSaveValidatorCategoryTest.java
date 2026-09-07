package org.egovframe.cloud.reservechecksevice.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.egovframe.cloud.common.util.MessageUtil;
import org.egovframe.cloud.reservechecksevice.api.dto.ReserveSaveRequestDto;
import org.egovframe.cloud.reservechecksevice.api.dto.ReserveUpdateRequestDto;
import org.egovframe.cloud.reservechecksevice.domain.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.validation.ConstraintValidatorContext;

/**
 * {@link ReserveSaveValidator}가 예약 유형별 분기를 실제 카테고리 키로 판정하는지 검증한다.
 * <p>
 * 공간 예약 분기가 {@code "place"} 를 비교하고 있었으나 세 서비스의 {@link Category} 가 쓰는 키는
 * {@code "space"} 다. 어떤 요청도 이 분기에 들어오지 못해 공간 예약은 신청 기간 검사를 통째로
 * 건너뛰었다. 예약 기간 두 필드에는 {@code @NotNull} 이 없어 이 검증기가 유일한 방어선이다.
 */
class ReserveSaveValidatorCategoryTest {

    private static final LocalDateTime DAY_1 = LocalDateTime.of(2026, 3, 1, 9, 0);
    private static final LocalDateTime DAY_2 = LocalDateTime.of(2026, 3, 2, 18, 0);

    private ReserveSaveValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new ReserveSaveValidator();
        MessageUtil messageUtil = mock(MessageUtil.class);
        when(messageUtil.getMessage(anyString())).thenReturn("message");
        when(messageUtil.getMessage(anyString(), any(Object[].class))).thenReturn("message");
        ReflectionTestUtils.setField(validator, "messageUtil", messageUtil);
        context = mock(ConstraintValidatorContext.class, RETURNS_DEEP_STUBS);
    }

    @Test
    @DisplayName("공간 예약은 신청 기간이 뒤집혀 있으면 거부한다")
    void spaceRejectsReversedPeriod() {
        ReserveSaveRequestDto dto = space(DAY_2, DAY_1);

        assertThat(validator.isValid(dto, context)).isFalse();
    }

    @Test
    @DisplayName("공간 예약은 신청 기간이 없으면 거부한다")
    void spaceRejectsMissingPeriod() {
        assertThat(validator.isValid(space(null, null), context)).isFalse();
        assertThat(validator.isValid(space(DAY_1, null), context)).isFalse();
        assertThat(validator.isValid(space(null, DAY_2), context)).isFalse();
    }

    @Test
    @DisplayName("공간 예약의 정상 신청은 통과한다")
    void spaceAcceptsValidPeriod() {
        assertThat(validator.isValid(space(DAY_1, DAY_2), context)).isTrue();
    }

    @Test
    @DisplayName("공간 예약 수정도 같은 기간 검사를 받는다")
    void spaceUpdateIsCheckedToo() {
        ReserveUpdateRequestDto reversed = ReserveUpdateRequestDto.builder()
                .categoryId(Category.SPACE.getKey())
                .reserveStartDate(DAY_2)
                .reserveEndDate(DAY_1)
                .build();

        assertThat(validator.isValid(reversed, context)).isFalse();
    }

    @Test
    @DisplayName("교육 예약은 신청 인원을 계속 검사한다")
    void educationStillChecksQty() {
        ReserveSaveRequestDto noQty = ReserveSaveRequestDto.builder()
                .categoryId(Category.EDUCATION.getKey())
                .build();
        ReserveSaveRequestDto withQty = ReserveSaveRequestDto.builder()
                .categoryId(Category.EDUCATION.getKey())
                .reserveQty(1)
                .build();

        assertThat(validator.isValid(noQty, context)).isFalse();
        assertThat(validator.isValid(withQty, context)).isTrue();
    }

    @Test
    @DisplayName("장비 예약은 신청 기간과 수량을 계속 검사한다")
    void equipmentStillChecksPeriodAndQty() {
        ReserveSaveRequestDto reversed = ReserveSaveRequestDto.builder()
                .categoryId(Category.EQUIPMENT.getKey())
                .reserveQty(1)
                .reserveStartDate(DAY_2)
                .reserveEndDate(DAY_1)
                .build();
        ReserveSaveRequestDto valid = ReserveSaveRequestDto.builder()
                .categoryId(Category.EQUIPMENT.getKey())
                .reserveQty(1)
                .reserveStartDate(DAY_1)
                .reserveEndDate(DAY_2)
                .build();

        assertThat(validator.isValid(reversed, context)).isFalse();
        assertThat(validator.isValid(valid, context)).isTrue();
    }

    @Test
    @DisplayName("알 수 없는 예약 유형은 유형별 검사 없이 통과한다")
    void unknownCategoryIsNotChecked() {
        ReserveSaveRequestDto unknown = ReserveSaveRequestDto.builder()
                .categoryId("place")
                .reserveStartDate(DAY_2)
                .reserveEndDate(DAY_1)
                .build();

        assertThat(validator.isValid(unknown, context)).isTrue();
    }

    private ReserveSaveRequestDto space(LocalDateTime startDate, LocalDateTime endDate) {
        return ReserveSaveRequestDto.builder()
                .categoryId(Category.SPACE.getKey())
                .reserveStartDate(startDate)
                .reserveEndDate(endDate)
                .build();
    }
}
