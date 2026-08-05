package org.egovframe.cloud.boardservice.service.posts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.egovframe.cloud.boardservice.api.board.dto.BoardResponseDto;
import org.egovframe.cloud.boardservice.domain.posts.PostsRepository;
import org.egovframe.cloud.boardservice.service.board.BoardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * org.egovframe.cloud.boardservice.service.posts.PostsServiceTest
 * <p>
 * PostsService.findNewest 가 요청한 게시판 순서를 보존하는지 검증하는 단위 테스트
 *
 * @author 표준프레임워크센터
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class PostsServiceTest {

    @Mock
    private PostsRepository postsRepository;

    @Mock
    private BoardService boardService;

    @InjectMocks
    private PostsService postsService;

    /**
     * boardNo 만 세팅한 게시판 응답 DTO 생성
     */
    private BoardResponseDto board(Integer boardNo) {
        return new BoardResponseDto(boardNo, "board-" + boardNo, null, null,
                null, null, null, null, null, null, null, null, null);
    }

    @Test
    @DisplayName("findNewest 결과 Map 의 키 순서가 요청한 게시판 순서대로 보존된다")
    void findNewest_preservesRequestedBoardOrder() {
        // given : 오름차순이 아닌 순서로 게시판 번호를 요청한다.
        List<Integer> boardNos = Arrays.asList(3, 1, 2);
        List<BoardResponseDto> boards = new ArrayList<>(Arrays.asList(board(3), board(1), board(2)));

        when(boardService.findAllByBoardNos(boardNos)).thenReturn(boards);
        when(postsRepository.findAllByBoardNosLimitCount(any(), any())).thenReturn(Collections.emptyList());

        // when
        Map<Integer, BoardResponseDto> result = postsService.findNewest(boardNos, 5);

        // then : HashMap 이면 키 순서가 1,2,3 으로 정렬되어 단정이 실패한다. 요청 순서 3,1,2 가 보존되어야 한다.
        assertThat(result.keySet()).containsExactly(3, 1, 2);
    }
}
