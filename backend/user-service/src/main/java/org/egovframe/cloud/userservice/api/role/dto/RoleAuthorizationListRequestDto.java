package org.egovframe.cloud.userservice.api.role.dto;

import lombok.Getter;
import org.egovframe.cloud.common.dto.RequestDto;

import javax.validation.constraints.NotBlank;

/**
 * org.egovframe.cloud.userservice.api.role.dto.RoleAuthorizationListRequestDto
 * <p>
 * 권한 인가 목록 요청 DTO 클래스
 * 인가 목록 요청 DTO 클래스(org.egovframe.cloud.portalservice.api.authorization.dto.AuthorizationListRequestDto) 상속?
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/09
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/09    jooho       최초 생성
 * </pre>
 */
@Getter
public class RoleAuthorizationListRequestDto extends RequestDto {

    /**
     * 권한 id
     */
    @NotBlank(message = "{role.roleId} {err.required}")
    private String roleId;

}
