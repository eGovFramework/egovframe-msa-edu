package org.egovframe.cloud.reservechecksevice.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReserveStatusTest {

    @Test
    @DisplayName("각 예약 상태의 key와 title이 정확하게 정의된다")
    void 예약_상태_key_title_검증() {
        assertThat(ReserveStatus.REQUEST.getKey()).isEqualTo("request");
        assertThat(ReserveStatus.REQUEST.getTitle()).isEqualTo("예약 신청");

        assertThat(ReserveStatus.APPROVE.getKey()).isEqualTo("approve");
        assertThat(ReserveStatus.APPROVE.getTitle()).isEqualTo("예약 승인");

        assertThat(ReserveStatus.CANCEL.getKey()).isEqualTo("cancel");
        assertThat(ReserveStatus.CANCEL.getTitle()).isEqualTo("예약 취소");

        assertThat(ReserveStatus.DONE.getKey()).isEqualTo("done");
        assertThat(ReserveStatus.DONE.getTitle()).isEqualTo("완료");
    }

    @Test
    @DisplayName("isEquals는 key 문자열이 일치할 때 true를 반환한다")
    void isEquals_일치하는_key_true반환() {
        assertThat(ReserveStatus.REQUEST.isEquals("request")).isTrue();
        assertThat(ReserveStatus.APPROVE.isEquals("approve")).isTrue();
        assertThat(ReserveStatus.CANCEL.isEquals("cancel")).isTrue();
        assertThat(ReserveStatus.DONE.isEquals("done")).isTrue();
    }

    @Test
    @DisplayName("isEquals는 key 문자열이 다를 때 false를 반환한다")
    void isEquals_다른_key_false반환() {
        assertThat(ReserveStatus.REQUEST.isEquals("approve")).isFalse();
        assertThat(ReserveStatus.REQUEST.isEquals("cancel")).isFalse();
        assertThat(ReserveStatus.REQUEST.isEquals("REQUEST")).isFalse();
        assertThat(ReserveStatus.REQUEST.isEquals("")).isFalse();
    }

    @Test
    @DisplayName("전체 enum 상수 개수는 4개이다")
    void 예약_상태_상수_개수_검증() {
        assertThat(ReserveStatus.values()).hasSize(4);
    }
}
