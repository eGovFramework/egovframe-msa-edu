package org.egovframe.cloud.portalservice.domain.board;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egovframe.cloud.servlet.domain.BaseEntity;

import javax.persistence.*;

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


}
