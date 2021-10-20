package org.egovframe.cloud.portalservice.domain.policy;

import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.portalservice.api.policy.dto.PolicyResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PolicyRepositoryCustom {
    Page<PolicyResponseDto> search(RequestDto requestDto, Pageable pageable);
    PolicyResponseDto searchOne(String type);
}
