package org.egovframe.cloud.boardservice.domain.board;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * org.egovframe.cloud.boardservice.domain.board.BoardRepository
 * <p>
 * 게시판 레파지토리 인터페이스
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
public interface BoardRepository extends JpaRepository<Board, Integer>, BoardRepositoryCustom {

}
