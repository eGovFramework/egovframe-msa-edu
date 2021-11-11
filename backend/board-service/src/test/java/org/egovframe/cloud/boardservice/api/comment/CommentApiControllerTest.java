package org.egovframe.cloud.boardservice.api.comment;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.egovframe.cloud.boardservice.api.board.dto.BoardResponseDto;
import org.egovframe.cloud.boardservice.api.comment.dto.CommentResponseDto;
import org.egovframe.cloud.boardservice.domain.board.Board;
import org.egovframe.cloud.boardservice.domain.board.BoardRepository;
import org.egovframe.cloud.boardservice.domain.comment.Comment;
import org.egovframe.cloud.boardservice.domain.comment.CommentId;
import org.egovframe.cloud.boardservice.domain.comment.CommentRepository;
import org.egovframe.cloud.boardservice.domain.posts.Posts;
import org.egovframe.cloud.boardservice.domain.posts.PostsId;
import org.egovframe.cloud.boardservice.domain.posts.PostsRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import lombok.extern.slf4j.Slf4j;

/**
 * org.egovframe.cloud.boardservice.api.posts.CommentApiControllerTest
 * <p>
 * 댓글 Rest API 컨트롤러 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/08/11
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일        수정자       수정내용
 *  ----------    --------    ---------------------------
 *  2021/08/11    jooho       최초 생성
 * </pre>
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles(profiles = "test")
class CommentApiControllerTest {

    /**
     * test rest template
     */
    @Autowired
    TestRestTemplate restTemplate;

    /**
     * 게시판 레파지토리 인터페이스
     */
    @Autowired
    BoardRepository boardRepository;

    /**
     * 게시물 레파지토리 인터페이스
     */
    @Autowired
    PostsRepository postsRepository;

    /**
     * 댓글 레파지토리 인터페이스
     */
    @Autowired
    CommentRepository commentRepository;

    /**
     * 게시판 API 경로
     */
    private static final String URL = "/api/v1/comments";

    /**
     * 테스트 데이터 등록 횟수
     */
    private final Integer GIVEN_DATA_COUNT = 10;

    /**
     * 테스트 데이터
     */
    private Board board;
    private Posts posts;

    private final Integer INSERT_COMMENT_NO = 1;
    private final String INSERT_COMMENT_CONTENT = "댓글 내용";

    private final String UPDATE_COMMENT_CONTENT = "댓글 내용 수정";

    /**
     * 테스트 시작 전 수행
     */
    @BeforeEach
    void setUp() {
        log.info("###setUp");

        // 게시판 등록
        board = boardRepository.save(Board.builder()
                .boardName("일반게시판1")
                .skinTypeCode("normal")
                .titleDisplayLength(50)
                .postDisplayCount(50)
                .pageDisplayCount(50)
                .newDisplayDayCount(50)
                .editorUseAt(true)
                .uploadUseAt(true)
                .uploadLimitCount(50)
                .uploadLimitSize(new BigDecimal("52428800"))
                .userWriteAt(true)
                .commentUseAt(true)
                .build());

        // 게시물 등록
        posts = postsRepository.save(Posts.builder()
                .board(board)
                .postsId(PostsId.builder()
                        .boardNo(board.getBoardNo())
                        .postsNo(1)
                        .build())
                .postsTitle("게시물 1")
                .postsContent("게시물 내용 1")
                .postsAnswerContent("게시물 답변 내용 1")
                .attachmentCode("0000000001")
                .readCount(0)
                .noticeAt(false)
                .deleteAt(0)
                .build());
    }

    /**
     * 테스트 종료 후 수행
     */
    @AfterEach
    void tearDown() {
        log.info("###tearDown");

        // 댓글 삭제
        commentRepository.deleteAll();

        // 게시물 삭제
        postsRepository.deleteAll();

        // 게시판 삭제
        boardRepository.deleteAll();
    }

    /**
     * 게시글의 전체 댓글 목록 조회
     */
    @Test
    void 게시글의_전체_댓글_목록_조회() {
        log.info("###게시글의_전체_댓글_목록_조회");

        // given
        insertComments();

        final String url = URL + "/total/" + posts.getPostsId().getBoardNo() + "/" + posts.getPostsId().getPostsNo();

        // when
        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {
                }
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map<String, Object> data = responseEntity.getBody();

        assertThat(data).isNotNull();
        assertThat(data.get("numberOfElements")).isEqualTo(GIVEN_DATA_COUNT);
        assertThat(data.get("totalElements")).isEqualTo(GIVEN_DATA_COUNT);
        assertThat(data.get("totalPages")).isEqualTo(1);
    }

    /**
     * 게시글의 전체 미삭제 댓글 목록 조회
     */
    @Test
    void 게시글의_전체_미삭제_댓글_목록_조회() {
        log.info("###게시글의_전체_미삭제_댓글_목록_조회");

        // given
        insertComments();

        final String url = URL + "/all/" + posts.getPostsId().getBoardNo() + "/" + posts.getPostsId().getPostsNo();

        // when
        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {
                }
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map<String, Object> data = responseEntity.getBody();

        assertThat(data).isNotNull();
        assertThat(data.get("numberOfElements")).isEqualTo(GIVEN_DATA_COUNT / 3);
        assertThat(data.get("totalElements")).isEqualTo(GIVEN_DATA_COUNT / 3);
        assertThat(data.get("totalPages")).isEqualTo(1);
    }

    /**
     * 게시글의 댓글 목록 조회
     */
    @Test
    void 게시글의_댓글_목록_조회() {
        log.info("###게시글의_댓글_목록_조회");

        // given
        insertComments();

        final Integer page = 0;
        final Integer size = 5;
        final String url = URL + "/" + posts.getPostsId().getBoardNo() + "/" + posts.getPostsId().getPostsNo() + "?page=" + page + "&size=" + size;

        // when
        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {
                }
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map<String, Object> data = responseEntity.getBody();

        assertThat(data).isNotNull();
        assertThat(data.get("numberOfElements")).isEqualTo(size);
        assertThat(data.get("totalElements")).isEqualTo(GIVEN_DATA_COUNT);
        assertThat(data.get("totalPages")).isEqualTo(GIVEN_DATA_COUNT / size);
    }

    /**
     * 게시글의 미삭제 댓글 목록 조회
     */
    @Test
    void 게시글의_미삭제_댓글_목록_조회() {
        log.info("###게시글의_미삭제_댓글_목록_조회");

        // given
        insertComments();

        final Integer page = 0;
        final Integer size = 5;
        final String url = URL + "/list/" + posts.getPostsId().getBoardNo() + "/" + posts.getPostsId().getPostsNo() + "?page=" + page + "&size=" + size;

        // when
        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {
                }
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map<String, Object> data = responseEntity.getBody();

        assertThat(data).isNotNull();
        assertThat(data.get("numberOfElements")).isEqualTo(GIVEN_DATA_COUNT / 3);
        assertThat(data.get("totalElements")).isEqualTo(GIVEN_DATA_COUNT / 3);
        assertThat(data.get("totalPages")).isEqualTo(1);
    }

    /**
     * 댓글 등록
     */
    @Test
    void 댓글_등록() {
        log.info("###댓글_등록");

        // given
        Map<String, Object> params = new HashMap<>();
        params.put("boardNo", posts.getPostsId().getBoardNo());
        params.put("postsNo", posts.getPostsId().getPostsNo());
        params.put("commentContent", INSERT_COMMENT_CONTENT);
        params.put("depthSeq", 0);
        params.put("parentCommentNo", null);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(params);

        // when
        ResponseEntity<CommentResponseDto> responseEntity = restTemplate.exchange(
                URL,
                HttpMethod.POST,
                httpEntity,
                new ParameterizedTypeReference<CommentResponseDto>() {
                }
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        CommentResponseDto dto = responseEntity.getBody();
        assertThat(dto).isNotNull();

        final Integer boardNo = dto.getBoardNo();
        final Integer postsNo = dto.getPostsNo();
        final Integer commentNo = dto.getCommentNo();

        Optional<Comment> comment = selectData(boardNo, postsNo, commentNo);
        assertThat(comment).isPresent();

        Comment entity = comment.get();
        assertThat(entity.getCommentId().getPostsId().getBoardNo()).isEqualTo(boardNo);
        assertThat(entity.getCommentId().getPostsId().getPostsNo()).isEqualTo(postsNo);
        assertThat(entity.getCommentId().getCommentNo()).isEqualTo(commentNo);
        assertThat(entity.getCommentContent()).isEqualTo(INSERT_COMMENT_CONTENT);
        assertThat(entity.getParentCommentNo()).isNull();
        assertThat(entity.getDeleteAt()).isZero();
    }

    /**
     * 댓글 수정 작성자체크
     */
    @Test
    void 댓글_수정_작성자체크() {
        log.info("###댓글_수정 작성자체크");

        // given
        Comment entity = insertComment();

        final Integer boardNo = entity.getCommentId().getPostsId().getBoardNo();
        final Integer postsNo = entity.getCommentId().getPostsId().getPostsNo();
        final Integer commentNo = entity.getCommentId().getCommentNo();

        Map<String, Object> params = new HashMap<>();
        params.put("boardNo", boardNo);
        params.put("postsNo", postsNo);
        params.put("commentNo", commentNo);
        params.put("commentContent", UPDATE_COMMENT_CONTENT);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(params);

        String url = URL + "/update";

        // when
        ResponseEntity<CommentResponseDto> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                httpEntity,
                new ParameterizedTypeReference<CommentResponseDto>() {
                }
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST); // 본인글 아닌 경우 예외 발생
    }

    /**
     * 댓글 삭제 작성자체크
     */
    @Test
    void 댓글_삭제_작성자체크() {
        log.info("###댓글_삭제 작성자체크");

        // given
        Comment entity = insertComment();

        final Integer boardNo = entity.getCommentId().getPostsId().getBoardNo();
        final Integer postsNo = entity.getCommentId().getPostsId().getPostsNo();
        final Integer commentNo = entity.getCommentId().getCommentNo();

        String url = URL + "/delete/" + boardNo + "/" + postsNo + "/" + commentNo;

        // when
        ResponseEntity<BoardResponseDto> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                null,
                BoardResponseDto.class
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST); // 관리자권한 or 본인글 아닌 경우 예외 발생
    }

    /**
     * 댓글 수정
     */
    @Test
    void 댓글_수정() {
        log.info("###댓글_수정");

        // given
        Comment entity = insertComment();

        final Integer boardNo = entity.getCommentId().getPostsId().getBoardNo();
        final Integer postsNo = entity.getCommentId().getPostsId().getPostsNo();
        final Integer commentNo = entity.getCommentId().getCommentNo();

        Map<String, Object> params = new HashMap<>();
        params.put("boardNo", boardNo);
        params.put("postsNo", postsNo);
        params.put("commentNo", commentNo);
        params.put("commentContent", UPDATE_COMMENT_CONTENT);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(params);

        // when
        ResponseEntity<CommentResponseDto> responseEntity = restTemplate.exchange(
                URL,
                HttpMethod.PUT,
                httpEntity,
                new ParameterizedTypeReference<CommentResponseDto>() {
                }
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        CommentResponseDto dto = responseEntity.getBody();
        assertThat(dto).isNotNull();

        Optional<Comment> comment = selectData(boardNo, postsNo, commentNo);
        assertThat(comment).isPresent();

        Comment updatedComment = comment.get();
        assertThat(updatedComment.getCommentId().getPostsId().getBoardNo()).isEqualTo(boardNo);
        assertThat(updatedComment.getCommentId().getPostsId().getPostsNo()).isEqualTo(postsNo);
        assertThat(updatedComment.getCommentId().getCommentNo()).isEqualTo(commentNo);
        assertThat(updatedComment.getCommentContent()).isEqualTo(UPDATE_COMMENT_CONTENT);
    }

    /**
     * 댓글 삭제
     */
    @Test
    void 댓글_삭제() {
        log.info("###댓글_삭제");

        // given
        Comment entity = insertComment();

        final Integer boardNo = entity.getCommentId().getPostsId().getBoardNo();
        final Integer postsNo = entity.getCommentId().getPostsId().getPostsNo();
        final Integer commentNo = entity.getCommentId().getCommentNo();

        String url = URL + "/" + boardNo + "/" + postsNo + "/" + commentNo;

        // when
        ResponseEntity<BoardResponseDto> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                null,
                BoardResponseDto.class
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Optional<Comment> comment = selectData(boardNo, postsNo, commentNo);
        assertThat(comment).isPresent();

        Comment deletedComment = comment.get();
        assertThat(deletedComment.getCommentId().getPostsId().getBoardNo()).isEqualTo(boardNo);
        assertThat(deletedComment.getCommentId().getPostsId().getPostsNo()).isEqualTo(postsNo);
        assertThat(deletedComment.getCommentId().getCommentNo()).isEqualTo(commentNo);
        assertThat(deletedComment.getDeleteAt()).isNotZero();
    }

    /**
     * 테스트 데이터 등록
     */
    private void insertComments() {
        log.info("###테스트 데이터 등록");

        // 댓글 등록
        List<Comment> list = new ArrayList<>();
        for (int i = 1; i <= GIVEN_DATA_COUNT; i++) {
            list.add(Comment.builder()
                    .posts(posts)
                    .commentId(CommentId.builder()
                            .postsId(PostsId.builder()
                                    .boardNo(posts.getPostsId().getBoardNo())
                                    .postsNo(posts.getPostsId().getPostsNo())
                                    .build())
                            .commentNo(i)
                            .build())
                    .commentContent(INSERT_COMMENT_CONTENT + i)
                    .groupNo(i)
                    .depthSeq(0)
                    .sortSeq(i)
                    .deleteAt(i % 3)
                    .build());
        }

        commentRepository.saveAll(list);
    }

    /**
     * 테스트 데이터 단건 등록
     *
     * @return Comment 댓글 엔티티
     */
    private Comment insertComment() {
        log.info("###테스트 데이터 단건 등록");

        return commentRepository.save(Comment.builder()
                .posts(posts)
                .commentId(CommentId.builder()
                        .postsId(PostsId.builder()
                                .boardNo(posts.getPostsId().getBoardNo())
                                .postsNo(posts.getPostsId().getPostsNo())
                                .build())
                        .commentNo(INSERT_COMMENT_NO)
                        .build())
                .commentContent(INSERT_COMMENT_CONTENT)
                .groupNo(INSERT_COMMENT_NO)
                .depthSeq(0)
                .sortSeq(INSERT_COMMENT_NO)
                .deleteAt(0)
                .build());
    }

    /**
     * 테스트 데이터 단건 삭제
     */
    /*private void deleteComment(Comment comment) {
        log.info("###테스트 데이터 단건 삭제");

        commentRepository.deleteById(comment.getCommentId());
    }*/

    /**
     * 테스트 데이터 삭제
     */
    /*private void deleteComments() {
        log.info("###테스트 데이터 삭제");

        commentRepository.deleteAll(comments);

        comments.clear();
    }*/

    /**
     * 테스트 데이터 단건 조회
     *
     * @param boardNo   게시판 번호
     * @param postsNo   게시물 번호
     * @param commentNo 댓글 번호
     * @return Optional<Comment> 댓글 엔티티
     */
    private Optional<Comment> selectData(Integer boardNo, Integer postsNo, Integer commentNo) {
        log.info("###테스트 데이터 단건 조회");

        return commentRepository.findById(CommentId.builder()
                .postsId(PostsId.builder()
                        .boardNo(boardNo)
                        .postsNo(postsNo)
                        .build())
                .commentNo(commentNo)
                .build());
    }

}