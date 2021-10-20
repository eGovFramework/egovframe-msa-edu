package org.egovframe.cloud.boardservice.domain.board;

import org.egovframe.cloud.boardservice.api.board.dto.BoardResponseDto;
import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.boardservice.api.board.dto.BoardListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * org.egovframe.cloud.boardservice.domain.board.BoardRepositoryCustom
 * <p>
 * 게시판 Querydsl 인터페이스
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
public interface BoardRepositoryCustom {

    /**
     * 게시판 페이지 목록 조회
     *
     * @param requestDto 게시판 목록 요청 DTO
     * @param pageable   페이지 정보
     * @return Page<BoardListResponseDto> 페이지 게시판 목록 응답 DTO
     */
    Page<BoardListResponseDto> findPage(RequestDto requestDto, Pageable pageable);

    /**
     * 게시판 목록 조회
     *
     * @param boardNos 게시판 번호 목록
     * @return List<BoardResponseDto> 게시판 상세 응답 DTO List
     */
    List<BoardResponseDto> findAllByBoardNoIn(List<Integer> boardNos);

}
