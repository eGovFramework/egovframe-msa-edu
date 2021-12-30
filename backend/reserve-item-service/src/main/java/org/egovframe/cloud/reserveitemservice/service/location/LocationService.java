package org.egovframe.cloud.reserveitemservice.service.location;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.common.exception.BusinessMessageException;
import org.egovframe.cloud.reactive.service.ReactiveAbstractService;
import org.egovframe.cloud.reserveitemservice.api.location.dto.LocationResponseDto;
import org.egovframe.cloud.reserveitemservice.api.location.dto.LocationSaveRequestDto;
import org.egovframe.cloud.reserveitemservice.api.location.dto.LocationUpdateRequestDto;
import org.egovframe.cloud.reserveitemservice.domain.location.Location;
import org.egovframe.cloud.reserveitemservice.domain.location.LocationRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * org.egovframe.cloud.reserveitemservice.service.location.LocationService
 *
 * 예약 지역 service 클래스
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
 *  2021/09/06    shinmj       최초 생성
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class LocationService extends ReactiveAbstractService {

    private final LocationRepository locationRepository;

    /**
     * 검색조건 없을 경우 전체 목록 조회
     *
     * @param pageable
     * @return
     */
    private Mono<Page<LocationResponseDto>> findAll(Pageable pageable) {
        return locationRepository.findAllByOrderBySortSeq(pageable)
                .flatMap(this::convertLocationResponseDto)
                .collectList()
                .zipWith(locationRepository.count())
                .flatMap(tuple -> Mono.just(new PageImpl<>(tuple.getT1(), pageable, tuple.getT2())));
    }

    /**
     * entity를 응답 dto 형태로 변환
     *
     * @param location
     * @return
     */
    private Mono<LocationResponseDto> convertLocationResponseDto(Location location) {
        return Mono.just(LocationResponseDto.builder()
                .entity(location)
                .build());
    }

    /**
     * 예약 지역 목록 조회
     *
     * @param requestDto
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public Mono<Page<LocationResponseDto>> search(RequestDto requestDto, Pageable pageable) {
        if (!StringUtils.hasText(requestDto.getKeywordType()) || !StringUtils.hasText(requestDto.getKeyword())) {
            return findAll(pageable);
        }

        if ("locationName".equals(requestDto.getKeywordType())
                && StringUtils.hasText(requestDto.getKeyword())
        ) {
            return locationRepository.findAllByLocationNameContainingOrderBySortSeq(requestDto.getKeyword(), pageable)
                    .flatMap(this::convertLocationResponseDto)
                    .collectList()
                    .zipWith(locationRepository.countAllByLocationNameContaining(requestDto.getKeyword()))
                    .flatMap(tuple -> Mono.just(new PageImpl<>(tuple.getT1(), pageable, tuple.getT2())));
        }

        return findAll(pageable);
    }

    /**
     * 예약 지역 한건 조회
     *
     * @param locationId
     * @return
     */
    @Transactional(readOnly = true)
    public Mono<LocationResponseDto> findById(Long locationId) {
        return locationRepository.findById(locationId)
                .switchIfEmpty(monoResponseStatusEntityNotFoundException(locationId))
                .flatMap(this::convertLocationResponseDto);
    }

    /**
     * 지역 목록 조회 - 사용여부 = true
     *
     * @return
     */
    @Transactional(readOnly = true)
    public Flux<LocationResponseDto> findAll() {
        return locationRepository.findAllByIsUseTrueOrderBySortSeq()
                .flatMap(this::convertLocationResponseDto);
    }

    /**
     * 예약 지역 저장
     *
     * @param saveRequestDto
     * @return
     */
    public Mono<LocationResponseDto> save(LocationSaveRequestDto saveRequestDto) {
        return locationRepository.save(saveRequestDto.toEntity())
                .flatMap(this::convertLocationResponseDto);
    }

    /**
     * 예약 지역 한건 저장
     *
     * @param locationId
     * @param updateRequestDto
     * @return
     */
    public Mono<Void> update(Long locationId, LocationUpdateRequestDto updateRequestDto) {
        return locationRepository.findById(locationId)
                .switchIfEmpty(monoResponseStatusEntityNotFoundException(locationId))
                .map(location ->
                        location.update(updateRequestDto.getLocationName(),
                        updateRequestDto.getSortSeq(),
                        updateRequestDto.getIsUse())
                )
                .flatMap(locationRepository::save)
                .then();
    }

    /**
     * 예약 지역 한건 삭제
     *
     * @param locationId
     * @return
     */
    public Mono<Void> delete(Long locationId) {
        return locationRepository.findById(locationId)
                .switchIfEmpty(monoResponseStatusEntityNotFoundException(locationId))
                .flatMap(locationRepository::delete)
                .onErrorResume(DataIntegrityViolationException.class,
                        throwable -> Mono.error(new BusinessMessageException(getMessage("err.db.constraint.delete"))));
    }

    /**
     * 예약 지역 사용여부 토글
     *
     * @param locationId
     * @param isUse
     * @return
     */
    public Mono<Void> updateIsUse(Long locationId, Boolean isUse) {
        return locationRepository.findById(locationId)
                .switchIfEmpty(monoResponseStatusEntityNotFoundException(locationId))
                .map(location -> location.updateIsUse(isUse))
                .flatMap(locationRepository::save)
                .then();
    }
}
