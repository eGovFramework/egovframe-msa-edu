package org.egovframe.cloud.boardservice.api.posts.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * org.egovframe.cloud.boardservice.api.board.dto.PostsListResponseDto
 * <p>
 * 게시물 목록 응답 DTO 클래스
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
public class PostsListResponseDto implements Serializable {

    /**
     * SerialVersionUID
     */
    private static final long serialVersionUID = 3316086575500238046L;

    /**
     * 게시판 번호
     */
    private Integer boardNo;

    /**
     * 게시물 번호
     */
    private Integer postsNo;

    /**
     * 게시물 제목
     */
    private String postsTitle;

    /**
     * 게시물 내용
     */
    private String postsContent;

    /**
     * 게시물 답변 내용
     */
    private String postsAnswerContent;

    /**
     * 조회 수
     */
    private Integer readCount;

    /**
     * 공지 여부
     */
    private Boolean noticeAt;

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
     * 신규 여부
     */
    private Boolean isNew;

    /**
     * 댓글 수
     */
    private Long commentCount;

    /**
     * 게시물 목록 응답 DTO 생성자
     *
     * @param boardNo            게시판 번호
     * @param postsNo            게시물 번호
     * @param postsTitle         게시물 제목
     * @param postsContent       게시물 내용
     * @param postsAnswerContent 게시물 답변 내용
     * @param readCount          조회 수
     * @param noticeAt           공지 여부
     * @param deleteAt           삭제 여부
     * @param createdBy          생성자 id
     * @param createdName        생성자 명
     * @param createdDate        생성 일시
     * @param commentCount       댓글 수
     */
    @QueryProjection
    public PostsListResponseDto(Integer boardNo, Integer postsNo, String postsTitle, String postsContent,
                                String postsAnswerContent, Integer readCount, Boolean noticeAt, Integer deleteAt,
                                String createdBy, String createdName, LocalDateTime createdDate, Integer newDisplayDayCount,
                                Long commentCount) {
        this.boardNo = boardNo;
        this.postsNo = postsNo;
        this.postsTitle = postsTitle;
        this.postsContent = postsContent;
        this.postsAnswerContent = postsAnswerContent;
        this.readCount = readCount;
        this.noticeAt = noticeAt;
        this.deleteAt = deleteAt;
        this.createdBy = createdBy;
        this.createdName = createdName;
        this.createdDate = createdDate;
        this.isNew = createdDate.plusDays(newDisplayDayCount).compareTo(LocalDateTime.now()) >= 0;
        this.commentCount = commentCount;
    }

}
