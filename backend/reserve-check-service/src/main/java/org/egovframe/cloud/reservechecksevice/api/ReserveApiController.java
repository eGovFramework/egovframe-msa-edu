package org.egovframe.cloud.reservechecksevice.api;

import java.time.LocalDate;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.egovframe.cloud.reservechecksevice.api.dto.ReserveCancelRequestDto;
import org.egovframe.cloud.reservechecksevice.api.dto.ReserveListResponseDto;
import org.egovframe.cloud.reservechecksevice.api.dto.ReserveRequestDto;
import org.egovframe.cloud.reservechecksevice.api.dto.ReserveResponseDto;
import org.egovframe.cloud.reservechecksevice.api.dto.ReserveSaveRequestDto;
import org.egovframe.cloud.reservechecksevice.api.dto.ReserveUpdateRequestDto;
import org.egovframe.cloud.reservechecksevice.service.ReserveService;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * org.egovframe.cloud.reservechecksevice.api.ReserveApiController
 * <p>
 * 예약 확인 rest controller class
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/09/17
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/17    shinmj      최초 생성
 * </pre>
 */
@RequiredArgsConstructor
@RestController
public class ReserveApiController {

    private final ReserveService reserveService;

    private final Environment env;

    /**
     * 서비스 상태 확인
     *
     * @return
     */
    @GetMapping("/actuator/health-info")
    public String status() {
        return String.format("GET Reserve Check Service on" +
                "\n local.server.port :" + env.getProperty("local.server.port")
                + "\n egov.message :" + env.getProperty("egov.message")
        );
    }

    /**
     * 예약 확인(신청) 목록 조회
     * 관리자인 경우 모두 조회
     *
     * @param requestDto
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/api/v1/reserves")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Page<ReserveListResponseDto>> search(ReserveRequestDto requestDto,
                                                     @RequestParam(name = "page") int page,
                                                     @RequestParam(name = "size") int size) {
        return reserveService.search(requestDto, PageRequest.of(page, size));
    }

    /**
     * 사용자별 예약 목록 조회
     *
     * @param userId
     * @param requestDto
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/api/v1/{userId}/reserves")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Page<ReserveListResponseDto>> searchForUser(@PathVariable String userId,
                                                            ReserveRequestDto requestDto,
                                                            @RequestParam(name = "page") int page,
                                                            @RequestParam(name = "size") int size) {
        return reserveService.searchForUser(userId, requestDto, PageRequest.of(page, size));
    }

    /**
     * 예약 한건 조회
     *
     * @param reserveId
     * @return
     */
    @GetMapping("/api/v1/reserves/{reserveId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ReserveResponseDto> findById(@PathVariable String reserveId) {
        return reserveService.findReserveById(reserveId);
    }

    /**
     * 예약 취소
     *
     * @param reserveId
     * @return
     */
    @PutMapping("/api/v1/reserves/cancel/{reserveId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> cancel(@PathVariable String reserveId, @RequestBody ReserveCancelRequestDto cancelRequestDto) {
        return reserveService.cancel(reserveId, cancelRequestDto);
    }

    /**
     * 예약 승인
     *
     * @param reserveId
     * @return
     */
    @PutMapping("/api/v1/reserves/approve/{reserveId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> approve(@PathVariable String reserveId) {
        return reserveService.approve(reserveId);
    }

    /**
     * 예약 정보 수정
     *
     * @param reserveId
     * @param updateRequestDto
     * @return
     */
    @PutMapping("/api/v1/reserves/{reserveId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> update(@PathVariable String reserveId, @Valid @RequestBody ReserveUpdateRequestDto updateRequestDto) {
        return reserveService.update(reserveId, updateRequestDto).then();
    }

    /**
     * 관리자 예약 신청
     * 관리자의 경우 실시간이어도 이벤트 스트림 거치지 않고 바로 예약 처리
     *
     * @param saveRequestDto
     * @return
     */
    @PostMapping("/api/v1/reserves")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ReserveResponseDto> create(@Valid @RequestBody ReserveSaveRequestDto saveRequestDto) {
        return reserveService.create(saveRequestDto);
    }

    /**
     * 예약물품 별 조회기간 내 예약 목록 조회
     *
     * @param reserveItemId
     * @return
     */
    @GetMapping("/api/v1/reserves/{reserveItemId}/inventories")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Integer> countInventory(@PathVariable Long reserveItemId,
                                        @RequestParam(name = "startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                        @RequestParam(name = "endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return reserveService.countInventory(reserveItemId,startDate.atTime(0,0), endDate.atTime(23, 59));
    }

}
