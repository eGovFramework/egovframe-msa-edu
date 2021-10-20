package org.egovframe.cloud.userservice.api.role.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egovframe.cloud.userservice.domain.role.RoleAuthorization;

/**
 * org.egovframe.cloud.userservice.api.role.dto.RoleAuthorizationResponseDto
 * <p>
 * 권한 인가 상세 응답 DTO 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/12
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/12    jooho       최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
public class RoleAuthorizationResponseDto {

    /**
     * 권한 id
     */
    private String roleId;

    /**
     * 인가 번호
     */
    private Integer authorizationNo;

    /**
     * 인가 명
     */
    private String authorizationName;

    /**
     * URL 패턴 값
     */
    private String urlPatternValue;

    /**
     * Http Method 코드
     */
    private String httpMethodCode;

    /**
     * 정렬 순서
     */
    private Integer sortSeq;

    /**
     * 생성 여부
     */
    private Integer createdAt;

    /**
     * 권한 인가 엔티티를 생성자로 주입 받아서 권한 인가 상세 응답 DTO 속성 값 세팅
     *
     * @param entity 권한 인가 엔티티
     */
    public RoleAuthorizationResponseDto(RoleAuthorization entity) {
        this.roleId = entity.getRoleAuthorizationId().getRoleId();
        this.authorizationNo = entity.getAuthorization().getAuthorizationNo();
        this.authorizationName = entity.getAuthorization().getAuthorizationName();
        this.urlPatternValue = entity.getAuthorization().getUrlPatternValue();
        this.httpMethodCode = entity.getAuthorization().getHttpMethodCode();
        this.sortSeq = entity.getAuthorization().getSortSeq();
        this.createdAt = 1;
    }

}
