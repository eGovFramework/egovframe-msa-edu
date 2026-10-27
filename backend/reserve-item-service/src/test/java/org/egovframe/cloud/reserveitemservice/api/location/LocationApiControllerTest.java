package org.egovframe.cloud.reserveitemservice.api.location;

import static org.junit.jupiter.api.Assertions.*;

import org.egovframe.cloud.reserveitemservice.api.location.dto.LocationSaveRequestDto;
import org.egovframe.cloud.reserveitemservice.api.location.dto.LocationUpdateRequestDto;
import org.egovframe.cloud.reserveitemservice.config.R2dbcConfig;
import org.egovframe.cloud.reserveitemservice.domain.location.Location;
import org.egovframe.cloud.reserveitemservice.domain.location.LocationRepository;
import org.egovframe.cloud.reserveitemservice.domain.reserveItem.ReserveItem;
import org.egovframe.cloud.reserveitemservice.domain.reserveItem.ReserveItemRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.junit.jupiter.api.BeforeEach;
import org.egovframe.cloud.reserveitemservice.support.JwtTestTokens;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@ActiveProfiles("test")
@Import({R2dbcConfig.class})
public class LocationApiControllerTest {

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private ReserveItemRepository reserveItemRepository;

	@Autowired
	private WebTestClient rawClient;

	private WebTestClient webTestClient;

	@Value("${token.secret}")
	private String tokenSecret;

	@BeforeEach
	void authenticateOverHttp() {
		webTestClient = rawClient.mutate()
			.defaultHeader("Authorization", JwtTestTokens.bearer(tokenSecret)).build();
	}

	private static final String API_URL = "/api/v1/locations";

	@AfterEach
	public void tearDown() {
		reserveItemRepository.deleteAll().block();
		locationRepository.deleteAll().block();
	}

	@Test
	public void 한건조회_성공() throws Exception {

		Location location1 = locationRepository.save(Location.builder()
			.locationName("location1")
			.isUse(true)
			.sortSeq(1)
			.build()).block();
		assertNotNull(location1);

		webTestClient.get()
			.uri(API_URL+"/{locationId}", location1.getLocationId())
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.jsonPath("$.locationName").isEqualTo(location1.getLocationName());
	}


	@Test
	public void 조회조건있는경우_페이지목록조회_성공() throws Exception {

		Location location1 = locationRepository.save(Location.builder()
			.locationName("location1")
			.isUse(true)
			.sortSeq(1)
			.build()).block();
		assertNotNull(location1);

		webTestClient.get()
			.uri(API_URL+"?keywordType=locationName&keyword=location&page=0&size=3")
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.jsonPath("$.totalElements").isEqualTo(1)
			.jsonPath("$.content[0].locationName").isEqualTo(location1.getLocationName());
	}

	@Test
	public void 조회조건없는경우_페이지목록조회_성공() throws Exception {
		Location location1 = locationRepository.save(Location.builder()
			.locationName("location1")
			.isUse(true)
			.sortSeq(1)
			.build()).block();
		assertNotNull(location1);

		webTestClient.get()
			.uri(API_URL+"?page=0&size=3")
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.jsonPath("$.totalElements").isEqualTo(1)
			.jsonPath("$.content[0].locationName").isEqualTo(location1.getLocationName());
	}

	@Test
	public void 한건저장_성공() throws Exception {
		webTestClient.post()
			.uri(API_URL)
			.contentType(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromValue(LocationSaveRequestDto.builder()
				.locationName("location1")
				.isUse(true)
				.sortSeq(1)
				.build()))
			.exchange()
			.expectStatus().isCreated()
			.expectBody().jsonPath("$.locationName").isEqualTo("location1");

	}

	@Test
	public void 한건수정_성공() throws Exception {
		Location location1 = locationRepository.save(Location.builder()
			.locationName("location1")
			.isUse(true)
			.sortSeq(1)
			.build()).block();
		assertNotNull(location1);

		webTestClient.put()
			.uri(API_URL+"/{locationId}", location1.getLocationId())
			.contentType(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromValue(LocationUpdateRequestDto.builder()
				.locationName("updateLocation")
				.isUse(location1.getIsUse())
				.sortSeq(location1.getSortSeq())
				.build()))
			.exchange()
			.expectStatus().isNoContent();

		Location updatedLocation = locationRepository.findById(location1.getLocationId()).block();
		assertNotNull(updatedLocation);
		assertEquals(updatedLocation.getLocationName(), "updateLocation");
	}

	@Test
	public void 한건삭제_참조데이터존재_삭제실패() throws Exception {
		Location location1 = locationRepository.save(Location.builder()
			.locationName("location1")
			.isUse(true)
			.sortSeq(1)
			.build()).block();
		assertNotNull(location1);

		reserveItemRepository.save(ReserveItem.builder()
			.locationId(location1.getLocationId())
			.categoryId("test")
			.build()).block();

		webTestClient.delete()
			.uri(API_URL+"/{locationId}", location1.getLocationId())
			.exchange()
			.expectStatus().isBadRequest();

	}

	@Test
	public void 한건삭제_성공() throws Exception {
		Location location1 = locationRepository.save(Location.builder()
			.locationName("location1")
			.isUse(true)
			.sortSeq(1)
			.build()).block();
		assertNotNull(location1);

		webTestClient.delete()
			.uri(API_URL+"/{locationId}", location1.getLocationId())
			.exchange()
			.expectStatus().isNoContent();

	}

	@Test
	void unauthenticatedRequestsAreRejected() {
		rawClient.get().uri(API_URL + "?page=0&size=3").exchange().expectStatus().isUnauthorized();
		rawClient.post().uri(API_URL).bodyValue(LocationSaveRequestDto.builder()
			.locationName("unauthenticated").isUse(true).sortSeq(1).build())
			.exchange().expectStatus().isUnauthorized();
		assertEquals(0L, locationRepository.count().block());
	}

	@Test
	void forgedSignatureIsRejected() {
		rawClient.get().uri(API_URL + "?page=0&size=3")
			.header("Authorization", JwtTestTokens.bearer("wrong-signing-secret"))
			.exchange().expectStatus().isUnauthorized();
	}


}
