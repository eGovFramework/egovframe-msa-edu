package org.egovframe.cloud.boardservice.api.comment.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * org.egovframe.cloud.boardservice.api.comment.dto.CommentListResponseDto
 * <p>
 * 댓글 목록 응답 DTO 클래스
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
public class CommentListResponseDto implements Serializable {

    /**
     * SerialVersionUID
     */
    private static final long serialVersionUID = -8163130888886378482L;

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
     * 댓글 목록 응답 DTO 생성자
     *
     * @param boardNo         게시판 번호
     * @param postsNo         게시물 번호
     * @param commentNo       댓글 번호
     * @param commentContent  댓글 내용
     * @param parentCommentNo 부모 댓글 번호
     * @param depthSeq        깊이 순서
     * @param sortSeq         정렬 순서
     * @param deleteAt        삭제 여부
     * @param createdBy       생성자 id
     * @param createdName     생성자 명
     * @param createdDate     생성 일시
     */
    @QueryProjection
    public CommentListResponseDto(Integer boardNo, Integer postsNo, Integer commentNo,
                                  String commentContent, Integer groupNo, Integer parentCommentNo,
                                  Integer depthSeq, Integer sortSeq, Integer deleteAt,
                                  String createdBy, String createdName, LocalDateTime createdDate) {
        this.boardNo = boardNo;
        this.postsNo = postsNo;
        this.commentNo = commentNo;
        this.commentContent = commentContent;
        this.groupNo = groupNo;
        this.parentCommentNo = parentCommentNo;
        this.depthSeq = depthSeq;
        this.sortSeq = sortSeq;
        this.deleteAt = deleteAt;
        this.createdBy = createdBy;
        this.createdName = createdName;
        this.createdDate = createdDate;
    }

}
