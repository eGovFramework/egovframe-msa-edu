package org.egovframe.cloud.reserveitemservice.service.location;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.reserveitemservice.api.location.dto.LocationResponseDto;
import org.egovframe.cloud.reserveitemservice.api.location.dto.LocationSaveRequestDto;
import org.egovframe.cloud.reserveitemservice.api.location.dto.LocationUpdateRequestDto;
import org.egovframe.cloud.reserveitemservice.domain.location.Location;
import org.egovframe.cloud.reserveitemservice.domain.location.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * org.egovframe.cloud.reserveitemservice.service.location.LocationServiceTest
 *
 * LocationService 단위 테스트
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
@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationService locationService;

    private Location location1;
    private Location location2;

    @BeforeEach
    void setUp() {
        location1 = Location.builder()
                .locationId(1L)
                .locationName("서울")
                .sortSeq(1)
                .isUse(true)
                .build();

        location2 = Location.builder()
                .locationId(2L)
                .locationName("부산")
                .sortSeq(2)
                .isUse(true)
                .build();
    }

    @Test
    @DisplayName("단건 조회 - 존재하는 ID로 조회 시 LocationResponseDto 반환")
    void findById_존재하는_아이디_성공() {
        given(locationRepository.findById(1L)).willReturn(Mono.just(location1));

        Mono<LocationResponseDto> result = locationService.findById(1L);

        StepVerifier.create(result)
                .assertNext(dto -> {
                    assertThat(dto.getLocationId()).isEqualTo(1L);
                    assertThat(dto.getLocationName()).isEqualTo("서울");
                    assertThat(dto.getIsUse()).isTrue();
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("단건 조회 - 존재하지 않는 ID로 조회 시 에러 발생")
    void findById_존재하지_않는_아이디_에러() {
        given(locationRepository.findById(99L)).willReturn(Mono.empty());

        Mono<LocationResponseDto> result = locationService.findById(99L);

        StepVerifier.create(result)
                .expectErrorMatches(ex -> ex.getMessage().contains("99"))
                .verify();
    }

    @Test
    @DisplayName("사용 중인 전체 목록 조회 - isUse=true인 항목만 반환")
    void findAll_사용중인_목록_반환() {
        given(locationRepository.findAllByIsUseTrueOrderBySortSeq())
                .willReturn(Flux.just(location1, location2));

        Flux<LocationResponseDto> result = locationService.findAll();

        StepVerifier.create(result)
                .assertNext(dto -> assertThat(dto.getLocationName()).isEqualTo("서울"))
                .assertNext(dto -> assertThat(dto.getLocationName()).isEqualTo("부산"))
                .verifyComplete();
    }

    @Test
    @DisplayName("저장 - 정상 입력 시 저장된 LocationResponseDto 반환")
    void save_정상_저장() {
        LocationSaveRequestDto saveDto = LocationSaveRequestDto.builder()
                .locationName("대구")
                .sortSeq(3)
                .isUse(true)
                .build();

        Location saved = Location.builder()
                .locationId(3L)
                .locationName("대구")
                .sortSeq(3)
                .isUse(true)
                .build();

        given(locationRepository.save(any(Location.class))).willReturn(Mono.just(saved));

        Mono<LocationResponseDto> result = locationService.save(saveDto);

        StepVerifier.create(result)
                .assertNext(dto -> {
                    assertThat(dto.getLocationId()).isEqualTo(3L);
                    assertThat(dto.getLocationName()).isEqualTo("대구");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("사용여부 수정 - 존재하는 ID의 isUse 값 변경 성공")
    void updateIsUse_정상_수정() {
        given(locationRepository.findById(1L)).willReturn(Mono.just(location1));
        given(locationRepository.save(any(Location.class))).willReturn(Mono.just(location1));

        Mono<Void> result = locationService.updateIsUse(1L, false);

        StepVerifier.create(result)
                .verifyComplete();

        verify(locationRepository).save(any(Location.class));
    }

    @Test
    @DisplayName("검색어 없는 경우 페이지 목록 조회 - 전체 목록 반환")
    void search_검색어_없는_경우_전체_목록() {
        Pageable pageable = PageRequest.of(0, 10);
        RequestDto requestDto = RequestDto.builder().build();

        given(locationRepository.findAllByOrderBySortSeq(pageable))
                .willReturn(Flux.just(location1, location2));
        given(locationRepository.count()).willReturn(Mono.just(2L));

        Mono<Page<LocationResponseDto>> result = locationService.search(requestDto, pageable);

        StepVerifier.create(result)
                .assertNext(page -> {
                    assertThat(page.getTotalElements()).isEqualTo(2L);
                    assertThat(page.getContent()).hasSize(2);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("지역명 검색 - 검색어 포함된 목록 반환")
    void search_지역명_검색어_있는_경우() {
        Pageable pageable = PageRequest.of(0, 10);
        RequestDto requestDto = RequestDto.builder()
                .keywordType("locationName")
                .keyword("서울")
                .build();

        given(locationRepository.findAllByLocationNameContainingOrderBySortSeq(anyString(), any(Pageable.class)))
                .willReturn(Flux.just(location1));
        given(locationRepository.countAllByLocationNameContaining(anyString()))
                .willReturn(Mono.just(1L));

        Mono<Page<LocationResponseDto>> result = locationService.search(requestDto, pageable);

        StepVerifier.create(result)
                .assertNext(page -> {
                    assertThat(page.getTotalElements()).isEqualTo(1L);
                    assertThat(page.getContent().get(0).getLocationName()).isEqualTo("서울");
                })
                .verifyComplete();
    }
}
