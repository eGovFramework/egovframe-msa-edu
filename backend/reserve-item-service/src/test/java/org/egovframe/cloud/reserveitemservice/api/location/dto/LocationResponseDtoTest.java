package org.egovframe.cloud.reserveitemservice.api.location.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.egovframe.cloud.reserveitemservice.domain.location.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * org.egovframe.cloud.reserveitemservice.api.location.dto.LocationResponseDtoTest
 * <p>
 * 예약 지역 응답 dto 단위 테스트
 *
 * @author 표준프레임워크센터
 * @version 1.0
 * @since 2024/01/01
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2024/01/01    contributors  최초 생성
 * </pre>
 */
class LocationResponseDtoTest {

    @DisplayName("Location 엔티티로부터 LocationResponseDto 가 올바르게 생성된다")
    @Test
    void constructor_maps_entity_fields_correctly() {
        Location entity = Location.builder()
                .locationId(1L)
                .locationName("서울")
                .sortSeq(1)
                .isUse(true)
                .build();

        LocationResponseDto dto = LocationResponseDto.builder()
                .entity(entity)
                .build();

        assertThat(dto.getLocationId()).isEqualTo(1L);
        assertThat(dto.getLocationName()).isEqualTo("서울");
        assertThat(dto.getSortSeq()).isEqualTo(1);
        assertThat(dto.getIsUse()).isTrue();
    }

    @DisplayName("isUse 가 false 인 엔티티도 올바르게 매핑된다")
    @Test
    void constructor_maps_false_isUse_correctly() {
        Location entity = Location.builder()
                .locationId(2L)
                .locationName("부산")
                .sortSeq(2)
                .isUse(false)
                .build();

        LocationResponseDto dto = LocationResponseDto.builder()
                .entity(entity)
                .build();

        assertThat(dto.getLocationId()).isEqualTo(2L);
        assertThat(dto.getLocationName()).isEqualTo("부산");
        assertThat(dto.getIsUse()).isFalse();
    }
}
