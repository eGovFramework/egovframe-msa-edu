package org.egovframe.cloud.reserverequestservice.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReserveStatusTest {

    @Test
    @DisplayName("REQUEST 상태의 key와 title이 올바르게 반환된다")
    void request_status_key_and_title() {
        assertThat(ReserveStatus.REQUEST.getKey()).isEqualTo("request");
        assertThat(ReserveStatus.REQUEST.getTitle()).isEqualTo("예약 신청");
    }

    @Test
    @DisplayName("APPROVE 상태의 key와 title이 올바르게 반환된다")
    void approve_status_key_and_title() {
        assertThat(ReserveStatus.APPROVE.getKey()).isEqualTo("approve");
        assertThat(ReserveStatus.APPROVE.getTitle()).isEqualTo("예약 승인");
    }

    @Test
    @DisplayName("CANCEL 상태의 key와 title이 올바르게 반환된다")
    void cancel_status_key_and_title() {
        assertThat(ReserveStatus.CANCEL.getKey()).isEqualTo("cancel");
        assertThat(ReserveStatus.CANCEL.getTitle()).isEqualTo("예약 취소");
    }

    @Test
    @DisplayName("DONE 상태의 key와 title이 올바르게 반환된다")
    void done_status_key_and_title() {
        assertThat(ReserveStatus.DONE.getKey()).isEqualTo("done");
        assertThat(ReserveStatus.DONE.getTitle()).isEqualTo("완료");
    }

    @Test
    @DisplayName("ReserveStatus는 총 4개의 상태값을 가진다")
    void reserve_status_total_count() {
        assertThat(ReserveStatus.values()).hasSize(4);
    }

    @Test
    @DisplayName("name()으로 enum 상수를 조회할 수 있다")
    void reserve_status_value_of() {
        assertThat(ReserveStatus.valueOf("REQUEST")).isEqualTo(ReserveStatus.REQUEST);
        assertThat(ReserveStatus.valueOf("CANCEL")).isEqualTo(ReserveStatus.CANCEL);
    }
}
