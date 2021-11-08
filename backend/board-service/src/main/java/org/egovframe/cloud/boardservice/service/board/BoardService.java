package org.egovframe.cloud.boardservice.service.board;

import lombok.RequiredArgsConstructor;
import org.egovframe.cloud.boardservice.api.board.dto.BoardListResponseDto;
import org.egovframe.cloud.boardservice.api.board.dto.BoardResponseDto;
import org.egovframe.cloud.boardservice.api.board.dto.BoardSaveRequestDto;
import org.egovframe.cloud.boardservice.api.board.dto.BoardUpdateRequestDto;
import org.egovframe.cloud.boardservice.domain.board.Board;
import org.egovframe.cloud.boardservice.domain.board.BoardRepository;
import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.common.exception.EntityNotFoundException;
import org.egovframe.cloud.common.service.AbstractService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * org.egovframe.cloud.boardservice.service.board.BoardService
 * <p>
 * 게시판 서비스 클래스
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
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BoardService extends AbstractService {

    /**
     * 게시판 레파지토리 인터페이스
     */
    private final BoardRepository boardRepository;

    /**
     * 조회 조건에 일치하는 게시판 페이지 목록 조회
     *
     * @param requestDto 요청 DTO
     * @param pageable   페이지 정보
     * @return Page<BoardListResponseDto> 페이지 게시판 목록 응답 DTO
     */
    public Page<BoardListResponseDto> findPage(RequestDto requestDto, Pageable pageable) {
        return boardRepository.findPage(requestDto, pageable);
    }

    /**
     * 게시판 목록 조회
     *
     * @param boardNos 게시판 번호 목록
     * @return List<BoardResponseDto> 게시판 상세 응답 DTO List
     */
    public List<BoardResponseDto> findAllByBoardNos(List<Integer> boardNos) {
        return boardRepository.findAllByBoardNoIn(boardNos);
    }

    /**
     * 게시판 단건 조회
     *
     * @param boardNo 게시판 번호
     * @return BoardResponseDto 게시판 응답 DTO
     */
    public BoardResponseDto findById(Integer boardNo) {
        Board entity = findBoard(boardNo);

        return new BoardResponseDto(entity);
    }

    /**
     * 게시판 등록
     *
     * @param requestDto 게시판 등록 요청 DTO
     * @return BoardResponseDto 게시판 응답 DTO
     */
    @Transactional
    public BoardResponseDto save(BoardSaveRequestDto requestDto) {
        Board entity = boardRepository.save(requestDto.toEntity());

        return new BoardResponseDto(entity);
    }

    /**
     * 게시판 수정
     *
     * @param boardNo    게시판 번호
     * @param requestDto 게시판 수정 요청 DTO
     * @return BoardResponseDto 게시판 응답 DTO
     */
    @Transactional
    public BoardResponseDto update(Integer boardNo, BoardUpdateRequestDto requestDto) {
        Board entity = findBoard(boardNo);

        // 수정
        entity.update(requestDto.getBoardName(), requestDto.getSkinTypeCode(), requestDto.getTitleDisplayLength(), requestDto.getPostDisplayCount(),
                requestDto.getPageDisplayCount(), requestDto.getNewDisplayDayCount(), requestDto.getEditorUseAt(), requestDto.getUserWriteAt(),
                requestDto.getCommentUseAt(), requestDto.getUploadUseAt(), requestDto.getUploadLimitCount(), requestDto.getUploadLimitSize());

        return new BoardResponseDto(entity);
    }

    /**
     * 게시판 삭제
     *
     * @param boardNo 게시판 번호
     */
    @Transactional
    public void delete(Integer boardNo) {
        Board entity = findBoard(boardNo);

        // 삭제
        boardRepository.delete(entity);
    }

    /**
     * 게시판 번호로 게시판 엔티티 조회
     *
     * @param boardNo 게시판 번호
     * @return Board 게시판 엔티티
     */
    private Board findBoard(Integer boardNo) throws EntityNotFoundException {
        return boardRepository.findById(boardNo)
                .orElseThrow(() -> new EntityNotFoundException(getMessage("valid.notexists.format", new Object[]{getMessage("board")})));
    }

}
