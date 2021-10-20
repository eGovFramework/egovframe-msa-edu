package org.egovframe.cloud.userservice.api.role.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egovframe.cloud.userservice.domain.role.Authorization;

import java.util.List;
import java.util.stream.Collectors;

/**
 * org.egovframe.cloud.userservice.api.role.dto.AuthorizationResponseDto
 * <p>
 * 인가 상세 응답 DTO 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/08
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/08    jooho       최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
public class AuthorizationResponseDto {

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
     * 권한 인가 목록 응답 DTO
     */
    private List<RoleAuthorizationListResponseDto> roleAuthorizations;

    /**
     * 인가 엔티티를 생성자로 주입 받아서 인가 상세 응답 DTO 속성 값 세팅
     *
     * @param entity 인가 엔티티
     */
    public AuthorizationResponseDto(Authorization entity) {
        this.authorizationNo = entity.getAuthorizationNo();
        this.authorizationName = entity.getAuthorizationName();
        this.urlPatternValue = entity.getUrlPatternValue();
        this.httpMethodCode = entity.getHttpMethodCode();
        this.sortSeq = entity.getSortSeq();
        if (entity.getRoleAuthorizations() != null) {
            this.roleAuthorizations = entity.getRoleAuthorizations().stream()
//                .map(RoleAuthorizationListResponseDto::new)
                    .map(e -> RoleAuthorizationListResponseDto.builder()
                            .roleId(e.getRoleAuthorizationId().getRoleId())
                            .authorizationNo(e.getRoleAuthorizationId().getAuthorizationNo())
                            .build())
                    .collect(Collectors.toList());
        }
    }

    /**
     * 인가 상세 응답 DTO 속성 값으로 인가 엔티티 빌더를 사용하여 객체 생성
     *
     * @return Authorization 인가 엔티티
     */
    public Authorization toEntity() {
        return Authorization.builder()
                .authorizationNo(authorizationNo)
                .authorizationName(authorizationName)
                .urlPatternValue(urlPatternValue)
                .httpMethodCode(httpMethodCode)
                .sortSeq(sortSeq)
                .build();
    }

}
