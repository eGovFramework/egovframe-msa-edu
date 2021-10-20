package org.egovframe.cloud.boardservice.domain.comment;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egovframe.cloud.boardservice.domain.posts.Posts;
import org.egovframe.cloud.boardservice.domain.user.User;
import org.egovframe.cloud.servlet.domain.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * org.egovframe.cloud.boardservice.domain.comment.Comment
 * <p>
 * 댓글 엔티티 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/08/04
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/08/04    jooho       최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Entity
public class Comment extends BaseEntity {

    /**
     * 댓글 복합키
     */
    @EmbeddedId
    private CommentId commentId;

    /**
     * 댓글 내용
     */
    @Column(nullable = false, length = 2000)
    private String commentContent;

    /**
     * 그룹 번호
     */
    @Column(columnDefinition = "int(9)")
    private Integer groupNo;

    /**
     * 부모 댓글 번호
     */
    @Column(columnDefinition = "int(9)")
    private Integer parentCommentNo;

    /**
     * 깊이 순서
     */
    @Column(nullable = false, columnDefinition = "smallint(3)")
    private Integer depthSeq;

    /**
     * 정렬 순서
     */
    @Column(nullable = false, columnDefinition = "int(9)")
    private Integer sortSeq;

    /**
     * 삭제 여부
     */
    @Column(nullable = false, columnDefinition = "tinyint(1) default '0'")
    private Integer deleteAt; // 0:미삭제, 1:작성자삭제, 2:관리자삭제

    /**
     * 생성자 엔티티
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdBy", referencedColumnName = "userId", insertable = false, updatable = false)
    private User creator;

    /**
     * 게시물 엔티티
     */
    @MapsId("postsId") // CommentId.postsId 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "board_no"),
            @JoinColumn(name = "posts_no")
    })
    private Posts posts;

    /**
     * 빌더 패턴 클래스 생성자
     *
     * @param posts           게시물 엔티티
     * @param commentId       댓글 복합키
     * @param commentContent  게시물 내용
     * @param groupNo         그룹 번호
     * @param parentCommentNo 부모 댓글 번호
     * @param depthSeq        깊이 순서
     * @param sortSeq         정렬 순서
     * @param deleteAt        삭제 여부
     */
    @Builder
    public Comment(Posts posts, CommentId commentId, String commentContent,
                   Integer groupNo, Integer parentCommentNo, Integer depthSeq,
                   Integer sortSeq, Integer deleteAt, User creator) {
        this.posts = posts;
        this.commentId = commentId;
        this.commentContent = commentContent;
        this.groupNo = groupNo;
        this.parentCommentNo = parentCommentNo;
        this.depthSeq = depthSeq;
        this.sortSeq = sortSeq;
        this.deleteAt = deleteAt;
        this.creator = creator;
    }

    /**
     * 댓글 내용 수정
     *
     * @param commentContent 게시물 내용
     * @return Comment 댓글 엔티티
     */
    public Comment update(String commentContent) {
        this.commentContent = commentContent;

        return this;
    }

    /**
     * 댓글 삭제 여부 수정
     *
     * @param deleteAt 삭제 여부
     * @return Comment 댓글 엔티티
     */
    public Comment updateDeleteAt(Integer deleteAt) {
        this.deleteAt = deleteAt;

        return this;
    }

}
