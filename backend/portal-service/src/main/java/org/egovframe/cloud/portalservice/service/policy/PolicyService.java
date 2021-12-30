package org.egovframe.cloud.portalservice.service.policy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.common.exception.EntityNotFoundException;
import org.egovframe.cloud.common.service.AbstractService;
import org.egovframe.cloud.portalservice.api.policy.dto.PolicyResponseDto;
import org.egovframe.cloud.portalservice.api.policy.dto.PolicySaveRequestDto;
import org.egovframe.cloud.portalservice.api.policy.dto.PolicyUpdateRequestDto;
import org.egovframe.cloud.portalservice.domain.policy.Policy;
import org.egovframe.cloud.portalservice.domain.policy.PolicyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * org.egovframe.cloud.portalservice.service.policy.PolicyService
 * <p>
 * 이용약관/개인정보수집동의(Policy) service class
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
@Transactional
@Service
public class PolicyService extends AbstractService {
    private final PolicyRepository policyRepository;

    /**
     * 목록 조회
     *
     * @param requestDto
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public Page<PolicyResponseDto> search(RequestDto requestDto, Pageable pageable){
        return policyRepository.search(requestDto, pageable);
    }

    /**
     * 회원가입시 가장 최근에 등록한 한건 조회
     *
     * @param type
     * @return
     */
    @Transactional(readOnly = true)
    public PolicyResponseDto searchOne(String type) {
        return policyRepository.searchOne(type);
    }

    /**
     * 단건 조회
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public PolicyResponseDto findById(Long id) {
        Policy policy = findPolicy(id);
        return new PolicyResponseDto(policy);
    }

    /**
     * 등록
     *
     * @param saveRequestDto
     * @return
     */
    public Long save(PolicySaveRequestDto saveRequestDto) {
        return policyRepository.save(saveRequestDto.toEntity()).getId();
    }

    /**
     * 수정
     *
     * @param id
     * @param updateRequestDto
     * @return
     */
    public Long update(Long id, PolicyUpdateRequestDto updateRequestDto) {
        Policy policy = findPolicy(id);

        policy.update(updateRequestDto.getTitle(), updateRequestDto.getIsUse(), updateRequestDto.getContents());

        return id;
    }

    /**
     * 삭제
     *
     * @param id
     */
    public void delete(Long id) {
        Policy policy = findPolicy(id);
        policyRepository.delete(policy);
    }

    /**
     * 사용여부 toggle
     *
     * @param id
     * @param isUse
     * @return
     */
    public Long updateIsUse(Long id, boolean isUse) {
        Policy policy = findPolicy(id);

        policy.updateIsUSe(isUse);

        return id;
    }

    private Policy findPolicy(Long id) {
        return policyRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(getMessage("valid.notexists.format", new Object[]{getMessage("policy")}) + " ID= " + id));
    }



}
