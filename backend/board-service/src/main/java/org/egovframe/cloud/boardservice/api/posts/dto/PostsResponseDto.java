package org.egovframe.cloud.boardservice.api.posts.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egovframe.cloud.boardservice.api.board.dto.BoardResponseDto;
import org.egovframe.cloud.boardservice.domain.posts.Posts;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * org.egovframe.cloud.boardservice.api.board.dto.PostsResponseDto
 * <p>
 * 게시물 상세 응답 DTO 클래스
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
public class PostsResponseDto implements Serializable {

    /**
     * SerialVersionUID
     */
    private static final long serialVersionUID = 8644170429040511387L;

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
     * 첨부파일 코드
     */
    private String attachmentCode;

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
     * 생성자
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
     * 게시판 응답 DTO
     */
    private BoardResponseDto board;

    /**
     * 신규 여부
     */
    private Boolean isNew;

    /**
     * 유저 게시물 조회 수
     */
    private Long userPostsReadCount;

    /**
     * 이전 게시물 응답 DTO List
     */
    private List<PostsSimpleResponseDto> prevPosts;

    /**
     * 다음 게시물 응답 DTO List
     */
    private List<PostsSimpleResponseDto> nextPosts;

    /**
     * 게시물 상세 응답 DTO 생성자
     *
     * @param boardNo            게시판 번호
     * @param postsNo            게시물 번호
     * @param postsTitle         게시물 제목
     * @param postsContent       게시물 내용
     * @param postsAnswerContent 게시물 답변 내용
     * @param attachmentCode     첨부파일 코드
     * @param readCount          조회 수
     * @param noticeAt           공지 여부
     * @param deleteAt           삭제 여부
     * @param createdBy          생성자 id
     * @param createdName        생성자 명
     * @param createdDate        생성 일시
     * @param board              게시판
     * @param userPostsReadCount 유저 게시물 조회 수
     */
    @QueryProjection
    public PostsResponseDto(Integer boardNo, Integer postsNo, String postsTitle, String postsContent,
                            String postsAnswerContent, String attachmentCode, Integer readCount, Boolean noticeAt,
                            Integer deleteAt, String createdBy, String createdName, LocalDateTime createdDate,
                            BoardResponseDto board, Long userPostsReadCount) {
        this.boardNo = boardNo;
        this.postsNo = postsNo;
        this.postsTitle = postsTitle;
        this.postsContent = postsContent;
        this.postsAnswerContent = postsAnswerContent;
        this.attachmentCode = attachmentCode;
        this.readCount = readCount;
        this.noticeAt = noticeAt;
        this.deleteAt = deleteAt;
        this.createdBy = createdBy;
        this.createdName = createdName;
        this.createdDate = createdDate;
        this.board = board;
        if (this.board.getNewDisplayDayCount() != null && this.board.getNewDisplayDayCount() > 0) {
            this.isNew = createdDate.plusDays(this.board.getNewDisplayDayCount()).compareTo(LocalDateTime.now()) <= 0;
        } else {
            this.isNew = false;
        }
        this.userPostsReadCount = userPostsReadCount;
    }

    /**
     * 게시물 엔티티를 생성자로 주입 받아서 게시물 상세 응답 DTO 속성 값 세팅
     *
     * @param entity 게시물 엔티티
     */
    public PostsResponseDto(Posts entity) {
        this.postsNo = entity.getPostsId().getPostsNo();
        this.boardNo = entity.getPostsId().getBoardNo();
        this.postsTitle = entity.getPostsTitle();
        this.postsContent = entity.getPostsContent();
        this.postsAnswerContent = entity.getPostsAnswerContent();
        this.attachmentCode = entity.getAttachmentCode();
        this.readCount = entity.getReadCount();
        this.noticeAt = entity.getNoticeAt();
        this.deleteAt = entity.getDeleteAt();
        this.createdBy = entity.getCreatedBy();
        this.createdName = entity.getCreator() != null ? entity.getCreator().getUserName() : null;
        this.createdDate = entity.getCreatedDate();
        this.board = new BoardResponseDto(entity.getBoard());
        if (this.board.getNewDisplayDayCount() != null) {
            this.isNew = createdDate.plusDays(this.board.getNewDisplayDayCount()).compareTo(LocalDateTime.now()) <= 0;
        } else {
            this.isNew = false;
        }
    }

    /**
     * 조회 수 증가
     */
    public void increaseReadCount() {
        this.readCount = this.readCount + 1;
    }

    /**
     * 이전 게시물
     */
    public void setPrevPosts(List<PostsSimpleResponseDto> prevPosts) {
        this.prevPosts = prevPosts == null ? null : new ArrayList<>(prevPosts);
    }

    /**
     * 다음 게시물
     */
    public void setNextPosts(List<PostsSimpleResponseDto> nextPosts) {
        this.nextPosts = nextPosts == null ? null : new ArrayList<>(nextPosts);
    }

}
