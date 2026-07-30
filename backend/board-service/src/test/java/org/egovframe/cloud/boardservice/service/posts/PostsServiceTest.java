package org.egovframe.cloud.boardservice.service.posts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.egovframe.cloud.boardservice.api.posts.dto.PostsResponseDto;
import org.egovframe.cloud.boardservice.domain.board.Board;
import org.egovframe.cloud.boardservice.domain.posts.Posts;
import org.egovframe.cloud.boardservice.domain.posts.PostsId;
import org.egovframe.cloud.boardservice.domain.posts.PostsRepository;
import org.egovframe.cloud.boardservice.domain.posts.PostsReadRepository;
import org.egovframe.cloud.boardservice.service.board.BoardService;
import org.egovframe.cloud.common.exception.EntityNotFoundException;
import org.egovframe.cloud.common.exception.InvalidValueException;
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
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * org.egovframe.cloud.boardservice.service.posts.PostsServiceTest
 * <p>
 * 게시물 서비스 단위 테스트 클래스
 *
 * @author 표준프레임워크센터
 * @version 1.0
 * @since 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PostsServiceTest {

    @Mock
    private PostsRepository postsRepository;

    @Mock
    private PostsReadRepository postsReadRepository;

    @Mock
    private BoardService boardService;

    @Mock
    private StreamBridge streamBridge;

    @Mock
    private MessageUtil messageUtil;

    @InjectMocks
    private PostsService postsService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(postsService, "messageUtil", messageUtil);
        given(messageUtil.getMessage(anyString())).willReturn("message");
        given(messageUtil.getMessage(anyString(), any(Object[].class))).willReturn("message");
    }

    @DisplayName("게시판 번호로 게시물을 조회하면 Posts 엔티티를 반환한다")
    @Test
    void should_returnPostsEntity_when_validBoardNoAndPostsNo() {
        // given
        Integer boardNo = 1;
        Integer postsNo = 1;
        PostsId postsId = PostsId.builder().boardNo(boardNo).postsNo(postsNo).build();
        Board board = Board.builder().boardNo(boardNo).boardName("공지사항").skinTypeCode("NORMAL")
                .titleDisplayLength(20).postDisplayCount(10).pageDisplayCount(10)
                .newDisplayDayCount(3).editorUseAt(false).userWriteAt(false)
                .commentUseAt(false).uploadUseAt(false).build();
        Posts posts = Posts.builder()
                .postsId(postsId)
                .board(board)
                .postsTitle("테스트 게시물")
                .postsContent("테스트 내용")
                .noticeAt(false)
                .deleteAt(0)
                .build();
        given(postsRepository.findById(postsId)).willReturn(Optional.of(posts));

        // when
        Posts result = postsService.findPosts(boardNo, postsNo);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPostsId().getBoardNo()).isEqualTo(boardNo);
        assertThat(result.getPostsId().getPostsNo()).isEqualTo(postsNo);
        assertThat(result.getPostsTitle()).isEqualTo("테스트 게시물");
        verify(postsRepository).findById(postsId);
    }

    @DisplayName("존재하지 않는 게시물 번호로 조회하면 EntityNotFoundException이 발생한다")
    @Test
    void should_throwEntityNotFoundException_when_postsNotFound() {
        // given
        Integer boardNo = 1;
        Integer postsNo = 999;
        PostsId postsId = PostsId.builder().boardNo(boardNo).postsNo(postsNo).build();
        given(postsRepository.findById(postsId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postsService.findPosts(boardNo, postsNo))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("게시판 번호가 null이면 게시물 페이지 조회 시 InvalidValueException이 발생한다")
    @Test
    void should_throwInvalidValueException_when_boardNoIsNull() {
        // given
        Integer boardNo = null;

        // when & then
        assertThatThrownBy(() -> postsService.findPage(boardNo, 0, null, null))
                .isInstanceOf(InvalidValueException.class);
    }

    @DisplayName("게시판 번호가 0 이하이면 게시물 페이지 조회 시 InvalidValueException이 발생한다")
    @Test
    void should_throwInvalidValueException_when_boardNoIsZeroOrNegative() {
        // given
        Integer boardNo = 0;

        // when & then
        assertThatThrownBy(() -> postsService.findPage(boardNo, 0, null, null))
                .isInstanceOf(InvalidValueException.class);
    }
}
