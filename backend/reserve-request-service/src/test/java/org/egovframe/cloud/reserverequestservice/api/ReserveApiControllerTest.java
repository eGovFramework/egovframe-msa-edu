package org.egovframe.cloud.reserverequestservice.api;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.egovframe.cloud.common.domain.Role;
import org.egovframe.cloud.reserverequestservice.api.dto.ReserveResponseDto;
import org.egovframe.cloud.reserverequestservice.api.dto.ReserveSaveRequestDto;
import org.egovframe.cloud.reserverequestservice.config.WithCustomMockUser;
import org.egovframe.cloud.reserverequestservice.domain.Reserve;
import org.egovframe.cloud.reserverequestservice.domain.ReserveRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles(profiles = "test")
class ReserveApiControllerTest {

	@Autowired
	private ReserveRepository reserveRepository;

	@Autowired
	private WebTestClient webTestClient;

	private Reserve reserve;

	@BeforeEach
	public void setup() {

		reserve = Reserve.builder()
			.reserveId("1")
			.reserveQty(50)
			.reservePurposeContent("test")
			.reserveStatusId("request")
			.reserveStartDate(LocalDateTime.of(2021, 9, 9, 1, 1))
			.reserveEndDate(LocalDateTime.of(2021, 9, 20, 1, 1))
			.build();
	}

	@AfterEach
	public void tearDown() {
		reserveRepository.deleteAll().block();
	}

	@Test
	@WithCustomMockUser(userId = "user", role = Role.USER)
	public void 사용자_예약_성공() {

		ReserveSaveRequestDto saveRequestDto =
			ReserveSaveRequestDto.builder()
				.reserveItemId(reserve.getReserveItemId())
				.reservePurposeContent(reserve.getReservePurposeContent())
				.reserveQty(reserve.getReserveQty())
				.reserveStartDate(reserve.getReserveStartDate())
				.reserveEndDate(reserve.getReserveEndDate())
				.attachmentCode(reserve.getAttachmentCode())
				.userId(reserve.getUserId())
				.userContactNo(reserve.getUserContactNo())
				.userEmail(reserve.getUserEmail())
				.build();

		ReserveResponseDto responseBody = webTestClient.post()
			.uri("/api/v1/requests/evaluates")
			.bodyValue(saveRequestDto)
			.exchange()
			.expectStatus().isCreated()
			.expectBody(ReserveResponseDto.class)
			.returnResult().getResponseBody();

		assertThat(responseBody.getReserveQty()).isEqualTo(reserve.getReserveQty());
		assertThat(responseBody.getReservePurposeContent()).isEqualTo(reserve.getReservePurposeContent());

	}

}