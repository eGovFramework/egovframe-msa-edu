package org.egovframe.cloud.reserveitemservice.api.reserveItem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import org.egovframe.cloud.common.exception.dto.ErrorCode;
import org.egovframe.cloud.common.exception.dto.ErrorResponse;
import org.egovframe.cloud.reserveitemservice.api.reserveItem.dto.ReserveItemMainResponseDto;
import org.egovframe.cloud.reserveitemservice.api.reserveItem.dto.ReserveItemResponseDto;
import org.egovframe.cloud.reserveitemservice.api.reserveItem.dto.ReserveItemSaveRequestDto;
import org.egovframe.cloud.reserveitemservice.api.reserveItem.dto.ReserveItemUpdateRequestDto;
import org.egovframe.cloud.reserveitemservice.config.R2dbcConfig;
import org.egovframe.cloud.reserveitemservice.domain.code.Code;
import org.egovframe.cloud.reserveitemservice.domain.location.Location;
import org.egovframe.cloud.reserveitemservice.domain.location.LocationRepository;
import org.egovframe.cloud.reserveitemservice.domain.reserveItem.ReserveItem;
import org.egovframe.cloud.reserveitemservice.domain.reserveItem.ReserveItemRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles("test")
@Import({R2dbcConfig.class})
class ReserveItemApiControllerTest {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ReserveItemRepository reserveItemRepository;

    @Autowired
    private R2dbcEntityTemplate entityTemplate;

    @Autowired
    WebTestClient webTestClient;

    private final static String API_URL = "/api/v1/reserve-items";

    Code category = null;
    Location location = null;
    ReserveItem reserveItem = ReserveItem.builder().build();

    @AfterEach
    public void tearDown() {
        entityTemplate.delete(Code.class).all().block();
        reserveItemRepository.deleteAll().block();
        locationRepository.deleteAll().block();
    }

    @BeforeEach
    public void setUp() {
        category = Code.builder().codeName("category").codeId("category").parentCodeId("reserve-category").build();
        entityTemplate.insert(Code.class)
            .using(category).block();
        location = locationRepository.save(Location.builder()
            .locationName("location1")
            .isUse(true)
            .sortSeq(1)
            .build()).block();
        assertNotNull(location);
        reserveItem = ReserveItem.builder()
            .categoryId(category.getCodeId())
            .locationId(location.getLocationId())
            .reserveItemName("test")
            .isUse(Boolean.TRUE)
            .operationStartDate(LocalDateTime.of(2021, 10, 1, 1, 1))
            .operationEndDate(LocalDateTime.of(2021, 10, 31, 23, 59))
            .reserveMethodId("internet")
            .reserveMeansId("realtime")
            .requestStartDate(LocalDateTime.of(2021, 10, 1, 1, 1))
            .requestEndDate(LocalDateTime.of(2021, 10, 31, 23, 59))
            .totalQty(100)
            .inventoryQty(100)
            .isPeriod(Boolean.FALSE)
            .selectionMeansId("evaluate")
            .build();
    }


    @Test
    public void 사용자목록조회_성공() {

        ReserveItem saved = reserveItemRepository.save(reserveItem).block();
        assertNotNull(saved);

        webTestClient.method(HttpMethod.GET)
            .uri("/api/v1/"+category.getCodeId()+"/reserve-items"+"?page=0&size=3&isUse=true")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.totalElements").isEqualTo(1)
            .jsonPath("$.content[0].reserveItemName").isEqualTo(reserveItem.getReserveItemName());
    }

    @Test
    public void 관리자목록조회_성공() {

        ReserveItem saved = reserveItemRepository.save(reserveItem).block();
        assertNotNull(saved);

        webTestClient.method(HttpMethod.GET)
            .uri(API_URL+"?page=0&size=3&isUse=false")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.totalElements").isEqualTo(1)
            .jsonPath("$.content[0].reserveItemName").isEqualTo(reserveItem.getReserveItemName());
    }

    @Test
    public void 한건조회_성공() {
        ReserveItem saved = reserveItemRepository.save(reserveItem).block();
        assertNotNull(saved);

        ReserveItemResponseDto responseBody = webTestClient.get()
            .uri(API_URL+"/{reserveItemId}", saved.getReserveItemId())
            .exchange()
            .expectStatus().isOk()
            .expectBody(ReserveItemResponseDto.class)
            .returnResult().getResponseBody();

        assertThat(responseBody.getCategoryId()).isEqualTo(category.getCodeId());
        assertThat(responseBody.getReserveItemName()).isEqualTo(saved.getReserveItemName());

    }

    @Test
    public void 사용자_포털_메인_예약목록_조회_성공() {
        ReserveItem saved = reserveItemRepository.save(reserveItem).block();
        assertNotNull(saved);

        Map<String, Collection<ReserveItemMainResponseDto>> responseBody = webTestClient.get()
            .uri(API_URL+"/latest/3")
            .exchange()
            .expectStatus().isOk()
            .expectBody(new ParameterizedTypeReference<Map<String, Collection<ReserveItemMainResponseDto>>>() {
            })
            .returnResult().getResponseBody();

        assertThat(responseBody.keySet().size()).isEqualTo(1);
        assertThat(responseBody.keySet().contains(category.getCodeId())).isTrue();
        Collection<ReserveItemMainResponseDto> reserveItemMainResponseDtos = responseBody.get(category.getCodeId());
        reserveItemMainResponseDtos.stream().forEach(reserveItemMainResponseDto -> {
            assertThat(reserveItemMainResponseDto.getReserveItemName().equals(saved.getReserveItemName()));
        });
    }

    @Test
    public void 한건_등록_성공() {
        ReserveItemSaveRequestDto requestDto = ReserveItemSaveRequestDto.builder()
            .reserveItemName(reserveItem.getReserveItemName())
            .categoryId(reserveItem.getCategoryId())
            .locationId(reserveItem.getLocationId())
            .inventoryQty(reserveItem.getInventoryQty())
            .totalQty(reserveItem.getTotalQty())
            .operationStartDate(reserveItem.getOperationStartDate())
            .operationEndDate(reserveItem.getOperationEndDate())
            .reserveMethodId(reserveItem.getReserveMethodId())
            .reserveMeansId(reserveItem.getReserveMeansId())
            .isUse(reserveItem.getIsUse())
            .requestStartDate(reserveItem.getRequestStartDate())
            .requestEndDate(reserveItem.getRequestEndDate())
            .isPeriod(reserveItem.getIsPeriod())
            .selectionMeansId(reserveItem.getSelectionMeansId())
            .build();

        ReserveItemResponseDto responseBody = webTestClient.post()
            .uri(API_URL)
            .bodyValue(requestDto)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(ReserveItemResponseDto.class)
            .returnResult().getResponseBody();

        System.out.println(responseBody);
        assertThat(responseBody.getReserveItemName()).isEqualTo(requestDto.getReserveItemName());

    }

    @Test
    public void 한건_수정_성공() {
        ReserveItem saved = reserveItemRepository.save(reserveItem).block();
        assertNotNull(saved);

        ReserveItemUpdateRequestDto requestDto = ReserveItemUpdateRequestDto.builder()
            .reserveItemName("update")
            .categoryId(reserveItem.getCategoryId())
            .locationId(reserveItem.getLocationId())
            .inventoryQty(reserveItem.getInventoryQty())
            .totalQty(reserveItem.getTotalQty())
            .operationStartDate(reserveItem.getOperationStartDate())
            .operationEndDate(reserveItem.getOperationEndDate())
            .reserveMethodId(reserveItem.getReserveMethodId())
            .reserveMeansId(reserveItem.getReserveMeansId())
            .isUse(reserveItem.getIsUse())
            .requestStartDate(reserveItem.getRequestStartDate())
            .requestEndDate(reserveItem.getRequestEndDate())
            .isPeriod(reserveItem.getIsPeriod())
            .selectionMeansId(reserveItem.getSelectionMeansId())
            .build();

        webTestClient.put()
            .uri(API_URL+"/"+saved.getReserveItemId())
            .bodyValue(requestDto)
            .exchange()
            .expectStatus().isNoContent();

        ReserveItem findbyid = reserveItemRepository.findById(saved.getReserveItemId()).block();
        assertThat(findbyid.getReserveItemName()).isEqualTo("update");

    }

    @Test
    public void 사용여부_false_수정_성공() {
        ReserveItem saved = reserveItemRepository.save(reserveItem).block();
        assertNotNull(saved);

        webTestClient.put()
            .uri(API_URL+"/"+saved.getReserveItemId()+"/false")
            .exchange()
            .expectStatus().isNoContent();

        ReserveItem findbyid = reserveItemRepository.findById(saved.getReserveItemId()).block();
        assertThat(findbyid.getIsUse()).isEqualTo(Boolean.FALSE);
    }

    @Test
    public void 한건_저장_validation_실패() {
        ReserveItemSaveRequestDto requestDto = ReserveItemSaveRequestDto.builder()
            .reserveItemName(reserveItem.getReserveItemName())
            .categoryId(reserveItem.getCategoryId())
            .locationId(reserveItem.getLocationId())
            .inventoryQty(reserveItem.getInventoryQty())
            .totalQty(reserveItem.getTotalQty())
            .operationStartDate(reserveItem.getOperationStartDate())
            .operationEndDate(reserveItem.getOperationEndDate())
            .reserveMethodId(reserveItem.getReserveMethodId())
            .reserveMeansId(reserveItem.getReserveMeansId())
            .isUse(reserveItem.getIsUse())
            .isPeriod(reserveItem.getIsPeriod())
            .selectionMeansId(reserveItem.getSelectionMeansId())
            .build();

        System.out.println(requestDto);

        ErrorResponse responseBody = webTestClient.post()
            .uri(API_URL)
            .bodyValue(requestDto)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody(ErrorResponse.class)
            .returnResult().getResponseBody();

        assertThat(responseBody.getCode()).isEqualTo(ErrorCode.INVALID_INPUT_VALUE.getCode());
        assertThat(responseBody.getErrors().size()).isEqualTo(1);
        responseBody.getErrors().stream().forEach(fieldError -> {
            assertThat(fieldError.getField()).isEqualTo("requestStartDate");
            System.out.println(fieldError.getMessage());
        });
    }

}