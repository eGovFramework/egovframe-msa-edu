package org.egovframe.cloud.portalservice.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
    private Integer uploadLimitSize;

}
