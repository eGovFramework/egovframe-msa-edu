package org.egovframe.cloud.portalservice.api.privacy;

import lombok.RequiredArgsConstructor;
import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.portalservice.api.privacy.dto.PrivacyListResponseDto;
import org.egovframe.cloud.portalservice.api.privacy.dto.PrivacyResponseDto;
import org.egovframe.cloud.portalservice.api.privacy.dto.PrivacySaveRequestDto;
import org.egovframe.cloud.portalservice.api.privacy.dto.PrivacyUpdateRequestDto;
import org.egovframe.cloud.portalservice.service.privacy.PrivacyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * org.egovframe.cloud.portalservice.api.privacy.PrivacyApiController
 * <p>
 * 개인정보처리방침 Rest API 컨트롤러 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/22
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/22    jooho       최초 생성
 * </pre>
 */
@RequiredArgsConstructor
@RestController
public class PrivacyApiController {

    /**
     * 개인정보처리방침 서비스
     */
    private final PrivacyService privacyService;

    /**
     * 개인정보처리방침 페이지 목록 조회
     *
     * @param requestDto 요청 DTO
     * @param pageable   페이지 정보
     * @return Page<PrivacyListResponseDto> 페이지 개인정보처리방침 목록 응답 DTO
     */
    @GetMapping("/api/v1/privacies")
    public Page<PrivacyListResponseDto> findPage(RequestDto requestDto,
                                                 @PageableDefault(sort = "privacy_no", direction = Sort.Direction.DESC) Pageable pageable) {
        return privacyService.findPage(requestDto, pageable);
    }

    /**
     * 개인정보처리방침 사용중인 내림차순 전체 목록 조회
     *
     * @return List<PrivacyResponseDto> 개인정보처리방침 상세 응답 DTO List
     */
    @GetMapping("/api/v1/privacies/all/use")
    public List<PrivacyResponseDto> findAllByUse() {
        return privacyService.findAllByUseAt(true);
    }

    /**
     * 개인정보처리방침 단건 조회
     *
     * @param privacyNo 개인정보처리방침 번호
     * @return PrivacyResponseDto 개인정보처리방침 상세 응답 DTO
     */
    @GetMapping("/api/v1/privacies/{privacyNo}")
    public PrivacyResponseDto findById(@PathVariable Integer privacyNo) {
        return privacyService.findById(privacyNo);
    }

    /**
     * 개인정보처리방침 등록
     *
     * @param requestDto 개인정보처리방침 등록 요청 DTO
     * @return PrivacyResponseDto 개인정보처리방침 상세 응답 DTO
     */
    @PostMapping("/api/v1/privacies")
    @ResponseStatus(HttpStatus.CREATED)
    public PrivacyResponseDto save(@RequestBody @Valid PrivacySaveRequestDto requestDto) {
        return privacyService.save(requestDto);
    }

    /**
     * 개인정보처리방침 수정
     *
     * @param privacyNo  개인정보처리방침 번호
     * @param requestDto 개인정보처리방침 수정 요청 DTO
     * @return PrivacyResponseDto 개인정보처리방침 상세 응답 DTO
     */
    @PutMapping("/api/v1/privacies/{privacyNo}")
    public PrivacyResponseDto update(@PathVariable Integer privacyNo, @RequestBody @Valid PrivacyUpdateRequestDto requestDto) {
        return privacyService.update(privacyNo, requestDto);
    }

    /**
     * 개인정보처리방침 사용 여부 수정
     *
     * @param privacyNo 개인정보처리방침 번호
     * @param useAt     사용 여부
     * @return PrivacyResponseDto 개인정보처리방침 상세 응답 DTO
     */
    @PutMapping("/api/v1/privacies/{privacyNo}/{useAt}")
    public PrivacyResponseDto updateUseAt(@PathVariable Integer privacyNo, @PathVariable Boolean useAt) {
        return privacyService.updateUseAt(privacyNo, useAt);
    }

    /**
     * 개인정보처리방침 삭제
     *
     * @param privacyNo 개인정보처리방침 번호
     */
    @DeleteMapping("/api/v1/privacies/{privacyNo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer privacyNo) {
        privacyService.delete(privacyNo);
    }

}
