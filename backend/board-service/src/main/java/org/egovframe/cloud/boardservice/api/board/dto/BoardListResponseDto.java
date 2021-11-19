package org.egovframe.cloud.boardservice.api.board.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * org.egovframe.cloud.boardservice.api.board.dto.BoardListResponseDto
 * <p>
 * 게시판 목록 응답 DTO 클래스
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
public class BoardListResponseDto implements Serializable {

    /**
     * SerialVersionUID
     */
    private static final long serialVersionUID = 115181553377528728L;

    /**
     * 게시판 번호
     */
    private Integer boardNo;

    /**
     * 게시판 명
     */
    private String boardName;

    /**
     * 스킨 유형 코드
     */
    private String skinTypeCode;

    /**
     * 스킨 유형 코드 명
     */
    private String skinTypeCodeName;

    /**
     * 생성 일시
     */
    private LocalDateTime createdDate;

    /**
     * 게시글이 있는지 여부
     */
    private Boolean isPosts;

    /**
     * 게시판 목록 응답 DTO 생성자
     *
     * @param boardNo          게시판 번호
     * @param boardName        게시판 명
     * @param skinTypeCode     스킨 유형 코드
     */
    @QueryProjection
    public BoardListResponseDto(Integer boardNo, String boardName, String skinTypeCode, Boolean isPosts) {
        this.boardNo = boardNo;
        this.boardName = boardName;
        this.skinTypeCode = skinTypeCode;
        this.isPosts = isPosts;
    }

    /**
     * 게시판 목록 응답 DTO 생성자
     *
     * @param boardNo          게시판 번호
     * @param boardName        게시판 명
     * @param skinTypeCode     스킨 유형 코드
     * @param skinTypeCodeName 스킨 유형 코드 명
     * @param createdDate      생성 일시
     */
    @QueryProjection
    public BoardListResponseDto(Integer boardNo, String boardName, String skinTypeCode, String skinTypeCodeName, LocalDateTime createdDate, Boolean isPosts) {
        this.boardNo = boardNo;
        this.boardName = boardName;
        this.skinTypeCode = skinTypeCode;
        this.skinTypeCodeName = skinTypeCodeName;
        this.createdDate = createdDate;
        this.isPosts = isPosts;
    }

}
