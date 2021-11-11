package org.egovframe.cloud.portalservice.api.code;

import lombok.RequiredArgsConstructor;
import org.egovframe.cloud.portalservice.api.code.dto.*;
import org.egovframe.cloud.portalservice.domain.code.CodeRepository;
import org.egovframe.cloud.portalservice.service.code.CodeDetailService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * org.egovframe.cloud.portalservice.api.code.CodeApiController
 * <p>
 * 공통코드 CRUD 요청을 처리하는 REST API Controller
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/07/12
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/12    jaeyeolkim  최초 생성
 * </pre>
 */
@RequiredArgsConstructor // final이 선언된 모든 필드를 인자값으로 하는 생성자를 대신 생성하여, 빈을 생성자로 주입받게 한다.
@RestController
public class CodeDetailApiController {

    private final CodeDetailService codeDetailService;
    private final CodeRepository codeRepository;

    /**
     * 공통코드 상세 목록 조회
     *
     * @param requestDto
     * @param pageable
     * @return
     */
    @GetMapping("/api/v1/code-details")
    public Page<CodeDetailListResponseDto> findAllByKeyword(CodeDetailRequestDto requestDto, Pageable pageable) {
        return codeRepository.findAllDetailByKeyword(requestDto, pageable);
    }

    /**
     * 부모공통코드의 상세 목록 조회
     * 사용중인 공통코드만 조회한다
     *
     * @param parentCodeId
     * @return
     */
    @GetMapping("/api/v1/code-details/{parentCodeId}/codes")
    public List<CodeDetailResponseDto> findDetailsByParentCodeIdUseAt(@PathVariable String parentCodeId) {
        return codeRepository.findDetailsByParentCodeIdUseAt(parentCodeId);
    }

    /**
     * 부모공통코드의 상세 목록 조회
     * 사용여부가 false 로 변경된 경우에도 인자로 받은 공통코드를 목록에 포함되도록 한다
     *
     * @param parentCodeId
     * @return
     */
    @GetMapping("/api/v1/code-details/{parentCodeId}/codes/{codeId}")
    public List<CodeDetailResponseDto> findDetailsUnionCodeIdByParentCodeId(@PathVariable String parentCodeId, @PathVariable String codeId) {
        return codeRepository.findDetailsUnionCodeIdByParentCodeId(parentCodeId, codeId);
    }

    /**
     * 공통코드 상세 단건 조회
     *
     * @param codeId
     * @return
     */
    @GetMapping("/api/v1/code-details/{codeId}")
    public CodeDetailResponseDto findByCodeId(@PathVariable String codeId) {
        return codeDetailService.findByCodeId(codeId);
    }

    /**
     * 공통코드 부모코드 단건 조회
     *
     * @param codeId
     * @return
     */
    @GetMapping("/api/v1/code-details/{codeId}/parent")
    public CodeResponseDto findParentByCodeId(@PathVariable String codeId) {
        return codeRepository.findParentByCodeId(codeId);
    }

    /**
     * 공통코드 상세 등록
     *
     * @param requestDto
     * @return
     */
    @PostMapping("/api/v1/code-details")
    @ResponseStatus(HttpStatus.CREATED)
    public String save(@RequestBody @Valid CodeDetailSaveRequestDto requestDto) {
        return codeDetailService.save(requestDto);
    }

    /**
     * 공통코드 상세 수정
     *
     * @param codeId
     * @param requestDto
     * @return
     */
    @PutMapping("/api/v1/code-details/{codeId}")
    public String update(@PathVariable String codeId, @RequestBody CodeDetailUpdateRequestDto requestDto) {
        return codeDetailService.update(codeId, requestDto);
    }

    /**
     * 사용여부 toggle
     *
     * @param codeId
     * @param useAt
     * @return
     */
    @PutMapping("/api/v1/code-details/{codeId}/toggle-use")
    public String updateUseAt(@PathVariable String codeId, @RequestParam boolean useAt) {
        return codeDetailService.updateUseAt(codeId, useAt);
    }

    /**
     * 공통코드 상세 삭제
     *
     * @param codeId
     */
    @DeleteMapping("/api/v1/code-details/{codeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String codeId) {
        codeDetailService.delete(codeId);
    }

}
