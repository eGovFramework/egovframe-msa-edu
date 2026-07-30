package org.egovframe.cloud.reserverequestservice.api.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.egovframe.cloud.reserverequestservice.domain.Reserve;
import org.egovframe.cloud.reserverequestservice.domain.ReserveStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReserveSaveRequestDtoTest {

    private ReserveSaveRequestDto buildDto() {
        return ReserveSaveRequestDto.builder()
                .reserveItemId(1L)
                .locationId(10L)
                .categoryId("equipment")
                .totalQty(5)
                .reserveQty(2)
                .reservePurposeContent("테스트 예약 목적")
                .reserveStartDate(LocalDateTime.of(2024, 6, 1, 9, 0))
                .reserveEndDate(LocalDateTime.of(2024, 6, 3, 18, 0))
                .userId("testUser")
                .userContactNo("010-1234-5678")
                .userEmail("test@example.com")
                .reserveMeansId("realtime")
                .operationStartDate(LocalDateTime.of(2024, 5, 1, 0, 0))
                .operationEndDate(LocalDateTime.of(2024, 12, 31, 23, 59))
                .requestStartDate(LocalDateTime.of(2024, 5, 1, 0, 0))
                .requestEndDate(LocalDateTime.of(2024, 12, 31, 23, 59))
                .isPeriod(false)
                .build();
    }

    @Test
    @DisplayName("builder로 생성한 DTO의 필드가 올바르게 설정된다")
    void builder_sets_fields_correctly() {
        ReserveSaveRequestDto dto = buildDto();

        assertThat(dto.getReserveItemId()).isEqualTo(1L);
        assertThat(dto.getCategoryId()).isEqualTo("equipment");
        assertThat(dto.getReserveQty()).isEqualTo(2);
        assertThat(dto.getUserId()).isEqualTo("testUser");
        assertThat(dto.getUserEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("createRequestReserve는 상태를 REQUEST로 설정하고 Reserve 엔티티를 반환한다")
    void create_request_reserve_sets_status_to_request() {
        ReserveSaveRequestDto dto = buildDto();

        Reserve reserve = dto.createRequestReserve();

        assertThat(reserve.getReserveStatusId()).isEqualTo(ReserveStatus.REQUEST.getKey());
        assertThat(reserve.getReserveId()).isNotNull();
        assertThat(reserve.getReserveItemId()).isEqualTo(1L);
        assertThat(reserve.getUserId()).isEqualTo("testUser");
    }

    @Test
    @DisplayName("createApproveReserve는 상태를 APPROVE로 설정하고 Reserve 엔티티를 반환한다")
    void create_approve_reserve_sets_status_to_approve() {
        ReserveSaveRequestDto dto = buildDto();

        Reserve reserve = dto.createApproveReserve();

        assertThat(reserve.getReserveStatusId()).isEqualTo(ReserveStatus.APPROVE.getKey());
        assertThat(reserve.getReserveId()).isNotNull();
    }

    @Test
    @DisplayName("toEntity는 DTO의 값을 Reserve 엔티티로 변환한다")
    void to_entity_maps_fields() {
        ReserveSaveRequestDto dto = buildDto();
        dto.setReserveId("test-reserve-id");
        dto.setReserveStatusId(ReserveStatus.REQUEST.getKey());

        Reserve reserve = dto.toEntity();

        assertThat(reserve.getReserveId()).isEqualTo("test-reserve-id");
        assertThat(reserve.getLocationId()).isEqualTo(10L);
        assertThat(reserve.getCategoryId()).isEqualTo("equipment");
        assertThat(reserve.getReservePurposeContent()).isEqualTo("테스트 예약 목적");
        assertThat(reserve.getUserContactNo()).isEqualTo("010-1234-5678");
    }

    @Test
    @DisplayName("createRequestReserve를 두 번 호출하면 각각 다른 reserveId가 생성된다")
    void create_request_reserve_generates_unique_id() {
        ReserveSaveRequestDto dto1 = buildDto();
        ReserveSaveRequestDto dto2 = buildDto();

        Reserve reserve1 = dto1.createRequestReserve();
        Reserve reserve2 = dto2.createRequestReserve();

        assertThat(reserve1.getReserveId()).isNotEqualTo(reserve2.getReserveId());
    }
}
