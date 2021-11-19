package org.egovframe.cloud.boardservice.domain.posts;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egovframe.cloud.boardservice.domain.board.Board;
import org.egovframe.cloud.boardservice.domain.comment.Comment;
import org.egovframe.cloud.boardservice.domain.user.User;
import org.egovframe.cloud.servlet.domain.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * org.egovframe.cloud.boardservice.domain.posts.Posts
 * <p>
 * 게시물 엔티티 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/28
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/28    jooho       최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Entity
public class Posts extends BaseEntity {

    /**
     * 게시물 복합키
     */
    @EmbeddedId
    private PostsId postsId;

    /**
     * 게시판 엔티티
     */
    @MapsId("boardNo") // PostsId.boardNo 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_no")
    private Board board;

    /**
     * 게시물 제목
     */
    @Column(nullable = false, length = 100)
    private String postsTitle;

    /**
     * 게시물 내용
     */
    @Column(nullable = false, columnDefinition = "longtext")
    private String postsContent;

    /**
     * 게시물 내용
     */
    @Column(columnDefinition = "longtext")
    private String postsAnswerContent;

    /**
     * 첨부파일 코드
     */
    @Column
    private String attachmentCode;

    /**
     * 조회 수
     */
    @Column(columnDefinition = "int(9) default '0'")
    private Integer readCount;

    /**
     * 공지 여부
     */
    @Column(nullable = false, columnDefinition = "tinyint(1) default '0'")
    private Boolean noticeAt;

    /**
     * 삭제 여부
     */
    @Column(nullable = false, columnDefinition = "tinyint(1) default '0'")
    private Integer deleteAt;

    /**
     * 생성자 엔티티
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdBy", referencedColumnName = "userId", insertable = false, updatable = false)
    private User creator;

    /**
     * 댓글 엔티티
     */
    @OneToMany(mappedBy = "posts", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Comment> comments;

    /**
     * 빌더 패턴 클래스 생성자
     *
     * @param board              게시판 엔티티
     * @param postsId            게시물 복합키
     * @param postsTitle         게시물 제목
     * @param postsContent       게시물 내용
     * @param postsAnswerContent 게시물 답변 내용
     * @param attachmentCode     첨부파일 코드
     * @param readCount          조회 수
     * @param noticeAt           공지 여부
     * @param deleteAt           삭제 여부
     */
    @Builder
    public Posts(Board board, PostsId postsId, String postsTitle,
                 String postsContent, String postsAnswerContent, String attachmentCode,
                 Integer readCount, Boolean noticeAt, Integer deleteAt,
                 User creator, List<Comment> comments) {
        this.postsId = postsId;
        this.postsTitle = postsTitle;
        this.postsContent = postsContent;
        this.postsAnswerContent = postsAnswerContent;
        this.attachmentCode = attachmentCode;
        this.readCount = readCount;
        this.noticeAt = noticeAt;
        this.deleteAt = deleteAt;
        this.creator = creator;
        this.comments = comments == null ? null : new ArrayList<>(comments);
        setBoard(board);
    }

    /**
     * 연관관계 설정
     *
     * @param board
     */
    public void setBoard(Board board) {
        this.board = board;
        board.getPosts().add(this);
    }

    /**
     * 게시물 속성 값 수정
     *
     * @param postsTitle         게시물 제목
     * @param postsContent       게시물 내용
     * @param attachmentCode     첨부파일 코드
     * @return Posts 게시물 엔티티
     */
    public Posts update(String postsTitle, String postsContent, String attachmentCode) {
        this.postsTitle = postsTitle;
        this.postsContent = postsContent;
        this.attachmentCode = attachmentCode;

        return this;
    }

    /**
     * 게시물 속성 값 수정
     *
     * @param postsTitle         게시물 제목
     * @param postsContent       게시물 내용
     * @param postsAnswerContent 게시물 답변 내용
     * @param attachmentCode     첨부파일 코드
     * @param noticeAt           공지 여부
     * @return Posts 게시물 엔티티
     */
    public Posts update(String postsTitle, String postsContent, String postsAnswerContent, String attachmentCode, Boolean noticeAt) {
        this.postsTitle = postsTitle;
        this.postsContent = postsContent;
        this.postsAnswerContent = postsAnswerContent;
        this.attachmentCode = attachmentCode;
        this.noticeAt = noticeAt;

        return this;
    }

    /**
     * 게시물 삭제 여부 수정
     *
     * @param deleteAt 삭제 여부
     * @return Posts 게시물 엔티티
     */
    public Posts updateDeleteAt(Integer deleteAt) {
        this.deleteAt = deleteAt;

        return this;
    }

    /**
     * 조회 수 증가
     *
     * @return Posts 게시물 엔티티
     */
    public Posts updateReadCount() {
        this.readCount += 1;

        return this;
    }

}
