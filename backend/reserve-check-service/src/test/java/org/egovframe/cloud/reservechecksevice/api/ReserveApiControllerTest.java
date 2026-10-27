package org.egovframe.cloud.reservechecksevice.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import org.egovframe.cloud.common.domain.Role;
import org.egovframe.cloud.common.exception.dto.ErrorCode;
import org.egovframe.cloud.common.exception.dto.ErrorResponse;
import org.egovframe.cloud.reservechecksevice.api.dto.ReserveCancelRequestDto;
import org.egovframe.cloud.reservechecksevice.api.dto.ReserveListResponseDto;
import org.egovframe.cloud.reservechecksevice.api.dto.ReserveSaveRequestDto;
import org.egovframe.cloud.reservechecksevice.api.dto.ReserveUpdateRequestDto;
import org.egovframe.cloud.reservechecksevice.client.ReserveItemServiceClient;
import org.egovframe.cloud.reservechecksevice.client.UserServiceClient;
import org.egovframe.cloud.reservechecksevice.client.dto.ReserveItemRelationResponseDto;
import org.egovframe.cloud.reservechecksevice.client.dto.ReserveItemResponseDto;
import org.egovframe.cloud.reservechecksevice.client.dto.UserResponseDto;
import org.egovframe.cloud.reservechecksevice.domain.Reserve;
import org.egovframe.cloud.reservechecksevice.domain.ReserveItem;
import org.egovframe.cloud.reservechecksevice.domain.ReserveRepository;
import org.egovframe.cloud.reservechecksevice.domain.ReserveStatus;
import org.egovframe.cloud.reservechecksevice.domain.location.Location;
import org.egovframe.cloud.reservechecksevice.util.RestResponsePage;
import org.egovframe.cloud.reservechecksevice.util.JwtTestTokens;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "test")
public class ReserveApiControllerTest {

	@Autowired
	private ReserveRepository reserveRepository;

	@MockBean
	private ReserveItemServiceClient reserveItemServiceClient;

	@MockBean
	private UserServiceClient userServiceClient;

	// 첨부파일 이벤트의 외부 RabbitMQ 경계만 대체한다.
	@MockBean
	private org.springframework.cloud.stream.function.StreamBridge streamBridge;

	@Autowired
	private WebTestClient webTestClient;

	@Value("${token.secret}")
	private String tokenSecret;

	/** 운영 보안 필터 체인이 검증하는 것과 같은 방식으로 서명한 JWT 를 실어 보내는 클라이언트 */
	private WebTestClient as(String userId, Role role) {
		return webTestClient.mutate()
			.defaultHeader("Authorization", JwtTestTokens.bearer(tokenSecret, userId, role))
			.build();
	}

	private static final String API_URL = "/api/v1/reserves";

	private UserResponseDto user;
	private Location location;
	private ReserveItem reserveItem;
	private Reserve reserve;

	@BeforeEach
	public void setup() {

		user = UserResponseDto.builder()
			.roleId(Role.ADMIN.getKey())
			.userId("user")
			.build();
		location = Location.builder()
			.locationId(1L)
			.locationName("location")
			.build();
		reserveItem = ReserveItem.builder()
			.reserveItemId(1L)
			.reserveItemName("test")
			.locationId(location.getLocationId())
			.location(location)
			.categoryId("place")
			.inventoryQty(100)
			.totalQty(100)
			.reserveMethodId("internet")
			.reserveMeansId("realtime")
			.requestStartDate(LocalDateTime.of(2021, 1, 1, 1, 1))
			.requestEndDate(LocalDateTime.of(2021, 12, 31, 23, 59))
			.operationStartDate(LocalDateTime.of(2021, 1, 1, 1, 1))
			.operationEndDate(LocalDateTime.of(2021, 12, 31, 23, 59))
			.build();

		reserve = Reserve.builder()
			.reserveId("1")
			.reserveItemId(reserveItem.getReserveItemId())
			.reserveQty(50)
			.reservePurposeContent("test")
			.reserveStatusId("request")
			.reserveStartDate(LocalDateTime.of(2021, 9, 9, 1, 1))
			.reserveEndDate(LocalDateTime.of(2021, 9, 20, 1, 1))
			.userId(user.getUserId())
			.userEmail("user@email.com")
			.userContactNo("contact")
			.build();
		reserve.setReserveItem(reserveItem);
		reserve.setUser(user);
	}

	@AfterEach
	public void tearDown() {
		reserveRepository.deleteAll().block();
	}

	@Test
	public void 예약신청관리_목록_조회_성공() {
		//given
		BDDMockito.when(userServiceClient.findByUserId(ArgumentMatchers.anyString()))
			.thenReturn(Mono.just(user));
		BDDMockito.when(reserveItemServiceClient.findByIdWithRelations(ArgumentMatchers.anyLong()))
			.thenReturn(Mono.just(ReserveItemRelationResponseDto.builder().entity(reserveItem).build()));

		Reserve saved = reserveRepository.insert(reserve).block();
		assertNotNull(saved);

		//when
		as("admin", Role.ADMIN).get()
			.uri(API_URL + "?page=0&size=5")
			.exchange()
			.expectStatus().isOk()
			.expectBody(new ParameterizedTypeReference<RestResponsePage<ReserveListResponseDto>>() {
			})
			.value(page -> {
				//then
				assertThat(page.getTotalElements()).isEqualTo(1L);
				assertThat(page.getContent().get(0).getReserveId()).isEqualTo(reserve.getReserveId());
				page.getContent().stream().forEach(System.out::println);
			});

	}

	@Test
	public void 관리자_취소_성공() {
		BDDMockito.when(reserveItemServiceClient.updateInventory(ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt()))
			.thenReturn(Mono.just(true));

		Reserve saved = reserveRepository.insert(reserve).block();
		assertNotNull(saved);

		as("admin", Role.ADMIN).put()
			.uri(API_URL + "/cancel/{reserveId}", saved.getReserveId())
			.bodyValue(ReserveCancelRequestDto.builder().reasonCancelContent("reason for cancellation").build())
			.exchange()
			.expectStatus().isNoContent();

		Reserve updated = reserveRepository.findById(saved.getReserveId()).block();
		assertThat(updated.getReserveStatusId()).isEqualTo("cancel");
		assertThat(updated.getReasonCancelContent()).isEqualTo("reason for cancellation");

	}

	@Test
	public void 사용자_취소_성공() {
		//given
		BDDMockito.when(reserveItemServiceClient.updateInventory(ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt()))
			.thenReturn(Mono.just(true));
		Reserve saved = reserveRepository.insert(reserve).block();
		assertNotNull(saved);

		//when
		as("user", Role.USER).put()
			.uri(API_URL + "/cancel/{reserveId}", saved.getReserveId())
			.bodyValue(ReserveCancelRequestDto.builder().reasonCancelContent("reason for cancellation").build())
			.exchange()
			.expectStatus().isNoContent()
		;

		Reserve updated = reserveRepository.findById(saved.getReserveId()).block();
		assertThat(updated.getReserveStatusId()).isEqualTo("cancel");
		assertThat(updated.getReasonCancelContent()).isEqualTo("reason for cancellation");
	}

	@Test
	public void 다른사용자_예약_취소_실패() {
		BDDMockito.when(reserveItemServiceClient.updateInventory(ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt()))
			.thenReturn(Mono.just(true));
		Reserve saved = reserveRepository.insert(reserve).block();
		assertNotNull(saved);

		as("test", Role.USER).put()
			.uri(API_URL + "/cancel/{reserveId}", saved.getReserveId())
			.bodyValue(ReserveCancelRequestDto.builder().reasonCancelContent("reason for cancellation").build())
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ErrorResponse.class)
			.value(response -> {
				assertThat(response.getMessage()).isEqualTo("해당 예약은 취소할 수 없습니다.");
				assertThat(response.getCode()).isEqualTo(ErrorCode.BUSINESS_CUSTOM_MESSAGE.getCode());
			});
	}

	@Test
	public void 예약상태_완료_취소_실패() {
		Reserve done = reserve.updateStatus(ReserveStatus.DONE.getKey());
		Reserve saved = reserveRepository.insert(done).block();
		assertNotNull(saved);

		BDDMockito.when(reserveItemServiceClient.updateInventory(ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt()))
			.thenReturn(Mono.just(true));

		as("user", Role.USER).put()
			.uri(API_URL + "/cancel/{reserveId}", saved.getReserveId())
			.bodyValue(ReserveCancelRequestDto.builder().reasonCancelContent("reason for cancellation").build())
			.exchange()
			.expectBody(ErrorResponse.class)
			.value(response -> {
				assertThat(response.getMessage()).isEqualTo("해당 예약은 이미 실행되어 취소할 수 없습니다.");
				assertThat(response.getCode()).isEqualTo(ErrorCode.BUSINESS_CUSTOM_MESSAGE.getCode());
			});
		;
	}

	@Test
	public void 관리자가_아닌_경우_승인_실패() {
		Reserve saved = reserveRepository.insert(reserve).block();
		assertNotNull(saved);

		BDDMockito.when(reserveItemServiceClient.findById(ArgumentMatchers.anyLong()))
			.thenReturn(Mono.just(ReserveItemResponseDto.builder().reserveItem(reserveItem).build()));
		BDDMockito.when(reserveItemServiceClient.updateInventory(ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt()))
			.thenReturn(Mono.just(true));

		as("user", Role.USER).put()
			.uri(API_URL + "/approve/{reserveId}", saved.getReserveId())
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ErrorResponse.class)
			.value(response -> {
				assertThat(response.getMessage()).isEqualTo("관리자만 승인할 수 있습니다.");
			});
	}

	@Test
	public void 예약승인_성공() {

		Reserve saved = reserveRepository.insert(reserve).block();
		assertNotNull(saved);

		BDDMockito.when(reserveItemServiceClient.findById(ArgumentMatchers.anyLong()))
			.thenReturn(Mono.just(ReserveItemResponseDto.builder().reserveItem(reserveItem).build()));
		BDDMockito.when(reserveItemServiceClient.updateInventory(ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt()))
			.thenReturn(Mono.just(true));

		as("admin", Role.ADMIN).put()
			.uri(API_URL + "/approve/{reserveId}", saved.getReserveId())
			.exchange()
			.expectStatus().isNoContent();

		Reserve updated = reserveRepository.findById(saved.getReserveId()).block();
		assertThat(updated.getReserveStatusId()).isEqualTo("approve");

	}

	@Test
	public void 예약승인_실패_재고부족() {
		ReserveItem failReserveItem = ReserveItem.builder()
			.reserveItemId(1L)
			.reserveItemName("test")
			.locationId(location.getLocationId())
			.location(location)
			.categoryId("equipment")
			.totalQty(20)
			.inventoryQty(10)
			.reserveMethodId("internet")
			.reserveMeansId("realtime")
			.isPeriod(false)
			.requestStartDate(LocalDateTime.of(2021, 1, 1, 1, 1))
			.requestEndDate(LocalDateTime.of(2021, 12, 31, 23, 59))
			.operationStartDate(LocalDateTime.of(2021, 1, 1, 1, 1))
			.operationEndDate(LocalDateTime.of(2021, 12, 31, 23, 59))
			.build();

		Reserve saved = reserveRepository.insert(reserve).block();
		assertNotNull(saved);

		BDDMockito.when(reserveItemServiceClient.findById(ArgumentMatchers.anyLong()))
			.thenReturn(Mono.just(ReserveItemResponseDto.builder().reserveItem(failReserveItem).build()));
		BDDMockito.when(reserveItemServiceClient.updateInventory(ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt()))
			.thenReturn(Mono.just(false));

		as("admin", Role.ADMIN).put()
			.uri(API_URL + "/approve/{reserveId}", saved.getReserveId())
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ErrorResponse.class)
			.value(response -> {
				assertThat(response.getMessage()).isEqualTo("해당 날짜에 예약할 수 있는 재고수량이 없습니다.");
			});

	}

	@Test
	public void 관리자_예약정보_수정_성공() {
		Reserve saved = reserveRepository.insert(reserve).block();
		assertNotNull(saved);

		BDDMockito.when(reserveItemServiceClient.findById(ArgumentMatchers.anyLong()))
			.thenReturn(Mono.just(ReserveItemResponseDto.builder().reserveItem(reserveItem).build()));
		BDDMockito.when(reserveItemServiceClient.updateInventory(ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt()))
			.thenReturn(Mono.just(true));

		ReserveUpdateRequestDto updateRequestDto =
			ReserveUpdateRequestDto.builder()
				.reserveItemId(saved.getReserveItemId())
				.categoryId(saved.getReserveItem().getCategoryId())
				.reservePurposeContent("purpose")
				.reserveQty(10)
				.reserveStartDate(saved.getReserveStartDate())
				.reserveEndDate(saved.getReserveEndDate())
				.attachmentCode(saved.getAttachmentCode())
				.userId(saved.getUserId())
				.userContactNo("contact update")
				.userEmail(saved.getUserEmail())
				.build();

		as("admin", Role.ADMIN).put()
			.uri(API_URL + "/{reserveId}", saved.getReserveId())
			.bodyValue(updateRequestDto)
			.exchange()
			.expectStatus().isNoContent()
		;

		Reserve updated = reserveRepository.findById(saved.getReserveId()).block();
		assertThat(updated.getReservePurposeContent()).isEqualTo("purpose");
		assertThat(updated.getReserveQty()).isEqualTo(10);
		assertThat(updated.getUserContactNo()).isEqualTo("contact update");
	}

	@Test
	public void 다른사용자_예약정보_수정_실패() {
		Reserve saved = reserveRepository.insert(reserve).block();
		assertNotNull(saved);

		BDDMockito.when(reserveItemServiceClient.findById(ArgumentMatchers.anyLong()))
			.thenReturn(Mono.just(ReserveItemResponseDto.builder().reserveItem(reserveItem).build()));
		BDDMockito.when(reserveItemServiceClient.updateInventory(ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt()))
			.thenReturn(Mono.just(true));

		ReserveUpdateRequestDto updateRequestDto =
			ReserveUpdateRequestDto.builder()
				.reserveItemId(saved.getReserveItemId())
				.categoryId(saved.getReserveItem().getCategoryId())
				.reservePurposeContent("purpose")
				.reserveQty(10)
				.reserveStartDate(saved.getReserveStartDate())
				.reserveEndDate(saved.getReserveEndDate())
				.attachmentCode(saved.getAttachmentCode())
				.userId(saved.getUserId())
				.userContactNo("contact update")
				.userEmail(saved.getUserEmail())
				.build();

		as("test", Role.USER).put()
			.uri(API_URL + "/{reserveId}", saved.getReserveId())
			.bodyValue(updateRequestDto)
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ErrorResponse.class)
			.value(response -> {
				assertThat(response.getMessage()).isEqualTo("해당 예약은 수정할 수 없습니다.");
			});
	}

	@Test
	public void 사용자_예약정보_수정_성공() {
		Reserve saved = reserveRepository.insert(reserve).block();
		assertNotNull(saved);

		BDDMockito.when(reserveItemServiceClient.findById(ArgumentMatchers.anyLong()))
			.thenReturn(Mono.just(ReserveItemResponseDto.builder().reserveItem(reserveItem).build()));
		BDDMockito.when(reserveItemServiceClient.updateInventory(ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt()))
			.thenReturn(Mono.just(true));

		ReserveUpdateRequestDto updateRequestDto =
			ReserveUpdateRequestDto.builder()
				.reserveItemId(saved.getReserveItemId())
				.categoryId(saved.getReserveItem().getCategoryId())
				.reservePurposeContent("purpose")
				.reserveQty(10)
				.reserveStartDate(saved.getReserveStartDate())
				.reserveEndDate(saved.getReserveEndDate())
				.attachmentCode(saved.getAttachmentCode())
				.userId(saved.getUserId())
				.userContactNo("contact update")
				.userEmail(saved.getUserEmail())
				.build();

		as("user", Role.USER).put()
			.uri(API_URL + "/{reserveId}", saved.getReserveId())
			.bodyValue(updateRequestDto)
			.exchange()
			.expectStatus().isNoContent()
		;

		Reserve updated = reserveRepository.findById(saved.getReserveId()).block();
		assertThat(updated.getReservePurposeContent()).isEqualTo("purpose");
		assertThat(updated.getReserveQty()).isEqualTo(10);
		assertThat(updated.getUserContactNo()).isEqualTo("contact update");

	}

	@Test
	public void 사용자_상태승인인예약정보_수정_실패() {
		Reserve failedReserve = reserve.withReserveStatusId(ReserveStatus.APPROVE.getKey());
		Reserve saved = reserveRepository.insert(failedReserve).block();
		assertNotNull(saved);

		BDDMockito.when(reserveItemServiceClient.findById(ArgumentMatchers.anyLong()))
			.thenReturn(Mono.just(ReserveItemResponseDto.builder().reserveItem(reserveItem).build()));
		BDDMockito.when(reserveItemServiceClient.updateInventory(ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt()))
			.thenReturn(Mono.just(false));

		ReserveUpdateRequestDto updateRequestDto =
			ReserveUpdateRequestDto.builder()
				.reserveItemId(saved.getReserveItemId())
				.categoryId(saved.getReserveItem().getCategoryId())
				.reservePurposeContent("purpose")
				.reserveQty(10)
				.reserveStartDate(saved.getReserveStartDate())
				.reserveEndDate(saved.getReserveEndDate())
				.attachmentCode(saved.getAttachmentCode())
				.userId(saved.getUserId())
				.userContactNo("contact update")
				.userEmail(saved.getUserEmail())
				.build();

		as("user", Role.USER).put()
			.uri(API_URL + "/{reserveId}", saved.getReserveId())
			.bodyValue(updateRequestDto)
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ErrorResponse.class)
			.value(response -> {
				assertThat(response.getMessage()).isEqualTo("예약 신청 상태인 경우에만 수정 가능합니다.");
			});

	}

	@Test
	public void 관리자_예약_성공() {
		BDDMockito.when(userServiceClient.findByUserId(ArgumentMatchers.anyString()))
			.thenReturn(Mono.just(user));
		BDDMockito.when(reserveItemServiceClient.findByIdWithRelations(ArgumentMatchers.anyLong()))
			.thenReturn(Mono.just(ReserveItemRelationResponseDto.builder().entity(reserveItem).build()));
		BDDMockito.when(reserveItemServiceClient.findById(ArgumentMatchers.anyLong()))
			.thenReturn(Mono.just(ReserveItemResponseDto.builder().reserveItem(reserveItem).build()));
		BDDMockito.when(reserveItemServiceClient.updateInventory(ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt()))
			.thenReturn(Mono.just(true));

		ReserveSaveRequestDto saveRequestDto =
			ReserveSaveRequestDto.builder()
				.reserveItemId(reserve.getReserveItemId())
				.categoryId(reserve.getReserveItem().getCategoryId())
				.reservePurposeContent(reserve.getReservePurposeContent())
				.reserveQty(reserve.getReserveQty())
				.reserveStartDate(reserve.getReserveStartDate())
				.reserveEndDate(reserve.getReserveEndDate())
				.attachmentCode(reserve.getAttachmentCode())
				.userId(reserve.getUserId())
				.userContactNo(reserve.getUserContactNo())
				.userEmail(reserve.getUserEmail())
				.build();

		as("admin", Role.ADMIN).post()
			.uri(API_URL)
			.bodyValue(saveRequestDto)
			.exchange()
			.expectStatus().isCreated()
			.expectBody()
			.jsonPath("$.reserveId").isNotEmpty()
			.jsonPath("$.reserveQty").isEqualTo(saveRequestDto.getReserveQty())
			.jsonPath("$.userId").isEqualTo(saveRequestDto.getUserId());

		assertThat(reserveRepository.count().block()).isEqualTo(1L);
		Reserve saved = reserveRepository.findAll().single().block();
		assertThat(saved.getReservePurposeContent()).isEqualTo(saveRequestDto.getReservePurposeContent());
		BDDMockito.verify(streamBridge).send(ArgumentMatchers.anyString(), ArgumentMatchers.any());

	}

	@Test
	public void 예약신청_valid_실패() {
		ReserveItem validReserveItem = ReserveItem.builder()
			.reserveItemId(1L)
			.reserveItemName("test")
			.locationId(location.getLocationId())
			.location(location)
			.categoryId("equipment")
			.totalQty(100)
			.inventoryQty(10)
			.operationStartDate(LocalDateTime.of(2021, 10, 1, 1, 1))
			.operationEndDate(LocalDateTime.of(2021, 10, 31, 23, 59))
			.build();
		reserve.setReserveItem(validReserveItem);

		ReserveSaveRequestDto saveRequestDto =
			ReserveSaveRequestDto.builder()
				.reserveItemId(reserve.getReserveItemId())
				.categoryId(reserve.getReserveItem().getCategoryId())
				.reservePurposeContent(reserve.getReservePurposeContent())
				.reserveQty(null)
				.reserveStartDate(LocalDateTime.of(2021, 11, 1, 1, 1))
				.reserveEndDate(reserve.getReserveEndDate())
				.attachmentCode(reserve.getAttachmentCode())
				.userId(reserve.getUserId())
				.userContactNo(reserve.getUserContactNo())
				.userEmail(reserve.getUserEmail())
				.build();

		as("admin", Role.ADMIN).post()
			.uri(API_URL)
			.bodyValue(saveRequestDto)
			.exchange()
			.expectStatus().isBadRequest()
		;
	}

	@Test
	public void 물품재고조회_성공() {

		BDDMockito.when(reserveItemServiceClient.findById(ArgumentMatchers.anyLong()))
			.thenReturn(Mono.just(ReserveItemResponseDto.builder().reserveItem(reserveItem).build()));

		Reserve inventoryreserve = Reserve.builder()
			.reserveId("1")
			.reserveItemId(reserveItem.getReserveItemId())
			.reserveQty(50)
			.reservePurposeContent("test")
			.reserveStatusId("request")
			.reserveStartDate(LocalDateTime.of(2021, 9, 9, 0, 0))
			.reserveEndDate(LocalDateTime.of(2021, 9, 9, 0, 0))
			.userId(user.getUserId())
			.userEmail("user@email.com")
			.userContactNo("contact")
			.build();
		inventoryreserve.setReserveItem(reserveItem);
		inventoryreserve.setUser(user);
		Reserve saved = reserveRepository.insert(inventoryreserve).block();
		assertNotNull(saved);

		Integer responseBody = as("user", Role.USER).get()
			.uri("/api/v1/reserves/" + reserveItem.getReserveItemId()
				+ "/inventories?startDate=2021-09-09&endDate=2021-09-09")
			.exchange()
			.expectStatus().isOk()
			.expectBody(Integer.class)
			.returnResult().getResponseBody();

		assertThat(responseBody).isEqualTo(50);

	}

	@Test
	public void 토큰없이_조회하면_401() {
		webTestClient.get()
			.uri(API_URL + "?page=0&size=5")
			.exchange()
			.expectStatus().isUnauthorized();
	}

	@Test
	public void 토큰없이_예약_변경_요청은_모두_401이고_데이터는_바뀌지_않는다() {
		Reserve saved = reserveRepository.insert(reserve).block();
		assertNotNull(saved);

		webTestClient.put()
			.uri(API_URL + "/approve/{reserveId}", saved.getReserveId())
			.exchange()
			.expectStatus().isUnauthorized();

		webTestClient.put()
			.uri(API_URL + "/cancel/{reserveId}", saved.getReserveId())
			.bodyValue(ReserveCancelRequestDto.builder().reasonCancelContent("reason for cancellation").build())
			.exchange()
			.expectStatus().isUnauthorized();

		webTestClient.post()
			.uri(API_URL)
			.bodyValue(ReserveSaveRequestDto.builder().reserveItemId(reserveItem.getReserveItemId()).build())
			.exchange()
			.expectStatus().isUnauthorized();

		Reserve unchanged = reserveRepository.findById(saved.getReserveId()).block();
		assertThat(unchanged.getReserveStatusId()).isEqualTo("request");
		assertThat(reserveRepository.count().block()).isEqualTo(1L);
	}

	@Test
	public void 서명이_다른_토큰은_거부된다() {
		String forged = JwtTestTokens.token("another-secret", "admin", Role.ADMIN.getKey());

		webTestClient.get()
			.uri(API_URL + "?page=0&size=5")
			.header("Authorization", "Bearer " + forged)
			.exchange()
			.expectStatus().value(status -> assertThat(status).isIn(401, 403));
	}

}
