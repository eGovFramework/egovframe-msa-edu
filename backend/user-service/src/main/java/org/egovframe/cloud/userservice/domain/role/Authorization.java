package org.egovframe.cloud.userservice.domain.role;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.egovframe.cloud.servlet.domain.BaseEntity;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * org.egovframe.cloud.userservice.domain.role.Authorization
 * <p>
 * 인가 엔티티 클래스
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
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
@ToString
public class Authorization extends BaseEntity {

    /**
     * 인가 번호
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer authorizationNo;

    /**
     * 인가 명
     */
    @Column(nullable = false, length = 50)
    private String authorizationName;

    /**
     * URL 패턴 값
     */
    @Column(nullable = false, length = 200)
    private String urlPatternValue;

    /**
     * Http Method 코드
     */
    @Column(nullable = false, length = 20)
    private String httpMethodCode;

    /**
     * 정렬 순서
     */
    @Column()
    private Integer sortSeq;

    /**
     * 권한 인가 엔티티
     */
    @ToString.Exclude
    @OneToMany(mappedBy = "authorization", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<RoleAuthorization> roleAuthorizations;

    /**
     * 빌더 패턴 클래스 생성자
     *
     * @param authorizationNo   인가 번호
     * @param authorizationName 인가 명
     * @param urlPatternValue   URL 패턴 값
     * @param httpMethodCode    Http Method 코드
     * @param sortSeq           정렬 순서
     */
    @Builder
    public Authorization(Integer authorizationNo, String authorizationName, String urlPatternValue, String httpMethodCode, Integer sortSeq, List<RoleAuthorization> roleAuthorizations) {
        this.authorizationNo = authorizationNo;
        this.authorizationName = authorizationName;
        this.urlPatternValue = urlPatternValue;
        this.httpMethodCode = httpMethodCode;
        this.sortSeq = sortSeq;
        this.roleAuthorizations = roleAuthorizations == null ? null : new ArrayList<>(roleAuthorizations);
    }

    /**
     * 인가 속성 값 수정
     *
     * @param authorizationName 인가 명
     * @param urlPatternValue   URL 패턴 값
     * @param httpMethodCode    Http Method 코드
     * @param sortSeq           정렬 순서
     * @return Authorization 인가 엔티티
     */
    public Authorization update(String authorizationName, String urlPatternValue, String httpMethodCode, Integer sortSeq) {
        this.authorizationName = authorizationName;
        this.urlPatternValue = urlPatternValue;
        this.httpMethodCode = httpMethodCode;
        this.sortSeq = sortSeq;

        return this;
    }

}
