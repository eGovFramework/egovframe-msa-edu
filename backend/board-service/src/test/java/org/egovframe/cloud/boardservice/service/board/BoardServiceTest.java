package org.egovframe.cloud.boardservice.service.board;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.Optional;

import org.egovframe.cloud.boardservice.api.board.dto.BoardResponseDto;
import org.egovframe.cloud.boardservice.api.board.dto.BoardSaveRequestDto;
import org.egovframe.cloud.boardservice.domain.board.Board;
import org.egovframe.cloud.boardservice.domain.board.BoardRepository;
import org.egovframe.cloud.common.exception.EntityNotFoundException;
import org.egovframe.cloud.common.util.MessageUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * org.egovframe.cloud.boardservice.service.board.BoardServiceTest
 * <p>
 * 게시판 서비스 단위 테스트 클래스
 *
 * @author 표준프레임워크센터
 * @version 1.0
 * @since 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private MessageUtil messageUtil;

    @InjectMocks
    private BoardService boardService;

    @BeforeEach
    void setUp() {
        // @Resource 주입 필드는 @InjectMocks가 처리하지 못하므로 직접 설정
        ReflectionTestUtils.setField(boardService, "messageUtil", messageUtil);
        given(messageUtil.getMessage(anyString())).willReturn("message");
        given(messageUtil.getMessage(anyString(), any(Object[].class))).willReturn("message");
    }

    @DisplayName("존재하는 게시판 번호로 조회하면 응답 DTO를 반환한다")
    @Test
    void should_returnBoardResponseDto_when_boardExists() {
        // given
        Integer boardNo = 1;
        Board board = Board.builder()
                .boardNo(boardNo)
                .boardName("공지사항")
                .skinTypeCode("NORMAL")
                .titleDisplayLength(20)
                .postDisplayCount(10)
                .pageDisplayCount(10)
                .newDisplayDayCount(3)
                .editorUseAt(true)
                .userWriteAt(false)
                .commentUseAt(false)
                .uploadUseAt(false)
                .uploadLimitCount(5)
                .uploadLimitSize(new BigDecimal("104857600"))
                .build();
        given(boardRepository.findById(boardNo)).willReturn(Optional.of(board));

        // when
        BoardResponseDto result = boardService.findById(boardNo);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getBoardNo()).isEqualTo(boardNo);
        assertThat(result.getBoardName()).isEqualTo("공지사항");
        assertThat(result.getSkinTypeCode()).isEqualTo("NORMAL");
        verify(boardRepository).findById(boardNo);
    }

    @DisplayName("존재하지 않는 게시판 번호로 조회하면 EntityNotFoundException이 발생한다")
    @Test
    void should_throwEntityNotFoundException_when_boardNotFound() {
        // given
        Integer boardNo = 999;
        given(boardRepository.findById(boardNo)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> boardService.findById(boardNo))
                .isInstanceOf(EntityNotFoundException.class);
        verify(boardRepository).findById(boardNo);
    }

    @DisplayName("게시판 등록 요청 DTO로 저장하면 응답 DTO를 반환한다")
    @Test
    void should_returnBoardResponseDto_when_saveBoard() {
        // given
        BoardSaveRequestDto requestDto = BoardSaveRequestDto.builder()
                .boardName("자유게시판")
                .skinTypeCode("NORMAL")
                .titleDisplayLength(20)
                .postDisplayCount(10)
                .pageDisplayCount(10)
                .newDisplayDayCount(3)
                .editorUseAt(false)
                .userWriteAt(true)
                .commentUseAt(true)
                .uploadUseAt(false)
                .build();
        Board savedBoard = Board.builder()
                .boardNo(1)
                .boardName("자유게시판")
                .skinTypeCode("NORMAL")
                .titleDisplayLength(20)
                .postDisplayCount(10)
                .pageDisplayCount(10)
                .newDisplayDayCount(3)
                .editorUseAt(false)
                .userWriteAt(true)
                .commentUseAt(true)
                .uploadUseAt(false)
                .build();
        given(boardRepository.save(any(Board.class))).willReturn(savedBoard);

        // when
        BoardResponseDto result = boardService.save(requestDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getBoardName()).isEqualTo("자유게시판");
        assertThat(result.getUserWriteAt()).isTrue();
        verify(boardRepository).save(any(Board.class));
    }

    @DisplayName("존재하지 않는 게시판 번호로 삭제하면 EntityNotFoundException이 발생한다")
    @Test
    void should_throwEntityNotFoundException_when_deleteNonExistentBoard() {
        // given
        Integer boardNo = 999;
        given(boardRepository.findById(boardNo)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> boardService.delete(boardNo))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
