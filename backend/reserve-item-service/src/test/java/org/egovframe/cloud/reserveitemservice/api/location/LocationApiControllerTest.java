package org.egovframe.cloud.reserveitemservice.api.location;

import org.egovframe.cloud.reserveitemservice.api.location.dto.LocationSaveRequestDto;
import org.egovframe.cloud.reserveitemservice.api.location.dto.LocationUpdateRequestDto;
import org.egovframe.cloud.reserveitemservice.config.R2dbcConfig;
import org.egovframe.cloud.reserveitemservice.domain.location.Location;
import org.egovframe.cloud.reserveitemservice.domain.location.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles("test")
@Import({R2dbcConfig.class})
class LocationApiControllerTest {

    @MockBean
    private LocationRepository locationRepository;

    @Autowired
    private WebTestClient webTestClient;

    private final static String API_URL = "/api/v1/locations";

    private Location location = Location.builder()
            .locationId(1L)
            .locationName("location")
            .isUse(true)
            .sortSeq(1)
            .build();

    @BeforeEach
    public void setup() {
       BDDMockito.when(locationRepository.findById(ArgumentMatchers.anyLong()))
               .thenReturn(Mono.just(location));
       //조회조건 있는 경우
       BDDMockito.when(locationRepository.findAllByLocationNameContainingOrderBySortSeq(
               ArgumentMatchers.anyString(), ArgumentMatchers.any(Pageable.class)))
               .thenReturn(Flux.just(location));
       BDDMockito.when(locationRepository.countAllByLocationNameContaining(ArgumentMatchers.anyString()))
               .thenReturn(Mono.just(1L));
       //조회조건 없는 경우
       BDDMockito.when(locationRepository.findAllByOrderBySortSeq(ArgumentMatchers.any(Pageable.class)))
               .thenReturn(Flux.just(location));
       BDDMockito.when(locationRepository.count()).thenReturn(Mono.just(1L));

       BDDMockito.when(locationRepository.save(ArgumentMatchers.any(Location.class)))
               .thenReturn(Mono.just(location));

    }

    @Test
    public void 한건조회_성공() throws Exception {
        webTestClient.get()
                .uri(API_URL+"/{locationId}", location.getLocationId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.locationName").isEqualTo(location.getLocationName());
    }

    @Test
    public void 조회조건있는경우_페이지목록조회_성공() throws Exception {
        webTestClient.get()
                .uri(API_URL+"?keywordType=locationName&keyword=location&page=0&size=3")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.totalElements").isEqualTo(1)
                .jsonPath("$.content[0].locationName").isEqualTo(location.getLocationName());
    }

    @Test
    public void 조회조건없는경우_페이지목록조회_성공() throws Exception {
        webTestClient.get()
                .uri(API_URL+"?page=0&size=3")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.totalElements").isEqualTo(1)
                .jsonPath("$.content[0].locationName").isEqualTo(location.getLocationName());
    }

    @Test
    public void 한건저장_성공() throws Exception {
        webTestClient.post()
                .uri(API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(LocationSaveRequestDto.builder()
                        .locationName(location.getLocationName())
                        .isUse(location.getIsUse())
                        .sortSeq(location.getSortSeq())
                        .build()))
                .exchange()
                .expectStatus().isCreated()
                .expectBody().jsonPath("$.locationName").isEqualTo(location.getLocationName());

    }

    @Test
    public void 한건수정_성공() throws Exception {
        webTestClient.put()
                .uri(API_URL+"/{locationId}", location.getLocationId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(LocationUpdateRequestDto.builder()
                        .locationName("updateLocation")
                        .isUse(location.getIsUse())
                        .sortSeq(location.getSortSeq())
                        .build()))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void 한건삭제_참조데이터존재_삭제실패() throws Exception {
        BDDMockito.when(locationRepository.delete(ArgumentMatchers.any(Location.class)))
                .thenReturn(Mono.error(new DataIntegrityViolationException("integrity test")));
        webTestClient.delete()
                .uri(API_URL+"/{locationId}", 1L)
                .exchange()
                .expectStatus().isBadRequest();

    }


}