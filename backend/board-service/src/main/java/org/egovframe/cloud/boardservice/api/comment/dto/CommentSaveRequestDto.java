package org.egovframe.cloud.boardservice.api.comment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egovframe.cloud.boardservice.domain.comment.Comment;
import org.egovframe.cloud.boardservice.domain.comment.CommentId;
import org.egovframe.cloud.boardservice.domain.posts.Posts;
import org.egovframe.cloud.boardservice.domain.posts.PostsId;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * org.egovframe.cloud.boardservice.api.comment.dto.CommentSaveRequestDto
 * <p>
 * 댓글 등록 요청 DTO 클래스
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
public class CommentSaveRequestDto {

    /**
     * 게시판 번호
     */
    @NotNull(message = "{board.board_no} {err.required}")
    private Integer boardNo;

    /**
     * 게시물 번호
     */
    @NotNull(message = "{posts.posts_no} {err.required}")
    private Integer postsNo;

    /**
     * 댓글 내용
     */
    @NotBlank(message = "{comment.comment_content} {err.required}")
    private String commentContent;

    /**
     * 그룹 번호
     */
    private Integer groupNo;

    /**
     * 부모 댓글 번호
     */
    private Integer parentCommentNo;

    /**
     * 깊이 순서
     */
    private Integer depthSeq;

    /**
     * 댓글 등록 요청 DTO 클래스 생성자
     * 빌더 패턴으로 객체 생성
     *
     * @param boardNo         게시판 번호
     * @param postsNo         게시물 번호
     * @param commentContent  댓글 내용
     * @param groupNo         그룹 번호
     * @param parentCommentNo 부모 댓글 번호
     * @param depthSeq        깊이 순서
     */
    @Builder
    public CommentSaveRequestDto(Integer boardNo, Integer postsNo, String commentContent, Integer groupNo, Integer parentCommentNo, Integer depthSeq) {
        this.boardNo = boardNo;
        this.postsNo = postsNo;
        this.commentContent = commentContent;
        this.groupNo = groupNo;
        this.parentCommentNo = parentCommentNo;
        this.depthSeq = depthSeq;
    }

    /**
     * 댓글 등록 요청 DTO 속성 값으로 댓글 엔티티 빌더를 사용하여 객체 생성
     *
     * @param commentNo 댓글 번호
     * @param sortSeq   정렬 순서
     * @return Comment 댓글 엔티티
     */
    public Comment toEntity(Integer commentNo, Integer sortSeq) {
        return Comment.builder()
                .commentId(CommentId.builder()
                        .postsId(PostsId.builder()
                                .boardNo(boardNo)
                                .postsNo(postsNo)
                                .build())
                        .commentNo(commentNo)
                        .build())
                .commentContent(commentContent)
                .groupNo(groupNo)
                .parentCommentNo(parentCommentNo)
                .depthSeq(depthSeq)
                .sortSeq(sortSeq)
                .build();
    }

    /**
     * 댓글 등록 요청 DTO 속성 값으로 댓글 엔티티 빌더를 사용하여 객체 생성
     *
     * @param posts     게시물 엔티티
     * @param commentNo 댓글 번호
     * @param groupNo   그룹 번호
     * @param sortSeq   정렬 순서
     * @return Comment 댓글 엔티티
     */
    public Comment toEntity(Posts posts, Integer commentNo, Integer groupNo, Integer sortSeq) {
        return Comment.builder()
                .posts(posts)
                .commentId(CommentId.builder()
                        .postsId(posts.getPostsId())
                        .commentNo(commentNo)
                        .build())
                .commentContent(commentContent)
                .groupNo(groupNo)
                .parentCommentNo(parentCommentNo)
                .depthSeq(depthSeq)
                .sortSeq(sortSeq)
                .build();
    }

}
