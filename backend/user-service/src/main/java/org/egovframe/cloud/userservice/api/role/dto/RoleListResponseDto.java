package org.egovframe.cloud.userservice.api.role.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * org.egovframe.cloud.userservice.api.role.dto.RoleListResponseDto
 * <p>
 * 권한 목록 응답 DTO 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/07
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/07    jooho       최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
public class RoleListResponseDto {

    /**
     * 권한 id
     */
    private String roleId;

    /**
     * 권한 명
     */
    private String roleName;

    /**
     * 권한 내용
     */
    private String roleContent;

    /**
     * 생성 일시
     */
    private LocalDateTime createdDate;

    /**
     * 권한 목록 응답 DTO 클래스 생성자
     *
     * @param roleId      권한 id
     * @param roleName    권한 명
     * @param roleContent 권한 내용
     * @param createdDate 생성 일시
     */
    @QueryProjection
    @Builder
    public RoleListResponseDto(String roleId, String roleName, String roleContent, LocalDateTime createdDate) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.roleContent = roleContent;
        this.createdDate = createdDate;
    }

}
