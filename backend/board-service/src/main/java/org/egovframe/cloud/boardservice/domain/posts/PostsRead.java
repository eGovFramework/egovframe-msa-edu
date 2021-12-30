package org.egovframe.cloud.boardservice.domain.posts;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import java.time.LocalDateTime;

/**
 * org.egovframe.cloud.boardservice.domain.posts.PostsRead
 * <p>
 * 게시물 조회 엔티티 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/08/02
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/08/02    jooho       최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class) // Auditing 기능 포함
public class PostsRead {

    /**
     * 게시물 조회 복합키
     */
    @EmbeddedId
    private PostsReadId postsReadId;

    /**
     * 사용자 id
     */
    private String userId;

    /**
     * ip 주소
     */
    @Column(nullable = false, columnDefinition = "varchar(100)")
    private String ipAddr;

    /**
     * 생성 일시
     */
    @CreatedDate
    @Column
    private LocalDateTime createdDate;

    /**
     * 빌더 패턴 클래스 생성자
     *
     * @param boardNo 게시판 번호
     * @param postsNo 게시물 번호
     * @param readNo  조회 번호
     * @param userId  사용자 id
     * @param tokenId 토큰 id
     * @param ipAddr  ip 주소
     */
    @Builder
    public PostsRead(Integer boardNo, Integer postsNo, Integer readNo, String userId, String tokenId, String ipAddr) {
        this.postsReadId = PostsReadId.builder()
                .boardNo(boardNo)
                .postsNo(postsNo)
                .readNo(readNo)
                .build();
        this.userId = userId;
        this.ipAddr = ipAddr;
    }

}
