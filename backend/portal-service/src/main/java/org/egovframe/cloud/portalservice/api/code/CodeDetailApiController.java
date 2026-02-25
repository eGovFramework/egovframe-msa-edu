package org.egovframe.cloud.portalservice.api.code;

import java.util.List;

import org.egovframe.cloud.portalservice.api.code.dto.CodeDetailListResponseDto;
import org.egovframe.cloud.portalservice.api.code.dto.CodeDetailRequestDto;
import org.egovframe.cloud.portalservice.api.code.dto.CodeDetailResponseDto;
import org.egovframe.cloud.portalservice.api.code.dto.CodeDetailSaveRequestDto;
import org.egovframe.cloud.portalservice.api.code.dto.CodeDetailUpdateRequestDto;
import org.egovframe.cloud.portalservice.api.code.dto.CodeResponseDto;
import org.egovframe.cloud.portalservice.domain.code.CodeRepository;
import org.egovframe.cloud.portalservice.service.code.CodeDetailService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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
@Tag(name = "Code Detail API", description = "공통코드 상세 관리 API")
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
    public List<CodeDetailResponseDto> findDetailsByParentCodeIdUseAt(@PathVariable("parentCodeId") String parentCodeId) {
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
    public List<CodeDetailResponseDto> findDetailsUnionCodeIdByParentCodeId(@PathVariable("parentCodeId") String parentCodeId, @PathVariable("codeId") String codeId) {
        return codeRepository.findDetailsUnionCodeIdByParentCodeId(parentCodeId, codeId);
    }

    /**
     * 공통코드 상세 단건 조회
     *
     * @param codeId
     * @return
     */
    @GetMapping("/api/v1/code-details/{codeId}")
    public CodeDetailResponseDto findByCodeId(@PathVariable("codeId") String codeId) {
        return codeDetailService.findByCodeId(codeId);
    }

    /**
     * 공통코드 부모코드 단건 조회
     *
     * @param codeId
     * @return
     */
    @GetMapping("/api/v1/code-details/{codeId}/parent")
    public CodeResponseDto findParentByCodeId(@PathVariable("codeId") String codeId) {
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
    public String update(@PathVariable("codeId") String codeId, @RequestBody CodeDetailUpdateRequestDto requestDto) {
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
    public String updateUseAt(@PathVariable("codeId") String codeId, @RequestParam boolean useAt) {
        return codeDetailService.updateUseAt(codeId, useAt);
    }

    /**
     * 공통코드 상세 삭제
     *
     * @param codeId
     */
    @DeleteMapping("/api/v1/code-details/{codeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("codeId") String codeId) {
        codeDetailService.delete(codeId);
    }

}
