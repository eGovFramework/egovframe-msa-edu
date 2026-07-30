package org.egovframe.cloud.reserverequestservice.api.dto;

import org.egovframe.cloud.reserverequestservice.domain.Reserve;
import org.egovframe.cloud.reserverequestservice.domain.ReserveStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReserveSaveRequestDtoTest {

    private ReserveSaveRequestDto buildDto() {
        return ReserveSaveRequestDto.builder()
                .reserveItemId(101L)
                .locationId(7L)
                .categoryId("CAT")
                .reserveQty(2)
                .reservePurposeContent("회의실 예약 사유")
                .attachmentCode("ATT-1")
                .reserveStartDate(LocalDateTime.of(2026, 6, 1, 10, 0))
                .reserveEndDate(LocalDateTime.of(2026, 6, 1, 12, 0))
                .userId("user-1")
                .userContactNo("010-0000-0000")
                .userEmail("user@example.com")
                .build();
    }

    @Test
    @DisplayName("toEntity는 DTO 필드를 Reserve 엔티티로 그대로 복사한다")
    void toEntity_copiesFields() {
        ReserveSaveRequestDto dto = buildDto();

        Reserve entity = dto.toEntity();

        assertThat(entity).isNotNull();
        assertThat(entity.getReserveItemId()).isEqualTo(101L);
        assertThat(entity.getLocationId()).isEqualTo(7L);
        assertThat(entity.getCategoryId()).isEqualTo("CAT");
        assertThat(entity.getReserveQty()).isEqualTo(2);
        assertThat(entity.getReservePurposeContent()).isEqualTo("회의실 예약 사유");
        assertThat(entity.getAttachmentCode()).isEqualTo("ATT-1");
        assertThat(entity.getUserId()).isEqualTo("user-1");
        assertThat(entity.getUserContactNo()).isEqualTo("010-0000-0000");
        assertThat(entity.getUserEmail()).isEqualTo("user@example.com");
    }

    @Test
    @DisplayName("createRequestReserve는 새 reserveId를 생성하고 상태를 REQUEST로 설정한다")
    void createRequestReserve_assignsIdAndRequestStatus() {
        ReserveSaveRequestDto dto = buildDto();
        assertThat(dto.getReserveId()).isNull();

        Reserve entity = dto.createRequestReserve();

        assertThat(dto.getReserveId()).isNotBlank();
        assertThat(entity.getReserveId()).isEqualTo(dto.getReserveId());
        assertThat(entity.getReserveStatusId()).isEqualTo(ReserveStatus.REQUEST.getKey());
    }

    @Test
    @DisplayName("createApproveReserve는 새 reserveId를 생성하고 상태를 APPROVE로 설정한다")
    void createApproveReserve_assignsIdAndApproveStatus() {
        ReserveSaveRequestDto dto = buildDto();

        Reserve entity = dto.createApproveReserve();

        assertThat(dto.getReserveId()).isNotBlank();
        assertThat(entity.getReserveStatusId()).isEqualTo(ReserveStatus.APPROVE.getKey());
    }

    @Test
    @DisplayName("createRequestReserve와 createApproveReserve는 매번 다른 reserveId를 생성한다")
    void createReserve_generatesUniqueId() {
        ReserveSaveRequestDto first = buildDto();
        ReserveSaveRequestDto second = buildDto();

        first.createRequestReserve();
        second.createRequestReserve();

        assertThat(first.getReserveId()).isNotEqualTo(second.getReserveId());
    }
}