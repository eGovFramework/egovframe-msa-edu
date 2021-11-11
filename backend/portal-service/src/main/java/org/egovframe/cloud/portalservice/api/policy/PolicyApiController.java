package org.egovframe.cloud.portalservice.api.policy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.portalservice.api.policy.dto.PolicyResponseDto;
import org.egovframe.cloud.portalservice.api.policy.dto.PolicySaveRequestDto;
import org.egovframe.cloud.portalservice.api.policy.dto.PolicyUpdateRequestDto;
import org.egovframe.cloud.portalservice.service.policy.PolicyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * org.egovframe.cloud.portalservice.api.policy.PolicyApiController
 * <p>
 * 이용약관/개인정보수집동의(Policy) API class
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/07/06
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/06    shinmj  최초 생성
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class PolicyApiController {

    private final PolicyService policyService;

    /**
     * 목록 조회
     *
     * @param requestDto
     * @param pageable
     * @return
     */
    @GetMapping("/api/v1/policies")
    public Page<PolicyResponseDto> search(RequestDto requestDto, Pageable pageable) {
        return policyService.search(requestDto, pageable);
    }

    /**
     * 단건 조회
     *
     * @param id
     * @return
     */
    @GetMapping("/api/v1/policies/{id}")
    public PolicyResponseDto findById(@PathVariable Long id) {
        return policyService.findById(id);
    }

    /**
     * 회원가입 시 가장 최근등록 된 자료 단건 조회
     *
     * @param type
     * @return
     */
    @GetMapping("/api/v1/policies/latest/{type}")
    public PolicyResponseDto searchOne(@PathVariable String type) {
        return policyService.searchOne(type);
    }

    /**
     * 등록
     *
     * @param saveRequestDto
     * @return
     */
    @PostMapping("/api/v1/policies")
    @ResponseStatus(HttpStatus.CREATED)
    public Long save(@RequestBody PolicySaveRequestDto saveRequestDto) {
        return policyService.save(saveRequestDto);
    }

    /**
     * 수정
     *
     * @param id
     * @param updateRequestDto
     * @return
     */
    @PutMapping("/api/v1/policies/{id}")
    public Long update(@PathVariable Long id, @RequestBody PolicyUpdateRequestDto updateRequestDto) {
        return policyService.update(id, updateRequestDto);
    }

    /**
     * 사용여부 toggle
     *
     * @param id
     * @param isUse
     * @return
     */
    @PutMapping("/api/v1/policies/{id}/{isUse}")
    public Long updateIsUse(@PathVariable Long id, @PathVariable boolean isUse) {
        return policyService.updateIsUse(id, isUse);
    }

    /**
     * 삭제
     *
     * @param id
     */
    @DeleteMapping("/api/v1/policies/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        policyService.delete(id);
    }


}
