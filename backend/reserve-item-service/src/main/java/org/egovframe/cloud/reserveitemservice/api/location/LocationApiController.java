package org.egovframe.cloud.reserveitemservice.api.location;

import io.swagger.v3.oas.annotations.Operation;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.reserveitemservice.api.location.dto.LocationResponseDto;
import org.egovframe.cloud.reserveitemservice.api.location.dto.LocationSaveRequestDto;
import org.egovframe.cloud.reserveitemservice.api.location.dto.LocationUpdateRequestDto;
import org.egovframe.cloud.reserveitemservice.service.location.LocationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * org.egovframe.cloud.reserveitemservice.api.location.LocationApiController
 * <p>
 * 예약 지역 api contoller class
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/09/06
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/06    shinmj      최초 생성
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class LocationApiController {

    private final LocationService locationService;

    /**
     * 목록 조회
     *
     * @param requestDto
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/api/v1/locations")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "page of location")
    public Mono<Page<LocationResponseDto>> search(RequestDto requestDto,
                                                  @RequestParam(name = "page") int page,
                                                  @RequestParam(name = "size") int size) {
        return locationService.search(requestDto, PageRequest.of(page, size));
    }

    /**
     * 한건 조회
     *
     * @param locationId
     * @return
     */
    @GetMapping("/api/v1/locations/{locationId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<LocationResponseDto> findById(@PathVariable Long locationId) {
        return locationService.findById(locationId);
    }

    /**
     * 지역 목록 조회 (사용여부 = true)
     * 예약 목록 등록 시
     *
     * @return
     */
    @GetMapping("/api/v1/locations/combo")
    @ResponseStatus(HttpStatus.OK)
    public Flux<LocationResponseDto> findAll() {
        return locationService.findAll();
    }

    /**
     * 지역 한건 저장
     *
     * @param saveRequestDto
     * @return
     */
    @PostMapping("/api/v1/locations")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<LocationResponseDto> save(@Valid @RequestBody LocationSaveRequestDto saveRequestDto) {
        return locationService.save(saveRequestDto);
    }

    /**
     * 지역 한건 수정
     *
     * @param locationId
     * @param updateRequestDto
     * @return
     */
    @PutMapping("/api/v1/locations/{locationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> update(@PathVariable Long locationId, @Valid @RequestBody LocationUpdateRequestDto updateRequestDto) {
        return locationService.update(locationId, updateRequestDto);
    }

    /**
     * 지역 한건 삭제
     *
     * @param locationId
     * @return
     */
    @DeleteMapping("/api/v1/locations/{locationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long locationId) {
        return locationService.delete(locationId);
    }

    /**
     * 지역 사용여부 toggle
     *
     * @param locationId
     * @param isUse
     * @return
     */
    @PutMapping("/api/v1/locations/{locationId}/{isUse}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> updateIsUse(@PathVariable Long locationId, @PathVariable Boolean isUse) {
        return locationService.updateIsUse(locationId, isUse);
    }
}
