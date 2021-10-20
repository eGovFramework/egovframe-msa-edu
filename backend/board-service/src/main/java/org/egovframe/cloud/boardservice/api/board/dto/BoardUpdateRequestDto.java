package org.egovframe.cloud.boardservice.api.board.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egovframe.cloud.boardservice.domain.board.Board;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * org.egovframe.cloud.boardservice.api.board.dto.BoardUpdateRequestDto
 * <p>
 * 게시판 수정 요청 DTO 클래스
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
public class BoardUpdateRequestDto {

    /**
     * 게시판 제목
     */
    @NotBlank(message = "{board.board_name} {err.required}")
    private String boardName;

    /**
     * 스킨 유형 코드
     */
    @NotBlank(message = "{board.skin_type_code} {err.required}")
    private String skinTypeCode;

    /**
     * 제목 표시 길이
     */
    @NotNull(message = "{board.title_display_length} {err.required}")
    private Integer titleDisplayLength;

    /**
     * 게시물 표시 수
     */
    @NotNull(message = "{board.post_display_count} {err.required}")
    private Integer postDisplayCount;

    /**
     * 페이지 표시 수
     */
    @NotNull(message = "{board.page_display_count} {err.required}")
    private Integer pageDisplayCount;

    /**
     * 표시 신규 수
     */
    @NotNull(message = "{board.new_display_day_count} {err.required}")
    private Integer newDisplayDayCount;

    /**
     * 에디터 사용 여부
     */
    @NotNull(message = "{board.editor_use_at} {err.required}")
    private Boolean editorUseAt;

    /**
     * 사용자 작성 여부
     */
    @NotNull(message = "{board.user_write_at} {err.required}")
    private Boolean userWriteAt;

    /**
     * 댓글 사용 여부
     */
    @NotNull(message = "{board.comment_use_at} {err.required}")
    private Boolean commentUseAt;

    /**
     * 업로드 사용 여부
     */
    @NotNull(message = "{board.upload_use_at} {err.required}")
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
     * 게시판 등록 요청 DTO 클래스 생성자
     * 빌더 패턴으로 객체 생성
     *
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
     * @param uploadLimitCount    업로드 제한 수
     * @param uploadLimitSize    업로드 제한 크기
     */
    @Builder
    public BoardUpdateRequestDto(String boardName, String skinTypeCode, Integer titleDisplayLength, Integer postDisplayCount,
                                 Integer pageDisplayCount, Integer newDisplayDayCount, Boolean editorUseAt, Boolean userWriteAt,
                                 Boolean commentUseAt, Boolean uploadUseAt, Integer uploadLimitCount, BigDecimal uploadLimitSize) {
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
     * 게시판 등록 요청 DTO 속성 값으로 게시판 엔티티 빌더를 사용하여 객체 생성
     *
     * @return Board 게시판 엔티티
     */
    public Board toEntity() {
        return Board.builder()
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

}
