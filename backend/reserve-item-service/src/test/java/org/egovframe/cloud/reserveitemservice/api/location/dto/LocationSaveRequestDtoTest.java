package org.egovframe.cloud.reserveitemservice.api.location.dto;

import org.egovframe.cloud.reserveitemservice.domain.location.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LocationSaveRequestDtoTest {

    @Test
    @DisplayName("toEntity는 LocationSaveRequestDto의 모든 필드를 Location 엔티티로 복사한다")
    void toEntity_copiesAllFields() {
        LocationSaveRequestDto dto = LocationSaveRequestDto.builder()
                .locationName("서울 본사")
                .sortSeq(10)
                .isUse(true)
                .build();

        Location entity = dto.toEntity();

        assertThat(entity).isNotNull();
        assertThat(entity.getLocationName()).isEqualTo("서울 본사");
        assertThat(entity.getSortSeq()).isEqualTo(10);
        assertThat(entity.getIsUse()).isTrue();
        assertThat(entity.getLocationId()).isNull();
    }

    @Test
    @DisplayName("toEntity는 선택 필드가 null이어도 그대로 전달한다")
    void toEntity_passesThroughNullOptionalFields() {
        LocationSaveRequestDto dto = LocationSaveRequestDto.builder()
                .locationName("부산 지사")
                .build();

        Location entity = dto.toEntity();

        assertThat(entity.getLocationName()).isEqualTo("부산 지사");
        assertThat(entity.getSortSeq()).isNull();
        assertThat(entity.getIsUse()).isNull();
    }

    @Test
    @DisplayName("Builder는 호출된 필드만 채우고 나머지는 null로 둔다")
    void builder_setsOnlyProvidedFields() {
        LocationSaveRequestDto dto = LocationSaveRequestDto.builder()
                .locationName("제주")
                .sortSeq(99)
                .build();

        assertThat(dto.getLocationName()).isEqualTo("제주");
        assertThat(dto.getSortSeq()).isEqualTo(99);
        assertThat(dto.getIsUse()).isNull();
    }
}
