package org.egovframe.cloud.boardservice.api.comment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egovframe.cloud.boardservice.domain.comment.Comment;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * org.egovframe.cloud.boardservice.api.comment.dto.CommentResponseDto
 *
 * 댓글 상세 응답 DTO 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/08/11
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *    수정일       수정자              수정내용
 *  ----------    --------    ---------------------------
 *  2021/08/11    jooho       최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
public class CommentResponseDto implements Serializable {

    /**
     * SerialVersionUID
     */
    private static final long serialVersionUID = -3087424580328463654L;

    /**
     * 게시판 번호
     */
    private Integer boardNo;

    /**
     * 게시물 번호
     */
    private Integer postsNo;

    /**
     * 댓글 번호
     */
    private Integer commentNo;

    /**
     * 댓글 내용
     */
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
     * 정렬 순서
     */
    private Integer sortSeq;

    /**
     * 삭제 여부
     */
    private Integer deleteAt;

    /**
     * 생성자 id
     */
    private String createdBy;

    /**
     * 생성자 명
     */
    private String createdName;

    /**
     * 생성 일시
     */
    private LocalDateTime createdDate;

    /**
     * 댓글 엔티티를 생성자로 주입 받아서 댓글 상세 응답 DTO 속성 값 세팅
     *
     * @param entity 댓글 엔티티
     */
    public CommentResponseDto(Comment entity) {
        this.postsNo = entity.getCommentId().getPostsId().getPostsNo();
        this.boardNo = entity.getCommentId().getPostsId().getBoardNo();
        this.commentNo = entity.getCommentId().getCommentNo();
        this.commentContent = entity.getCommentContent();
        this.groupNo = entity.getGroupNo();
        this.parentCommentNo = entity.getParentCommentNo();
        this.depthSeq = entity.getDepthSeq();
        this.sortSeq = entity.getSortSeq();
        this.deleteAt = entity.getDeleteAt();
        this.createdBy = entity.getCreatedBy();
        this.createdName = entity.getCreator() != null ? entity.getCreator().getUserName() : null;
        this.createdDate = entity.getCreatedDate();
    }

}
