package org.egovframe.cloud.reserveitemservice.api.reserveItem;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.egovframe.cloud.reserveitemservice.api.reserveItem.dto.ReserveItemRequestDto;
import org.egovframe.cloud.reserveitemservice.config.R2dbcConfig;
import org.egovframe.cloud.reserveitemservice.domain.code.Code;
import org.egovframe.cloud.reserveitemservice.domain.location.Location;
import org.egovframe.cloud.reserveitemservice.domain.reserveItem.ReserveItem;
import org.egovframe.cloud.reserveitemservice.domain.reserveItem.ReserveItemRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles(profiles = "test")
@Import({R2dbcConfig.class})
class ReserveItemApiControllerTest {


    @MockBean
    ReserveItemRepository reserveItemRepository;

    @Autowired
    WebTestClient webTestClient;

    private final static String API_URL = "/api/v1/reserve-items";


    @Test
    public void 사용자별_검색_목록_조회_성공() throws Exception {
        LocalDateTime startDate = LocalDateTime.of(2021, 1, 28, 1,1);
        LocalDateTime endDate = LocalDateTime.of(2021, 12, 6, 1,1);

        Location location = Location.builder()
                .locationId(1L)
                .locationName("location")
                .sortSeq(1)
                .isUse(true)
                .build();
        ReserveItem reserveItem = ReserveItem.builder()
                .reserveItemId(1L)
                .location(location)
                .locationId(location.getLocationId())
                .reserveItemName("test")
                .categoryId("education")
                .categoryName("교육")
                .totalQty(100)
                .inventoryQty(80)
                .operationEndDate(endDate)
                .operationStartDate(startDate)
                .isPeriod(false)
                .isUse(true)
                .build();

        BDDMockito.when(reserveItemRepository.searchForUser(ArgumentMatchers.anyString(),
                ArgumentMatchers.any(ReserveItemRequestDto.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(Flux.just(reserveItem));

        BDDMockito.when(reserveItemRepository.searchCountForUser(ArgumentMatchers.anyString(),
                ArgumentMatchers.any(ReserveItemRequestDto.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(Mono.just(1L));

        webTestClient.get()
                .uri("/api/v1/{categoryId}/reserve-items?keywordType=locationName&keyword=location&page=0&size=3", "education")
                .exchange()
                .expectStatus().isOk();

    }
    @Test
    public void main_예약물품조회_성공() throws Exception {
        BDDMockito.when(reserveItemRepository.findCodeDetail(ArgumentMatchers.anyString()))
            .thenReturn(Flux.fromIterable(Arrays.asList(Code.builder().codeId("education").codeName("교육").build(),
                Code.builder().codeId("equipment").codeName("장비").build(),
                Code.builder().codeId("space").codeName("장소").build())));

        LocalDateTime startDate = LocalDateTime.of(2021, 1, 28, 1,1);
        LocalDateTime endDate = LocalDateTime.of(2021, 12, 6, 1,1);

        Location location = Location.builder()
            .locationId(1L)
            .locationName("location")
            .sortSeq(1)
            .isUse(true)
            .build();
        ReserveItem reserveItem1 = ReserveItem.builder()
            .reserveItemId(1L)
            .location(location)
            .locationId(location.getLocationId())
            .reserveItemName("test")
            .categoryId("education")
            .categoryName("교육")
            .totalQty(100)
            .inventoryQty(80)
            .operationEndDate(endDate)
            .operationStartDate(startDate)
            .reserveMethodId("visit")
            .isPeriod(false)
            .isUse(true)
            .build();
        ReserveItem reserveItem2 = ReserveItem.builder()
            .reserveItemId(1L)
            .location(location)
            .locationId(location.getLocationId())
            .reserveItemName("test")
            .categoryId("education")
            .categoryName("장비")
            .totalQty(100)
            .inventoryQty(80)
            .operationEndDate(endDate)
            .operationStartDate(startDate)
            .reserveMethodId("visit")
            .isPeriod(false)
            .isUse(true)
            .build();

        ReserveItem reserveItem3 = ReserveItem.builder()
            .reserveItemId(1L)
            .location(location)
            .locationId(location.getLocationId())
            .reserveItemName("test")
            .categoryId("education")
            .categoryName("공간")
            .totalQty(100)
            .inventoryQty(80)
            .operationEndDate(endDate)
            .operationStartDate(startDate)
            .reserveMethodId("visit")
            .isPeriod(false)
            .isUse(true)
            .build();
        reserveItem1.setCreateDate(LocalDateTime.now());
        reserveItem2.setCreateDate(LocalDateTime.now());
        reserveItem3.setCreateDate(LocalDateTime.now());

        BDDMockito.when(reserveItemRepository.findLatestByCategory(ArgumentMatchers.anyInt(), ArgumentMatchers.anyString()))
            .thenReturn(Flux.fromIterable(Arrays.asList(reserveItem1, reserveItem2, reserveItem3)));


        webTestClient.get()
            .uri("/api/v1/reserve-items/latest/3")
            .exchange()
            .expectStatus().isOk();


    }

}