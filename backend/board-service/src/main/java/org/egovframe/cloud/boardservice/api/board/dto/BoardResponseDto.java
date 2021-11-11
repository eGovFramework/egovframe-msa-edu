package org.egovframe.cloud.boardservice.api.board.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egovframe.cloud.boardservice.api.posts.dto.PostsSimpleResponseDto;
import org.egovframe.cloud.boardservice.domain.board.Board;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * org.egovframe.cloud.boardservice.api.board.dto.BoardResponseDto
 * <p>
 * 게시판 상세 응답 DTO 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/26
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/26    jooho       최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
public class BoardResponseDto implements Serializable {

    /**
     * SerialVersionUID
     */
    private static final long serialVersionUID = -7139346671431363426L;

    /**
     * 게시판 번호
     */
    private Integer boardNo;

    /**
     * 게시판 제목
     */
    private String boardName;

    /**
     * 스킨 유형 코드
     */
    private String skinTypeCode;

    /**
     * 제목 표시 길이
     */
    private Integer titleDisplayLength;

    /**
     * 게시물 표시 수
     */
    private Integer postDisplayCount;

    /**
     * 페이지 표시 수
     */
    private Integer pageDisplayCount;

    /**
     * 표시 신규 수
     */
    private Integer newDisplayDayCount;

    /**
     * 에디터 사용 여부
     */
    private Boolean editorUseAt;

    /**
     * 사용자 작성 여부
     */
    private Boolean userWriteAt;

    /**
     * 댓글 사용 여부
     */
    private Boolean commentUseAt;

    /**
     * 업로드 사용 여부
     */
    private Boolean uploadUseAt;

    /**
     * 업로드 제한 수
     */
    private Integer uploadLimitCount;

    /**
     * 업로드 제한 크기
     */
    private BigDecimal uploadLimitSize;

    /**
     * 게시물 목록
     */
    private List<PostsSimpleResponseDto> posts;

    /**
     * 게시판 상세 응답 DTO 생성자
     *
     * @param boardNo            게시판 번호
     * @param boardName          게시판 명
     * @param skinTypeCode       스킨 유형 코드
     * @param titleDisplayLength 제목 표시 길이
     * @param postDisplayCount   게시물 표시 수
     * @param pageDisplayCount   페이지 표시 수
     * @param newDisplayDayCount 신규 표시 일 수
     * @param editorUseAt        에디터 사용 여부
     * @param userWriteAt        사용자 작성 여부
     * @param commentUseAt       댓글 사용 여부
     * @param uploadUseAt        업로드 사용 여부
     * @param uploadLimitCount   업로드 제한 수
     * @param uploadLimitSize    업로드 제한 크기
     */
    @QueryProjection
    public BoardResponseDto(Integer boardNo, String boardName, String skinTypeCode, Integer titleDisplayLength,
                            Integer postDisplayCount, Integer pageDisplayCount, Integer newDisplayDayCount, Boolean editorUseAt,
                            Boolean userWriteAt, Boolean commentUseAt, Boolean uploadUseAt, Integer uploadLimitCount,
                            BigDecimal uploadLimitSize) {
        this.boardNo = boardNo;
        this.boardName = boardName;
        this.skinTypeCode = skinTypeCode;
        this.titleDisplayLength = titleDisplayLength;
        this.postDisplayCount = postDisplayCount;
        this.pageDisplayCount = pageDisplayCount;
        this.newDisplayDayCount = newDisplayDayCount;
        this.editorUseAt = editorUseAt;
        this.userWriteAt = userWriteAt;
        this.commentUseAt = commentUseAt;
        this.uploadUseAt = uploadUseAt;
        this.uploadLimitCount = uploadLimitCount;
        this.uploadLimitSize = uploadLimitSize;
    }

    /**
     * 게시판 엔티티를 생성자로 주입 받아서 게시판 상세 응답 DTO 속성 값 세팅
     *
     * @param entity 게시판 엔티티
     */
    public BoardResponseDto(Board entity) {
        this.boardNo = entity.getBoardNo();
        this.boardName = entity.getBoardName();
        this.skinTypeCode = entity.getSkinTypeCode();
        this.titleDisplayLength = entity.getTitleDisplayLength();
        this.postDisplayCount = entity.getPostDisplayCount();
        this.pageDisplayCount = entity.getPageDisplayCount();
        this.newDisplayDayCount = entity.getNewDisplayDayCount();
        this.editorUseAt = entity.getEditorUseAt();
        this.userWriteAt = entity.getUserWriteAt();
        this.commentUseAt = entity.getCommentUseAt();
        this.uploadUseAt = entity.getUploadUseAt();
        this.uploadLimitCount = entity.getUploadLimitCount();
        this.uploadLimitSize = entity.getUploadLimitSize();
    }

    /**
     * 게시판 상세 응답 DTO 속성 값으로 게시판 엔티티 빌더를 사용하여 객체 생성
     *
     * @return Board 게시판 엔티티
     */
    public Board toEntity() {
        return Board.builder()
                .boardNo(boardNo)
                .boardName(boardName)
                .skinTypeCode(skinTypeCode)
                .titleDisplayLength(titleDisplayLength)
                .postDisplayCount(postDisplayCount)
                .pageDisplayCount(pageDisplayCount)
                .newDisplayDayCount(newDisplayDayCount)
                .editorUseAt(editorUseAt)
                .userWriteAt(userWriteAt)
                .commentUseAt(commentUseAt)
                .uploadUseAt(uploadUseAt)
                .uploadLimitCount(uploadLimitCount)
                .uploadLimitSize(uploadLimitSize)
                .build();
    }

    /**
     * 최근 게시물 목록 세팅
     *
     * @param posts 게시물 목록
     */
    public void setNewestPosts(List<PostsSimpleResponseDto> posts) {
        this.posts = posts == null ? null : new ArrayList<>(posts);
    }

}
