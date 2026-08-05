package org.egovframe.cloud.reserveitemservice.api.location.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.egovframe.cloud.reserveitemservice.domain.location.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * org.egovframe.cloud.reserveitemservice.api.location.dto.LocationSaveRequestDtoTest
 * <p>
 * 예약 지역 저장 요청 dto 단위 테스트
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
class LocationSaveRequestDtoTest {

    @DisplayName("빌더로 생성한 LocationSaveRequestDto 의 필드 값이 올바르게 설정된다")
    @Test
    void builder_sets_fields_correctly() {
        LocationSaveRequestDto dto = LocationSaveRequestDto.builder()
                .locationName("서울")
                .sortSeq(1)
                .isUse(true)
                .build();

        assertThat(dto.getLocationName()).isEqualTo("서울");
        assertThat(dto.getSortSeq()).isEqualTo(1);
        assertThat(dto.getIsUse()).isTrue();
    }

    @DisplayName("toEntity 호출 시 Location 엔티티가 올바르게 생성된다")
    @Test
    void toEntity_creates_location_with_same_values() {
        LocationSaveRequestDto dto = LocationSaveRequestDto.builder()
                .locationName("부산")
                .sortSeq(2)
                .isUse(false)
                .build();

        Location entity = dto.toEntity();

        assertThat(entity.getLocationName()).isEqualTo("부산");
        assertThat(entity.getSortSeq()).isEqualTo(2);
        assertThat(entity.getIsUse()).isFalse();
    }

    @DisplayName("isUse 가 null 이어도 toEntity 가 정상 동작한다")
    @Test
    void toEntity_handles_null_isUse() {
        LocationSaveRequestDto dto = LocationSaveRequestDto.builder()
                .locationName("대전")
                .sortSeq(3)
                .isUse(null)
                .build();

        Location entity = dto.toEntity();

        assertThat(entity.getLocationName()).isEqualTo("대전");
        assertThat(entity.getIsUse()).isNull();
    }
}
