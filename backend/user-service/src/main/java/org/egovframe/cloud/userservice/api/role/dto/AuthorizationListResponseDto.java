package org.egovframe.cloud.userservice.api.role.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * org.egovframe.cloud.userservice.api.role.dto.AuthorizationListResponseDto
 * <p>
 * 인가 목록 응답 DTO 클래스
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
public class AuthorizationListResponseDto implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 7400347728171964946L;

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
     * 인가 목록 응답 DTO 생성자
     *
     * @param authorizationNo   인가 번호
     * @param authorizationName 인가 명
     * @param urlPatternValue   URL 패턴 값
     * @param httpMethodCode    Http Method 코드
     * @param sortSeq           정렬 순서
     */
    @QueryProjection
    public AuthorizationListResponseDto(Integer authorizationNo, String authorizationName, String urlPatternValue, String httpMethodCode, Integer sortSeq) {
        this.authorizationNo = authorizationNo;
        this.authorizationName = authorizationName;
        this.urlPatternValue = urlPatternValue;
        this.httpMethodCode = httpMethodCode;
        this.sortSeq = sortSeq;
    }

}
