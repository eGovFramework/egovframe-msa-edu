package org.egovframe.cloud.boardservice.api.posts.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egovframe.cloud.boardservice.api.board.dto.BoardResponseDto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * org.egovframe.cloud.boardservice.api.posts.dto.PostsSimpleResponseDto
 * <p>
 * 게시물 응답 DTO 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/09/03
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *    수정일       수정자              수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/03    jooho       최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
public class PostsSimpleResponseDto implements Serializable {

    /**
     * SerialVersionUID
     */
    private static final long serialVersionUID = 6916914915364711614L;

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
     * 생성 일시
     */
    private LocalDateTime createdDate;

    /**
     * 신규 여부
     */
    private Boolean isNew;

    /**
     * 게시물 응답 DTO 생성자
     *
     * @param boardNo      게시판 번호
     * @param postsNo      게시물 번호
     * @param postsTitle   게시물 제목
     * @param postsContent 게시물 내용
     * @param createdDate  생성 일시
     */
    @QueryProjection
    public PostsSimpleResponseDto(Integer boardNo, Integer postsNo,
                                  String postsTitle, String postsContent, LocalDateTime createdDate) {
        this.boardNo = boardNo;
        this.postsNo = postsNo;
        this.postsTitle = postsTitle;
        // this.postsContent = HtmlUtils.htmlEscape(HtmlUtils.htmlUnescape(postsContent)); // frontend 에서 처리
        this.postsContent = postsContent;
        this.createdDate = createdDate;
    }

    /**
     * 신규 여부 계산
     *
     * @param boardResponseDto 게시판 상세 응답 DTO
     * @return PostsSimpleResponseDto 게시물 응답 DTO
     */
    public PostsSimpleResponseDto setIsNew(BoardResponseDto boardResponseDto) {
        if (boardResponseDto.getNewDisplayDayCount() != null) {
            int compareTo = createdDate.toLocalDate().compareTo(LocalDate.now());
            this.isNew = 0 <= compareTo && compareTo <= boardResponseDto.getNewDisplayDayCount();
        } else {
            this.isNew = false;
        }
        return this;
    }

}
