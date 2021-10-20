package org.egovframe.cloud.userservice.domain.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egovframe.cloud.servlet.domain.BaseTimeEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

/**
 * org.egovframe.cloud.userservice.domain.user.UserFindPassword
 * <p>
 * 사용자 비밀번호 찾기 엔티티 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/09/15
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *    수정일       수정자              수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/15    jooho       최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
@Entity
@DynamicInsert
@DynamicUpdate
public class UserFindPassword extends BaseTimeEntity {

    /**
     * 복합키
     */
    @EmbeddedId
    private UserFindPasswordId userFindPasswordId;

    /**
     * 토큰 값
     */
    @Column(nullable = false, length = 50)
    private String tokenValue;

    /**
     * 변경 여부
     */
    @Column(nullable = false, columnDefinition = "tinyint(1) default '0'")
    private Boolean changeAt;

    /**
     * 빌드 패턴 클래스 생성자
     *
     * @param emailAddr  이메일 주소
     * @param requestNo  요청 번호
     * @param tokenValue 토큰 값
     * @param changeAt   변경 여부
     */
    @Builder
    public UserFindPassword(String emailAddr, Integer requestNo, String tokenValue, Boolean changeAt) {
        this.userFindPasswordId = UserFindPasswordId.builder()
                .emailAddr(emailAddr)
                .requestNo(requestNo)
                .build();
        this.tokenValue = tokenValue;
        this.changeAt = changeAt;
    }

    /**
     * 변경 여부 수정
     *
     * @param changeAt 변경 여부
     * @return UserFindPassword 사용자 비밀번호 찾기 엔티티
     */
    public UserFindPassword updateChangeAt(Boolean changeAt) {
        this.changeAt = changeAt;

        return this;
    }

}
