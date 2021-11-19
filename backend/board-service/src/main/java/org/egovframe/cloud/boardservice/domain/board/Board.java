package org.egovframe.cloud.boardservice.domain.board;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egovframe.cloud.boardservice.domain.posts.Posts;
import org.egovframe.cloud.servlet.domain.BaseEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * org.egovframe.cloud.boardservice.domain.board.Board
 * <p>
 * 게시판 엔티티 클래스
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
@Entity
public class Board extends BaseEntity {

    /**
     * 게시판 번호
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "int(9)")
    private Integer boardNo;

    /**
     * 게시판 제목
     */
    @Column(nullable = false, length = 100)
    private String boardName;

    /**
     * 스킨 유형 코드
     */
    @Column(nullable = false, length = 20)
    private String skinTypeCode;

    /**
     * 제목 표시 길이
     */
    @Column(nullable = false, columnDefinition = "mediumint(5) default '20'")
    private Integer titleDisplayLength;

    /**
     * 게시물 표시 수
     */
    @Column(nullable = false, columnDefinition = "mediumint(5) default '10'")
    private Integer postDisplayCount;

    /**
     * 페이지 표시 수
     */
    @Column(nullable = false, columnDefinition = "mediumint(5) default '10'")
    private Integer pageDisplayCount;

    /**
     * 신규 표시 일 수
     */
    @Column(nullable = false, columnDefinition = "mediumint(5) default '3'")
    private Integer newDisplayDayCount;

    /**
     * 에디터 사용 여부
     */
    @Column(nullable = false, columnDefinition = "tinyint(1) default '0'")
    private Boolean editorUseAt;

    /**
     * 사용자 작성 여부
     */
    @Column(nullable = false, columnDefinition = "tinyint(1) default '0'")
    private Boolean userWriteAt;

    /**
     * 댓글 사용 여부
     */
    @Column(nullable = false, columnDefinition = "tinyint(1) default '0'")
    private Boolean commentUseAt;

    /**
     * 업로드 사용 여부
     */
    @Column(nullable = false, columnDefinition = "tinyint(1) default '0'")
    private Boolean uploadUseAt;

    /**
     * 업로드 제한 수
     */
    @Column(columnDefinition = "mediumint(5)")
    private Integer uploadLimitCount;

    /**
     * 업로드 제한 크기
     */
    @Column(columnDefinition = "bigint(20)")
    private BigDecimal uploadLimitSize;

    /**
     * 게시물 엔티티
     */
    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Posts> posts = new ArrayList<>();

    /**
     * 빌더 패턴 클래스 생성자
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
    @Builder
    public Board(Integer boardNo, String boardName, String skinTypeCode, Integer titleDisplayLength,
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
     * 게시판 속성 값 수정
     *
     * @param boardName          게시판 명
     * @param skinTypeCode       스킨 유형 코드
     * @param titleDisplayLength 제목 표시 길이
     * @param postDisplayCount   게시물 표시 수
     * @param pageDisplayCount   페이지 표시 수
     * @param newDisplayDayCount 신규 표시 일 수
     * @param userWriteAt        사용자 작성 여부
     * @param editorUseAt        에디터 사용 여부
     * @param commentUseAt       댓글 사용 여부
     * @param uploadUseAt        업로드 사용 여부
     * @param uploadLimitCount   업로드 제한 수
     * @param uploadLimitSize    업로드 제한 크기
     * @return Board 게시판 엔티티
     */
    public Board update(String boardName, String skinTypeCode, Integer titleDisplayLength, Integer postDisplayCount,
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

        return this;
    }

}
