package org.egovframe.cloud.userservice.api.role.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * org.egovframe.cloud.userservice.api.role.dto.RoleAuthorizationListResponseDto
 * <p>
 * 권한 인가 목록 응답 DTO 클래스
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
public class RoleAuthorizationListResponseDto {

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
    private Boolean createdAt;

    /**
     * 권한 인가 목록 응답 DTO 클래스 생성자
     *
     * @param roleId            권한 id
     * @param authorizationNo   인가 번호
     * @param authorizationName 인가 명
     * @param urlPatternValue   URL 패턴 값
     * @param httpMethodCode    Http Method 코드
     * @param sortSeq           정렬 순서
     * @param createdAt          생성 여부
     */
    @QueryProjection
    @Builder
    public RoleAuthorizationListResponseDto(String roleId, Integer authorizationNo, String authorizationName,
                                            String urlPatternValue, String httpMethodCode, Integer sortSeq, Boolean createdAt) {
        this.roleId = roleId;
        this.authorizationNo = authorizationNo;
        this.authorizationName = authorizationName;
        this.urlPatternValue = urlPatternValue;
        this.httpMethodCode = httpMethodCode;
        this.sortSeq = sortSeq;
        this.createdAt = createdAt;
    }

}
