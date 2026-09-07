package org.egovframe.cloud.reservechecksevice.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.egovframe.cloud.reservechecksevice.api.dto.ReserveCancelRequestDto;
import org.egovframe.cloud.reservechecksevice.service.ReserveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Mono;

/**
 * 예약 취소 요청 본문의 취소 사유(@NotBlank)가 실제로 검증되는지 확인한다.
 * 스프링 컨텍스트를 띄우지 않는 standalone WebFlux 바인딩이라 통합테스트 환경에 의존하지 않는다.
 */
class ReserveCancelValidationTest {

    private ReserveService reserveService;
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        reserveService = mock(ReserveService.class);
        Environment env = mock(Environment.class);
        when(reserveService.cancel(anyString(), any(ReserveCancelRequestDto.class)))
            .thenReturn(Mono.empty());
        webTestClient = WebTestClient
            .bindToController(new ReserveApiController(reserveService, env))
            .build();
    }

    @Test
    @DisplayName("취소 사유가 공백 문자뿐이면 400 으로 거부하고 서비스를 호출하지 않는다")
    void blankReasonIsRejected() {
        webTestClient.put()
            .uri("/api/v1/reserves/cancel/1")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{\"reasonCancelContent\":\" \"}")
            .exchange()
            .expectStatus().isBadRequest();

        verify(reserveService, never()).cancel(anyString(), any(ReserveCancelRequestDto.class));
    }

    @Test
    @DisplayName("취소 사유가 없으면 400 으로 거부하고 서비스를 호출하지 않는다")
    void nullReasonIsRejected() {
        webTestClient.put()
            .uri("/api/v1/reserves/cancel/1")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{}")
            .exchange()
            .expectStatus().isBadRequest();

        verify(reserveService, never()).cancel(anyString(), any(ReserveCancelRequestDto.class));
    }

    @Test
    @DisplayName("취소 사유가 정상이면 204 로 처리된다")
    void validReasonIsAccepted() {
        webTestClient.put()
            .uri("/api/v1/reserves/cancel/1")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{\"reasonCancelContent\":\"개인 사정\"}")
            .exchange()
            .expectStatus().isNoContent();

        verify(reserveService).cancel(anyString(), any(ReserveCancelRequestDto.class));
    }
}
