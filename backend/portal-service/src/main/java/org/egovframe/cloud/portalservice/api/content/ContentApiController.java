package org.egovframe.cloud.portalservice.api.content;

import lombok.RequiredArgsConstructor;
import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.portalservice.api.content.dto.ContentListResponseDto;
import org.egovframe.cloud.portalservice.api.content.dto.ContentResponseDto;
import org.egovframe.cloud.portalservice.api.content.dto.ContentSaveRequestDto;
import org.egovframe.cloud.portalservice.api.content.dto.ContentUpdateRequestDto;
import org.egovframe.cloud.portalservice.service.content.ContentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * org.egovframe.cloud.portalservice.api.content.ContentApiController
 * <p>
 * 컨텐츠 Rest API 컨트롤러 클래스
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
public class ContentApiController {

    /**
     * 컨텐츠 서비스
     */
    private final ContentService contentService;

    /**
     * 컨텐츠 페이지 목록 조회
     *
     * @param requestDto 요청 DTO
     * @param pageable   페이지 정보
     * @return Page<ContentListResponseDto> 페이지 컨텐츠 목록 응답 DTO
     */
    @GetMapping("/api/v1/contents")
    public Page<ContentListResponseDto> findPage(RequestDto requestDto,
                                                 @PageableDefault(sort = "content_no", direction = Sort.Direction.DESC) Pageable pageable) {
        return contentService.findPage(requestDto, pageable);
    }

    /**
     * 컨텐츠 단건 조회
     *
     * @param contentNo 컨텐츠 번호
     * @return ContentResponseDto 컨텐츠 상세 응답 DTO
     */
    @GetMapping("/api/v1/contents/{contentNo}")
    public ContentResponseDto findById(@PathVariable Integer contentNo) {
        return contentService.findById(contentNo);
    }

    /**
     * 컨텐츠 등록
     *
     * @param requestDto 컨텐츠 등록 요청 DTO
     * @return ContentResponseDto 컨텐츠 상세 응답 DTO
     */
    @PostMapping("/api/v1/contents")
    @ResponseStatus(HttpStatus.CREATED)
    public ContentResponseDto save(@RequestBody @Valid ContentSaveRequestDto requestDto) {
        return contentService.save(requestDto);
    }

    /**
     * 컨텐츠 수정
     *
     * @param contentNo  컨텐츠 번호
     * @param requestDto 컨텐츠 수정 요청 DTO
     * @return ContentResponseDto 컨텐츠 상세 응답 DTO
     */
    @PutMapping("/api/v1/contents/{contentNo}")
    public ContentResponseDto update(@PathVariable Integer contentNo, @RequestBody @Valid ContentUpdateRequestDto requestDto) {
        return contentService.update(contentNo, requestDto);
    }

    /**
     * 컨텐츠 삭제
     *
     * @param contentNo 컨텐츠 번호
     */
    @DeleteMapping("/api/v1/contents/{contentNo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer contentNo) {
        contentService.delete(contentNo);
    }

}
