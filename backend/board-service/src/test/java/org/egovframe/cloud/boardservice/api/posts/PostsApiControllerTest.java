package org.egovframe.cloud.boardservice.api.posts;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.assertj.core.api.Condition;
import org.egovframe.cloud.boardservice.api.posts.dto.PostsListResponseDto;
import org.egovframe.cloud.boardservice.api.posts.dto.PostsResponseDto;
import org.egovframe.cloud.boardservice.domain.board.Board;
import org.egovframe.cloud.boardservice.domain.board.BoardRepository;
import org.egovframe.cloud.boardservice.domain.posts.Posts;
import org.egovframe.cloud.boardservice.domain.posts.PostsId;
import org.egovframe.cloud.boardservice.domain.posts.PostsRepository;
import org.egovframe.cloud.boardservice.service.posts.PostsService;
import org.egovframe.cloud.boardservice.util.RestResponsePage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * org.egovframe.cloud.boardservice.api.posts.PostsApiControllerTest
 * <p>
 * 게시물 Rest API 컨트롤러 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/08/10
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/08/10    jooho       최초 생성
 * </pre>
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles(profiles = "test")
class PostsApiControllerTest {

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
     * 게시물 API 경로
     */
    private static final String URL = "/api/v1/posts";

    /**
     * 테스트 데이터 등록 횟수
     */
    private final Integer GIVEN_DATA_COUNT = 10;

    /**
     * 테스트 데이터
     */
    private Board board;
    private List<Posts> posts = new ArrayList<>();

    private final Integer INSERT_POSTS_NO = 1;
    private final String INSERT_POSTS_TITLE = "게시물 제목";
    private final String INSERT_POSTS_CONTENT = "게시물 내용";
    private final String INSERT_POSTS_ANSWER_CONTENT = "게시물 답변 내용";
    private final String INSERT_ATTACHMENT_CODE = "0000000001";
    private final Integer INSERT_READ_COUNT = 0;
    private final Boolean INSERT_NOTICE_AT = true;

    private final String UPDATE_POSTS_TITLE = "게시물 제목 수정";
    private final String UPDATE_POSTS_CONTENT = "게시물 내용 수정";
    private final String UPDATE_POSTS_ANSWER_CONTENT = "게시물 답변 내용 수정";
    private final String UPDATE_ATTACHMENT_CODE = "0000000002";
    private final Boolean UPDATE_NOTICE_AT = false;

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
                .userWriteAt(false)
                .commentUseAt(true)
                .build());
    }

    /**
     * 테스트 종료 후 수행
     */
    @AfterEach
    void tearDown() {
        log.info("###tearDown");

        postsRepository.deleteAll();

        // 게시판 삭제
        boardRepository.deleteAll();
    }

    /**
     * 최근 게시물이 포함된 게시판 목록 조회
     * @throws JsonProcessingException json exception
     * @throws JsonMappingException json exception
     */
    @Test
    void 최근_게시물이_포함된_게시판_목록_조회() throws JsonMappingException, JsonProcessingException {
        log.info("###최근_게시물이_포함된_게시판_목록_조회");

        // given
        insertPosts(null);

        final Integer postsCount = 3;
        final String url = URL + "/newest/" + board.getBoardNo() + "/" + postsCount;

        // when
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<String>() {
                }
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode data = mapper.readTree(responseEntity.getBody());
        JsonNode boardData = data.get(board.getBoardNo().toString());

        assertThat(data).isNotNull();
        assertThat(boardData.get("boardNo").asInt()).isEqualTo(board.getBoardNo());
        assertThat(boardData.get("posts").isArray()).isTrue();
        // assertThat(boardData.get("posts").size()).isEqualTo(postsCount); // h2에서 rownum을 계산하지 못해서 3건, 5건 조회될때가 있음.
    }

    /**
     * 게시물 삭제포함 페이지 목록 조회
     */
    @Test
    void 게시물_삭제포함_페이지_목록_조회() {
        log.info("###게시물_삭제포함_페이지_목록_조회");

        // given
        insertPosts(null);

        String url = URL + "/" + board.getBoardNo();
        String queryString = "?keywordType=postsTitle&keyword=" + INSERT_POSTS_TITLE; // 검색 조건
        queryString += "&page=0&size=" + GIVEN_DATA_COUNT; // 페이지 정보

        // when
        ResponseEntity<RestResponsePage<PostsListResponseDto>> responseEntity = restTemplate.exchange(
                url + queryString,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<RestResponsePage<PostsListResponseDto>>() {
                }
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        RestResponsePage<PostsListResponseDto> page = responseEntity.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getNumberOfElements()).isEqualTo(GIVEN_DATA_COUNT);
        assertThat(page.getContent())
                .isNotEmpty()
                .has(new Condition<>(l -> (INSERT_POSTS_TITLE + "9").equals(l.get(0).getPostsTitle()) && l.get(0).getNoticeAt(), "PostsApiControllerTest.findPage contains [notice] " + INSERT_POSTS_TITLE + "9"))
                .has(new Condition<>(l -> (INSERT_POSTS_TITLE + "6").equals(l.get(1).getPostsTitle()) && l.get(0).getNoticeAt(), "PostsApiControllerTest.findPage contains [notice] " + INSERT_POSTS_TITLE + "6"));
    }

    /**
     * 게시물 삭제제외 페이지 목록 조회
     */
    @Test
    void 게시물_삭제제외_페이지_목록_조회() {
        log.info("###게시물_삭제제외_페이지_목록_조회");

        // given
        insertPosts(null);

        String url = URL + "/list/" + board.getBoardNo();
        String queryString = "?keywordType=postsTitle&keyword=" + INSERT_POSTS_TITLE; // 검색 조건
        queryString += "&page=0&size=" + GIVEN_DATA_COUNT; // 페이지 정보

        // when
        ResponseEntity<RestResponsePage<PostsListResponseDto>> responseEntity = restTemplate.exchange(
                url + queryString,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<RestResponsePage<PostsListResponseDto>>() {
                }
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        RestResponsePage<PostsListResponseDto> page = responseEntity.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getNumberOfElements()).isEqualTo(GIVEN_DATA_COUNT / 2);
        assertThat(page.getContent())
                .isNotEmpty()
                .has(new Condition<>(l -> (INSERT_POSTS_TITLE + "6").equals(l.get(0).getPostsTitle()) && l.get(0).getNoticeAt(), "PostsApiControllerTest.findPage contains [notice] " + INSERT_POSTS_TITLE + "6"))
                .has(new Condition<>(l -> (INSERT_POSTS_TITLE + "10").equals(l.get(1).getPostsTitle()) && l.get(0).getNoticeAt(), "PostsApiControllerTest.findPage contains [notice] " + INSERT_POSTS_TITLE + "10"));
    }

    /**
     * 게시물 삭제포함 단건 조회
     */
    @Test
    void 게시물_삭제포함_단건_조회() {
        log.info("###게시물_삭제포함_단건_조회");

        // given
        final Posts post = insertPost(1);

        String url = URL + "/" + post.getPostsId().getBoardNo() + "/" + post.getPostsId().getPostsNo();

        // when
        ResponseEntity<PostsResponseDto> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PostsResponseDto>() {
                }
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        PostsResponseDto dto = responseEntity.getBody();
        assertThat(dto).isNotNull();
        assertThat(dto.getBoardNo()).isEqualTo(post.getPostsId().getBoardNo());
        assertThat(dto.getPostsNo()).isEqualTo(post.getPostsId().getPostsNo());
        assertThat(dto.getPostsTitle()).isEqualTo(post.getPostsTitle());
        assertThat(dto.getPostsContent()).isEqualTo(post.getPostsContent());
        assertThat(dto.getPostsAnswerContent()).isEqualTo(post.getPostsAnswerContent());
        assertThat(dto.getAttachmentCode()).isEqualTo(post.getAttachmentCode());
        assertThat(dto.getReadCount()).isEqualTo(post.getReadCount() + 1);
        assertThat(dto.getNoticeAt()).isEqualTo(post.getNoticeAt());
        assertThat(dto.getDeleteAt()).isEqualTo(post.getDeleteAt());
    }

    /**
     * 게시물 삭제제외 단건 조회
     */
    @Test
    void 게시물_삭제제외_단건_조회() {
        log.info("###게시물_삭제제외_단건_조회");

        // given
        final Posts post = insertPost(1);

        String url = URL + "/view/" + post.getPostsId().getBoardNo() + "/" + post.getPostsId().getPostsNo();

        // when
        ResponseEntity<PostsResponseDto> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PostsResponseDto>() {
                }
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST); // 삭제 게시물 조회 불가
    }

    /**
     * 게시물 등록 작성자체크
     */
    @Test
    void 게시물_등록_작성자체크() {
        log.info("###게시물_등록_작성자체크");

        // given
        String url = URL + "/save/" + board.getBoardNo();

        Map<String, Object> params = new HashMap<>();
        params.put("postsTitle", INSERT_POSTS_TITLE);
        params.put("postsContent", INSERT_POSTS_CONTENT);
        params.put("postsAnswerContent", INSERT_POSTS_ANSWER_CONTENT);
        params.put("attachmentCode", INSERT_ATTACHMENT_CODE);
        params.put("noticeAt", INSERT_NOTICE_AT);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(params);

        // when
        ResponseEntity<PostsResponseDto> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                httpEntity,
                new ParameterizedTypeReference<PostsResponseDto>() {
                }
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST); // 게시판에 사용자 글쓰기 허용 X
    }

    /**
     * 게시물 수정 작성자체크
     */
    @Test
    void 게시물_수정_작성자체크() {
        log.info("###게시물_수정 작성자체크");

        // given
        Posts entity = insertPost(0);

        final Integer boardNo = entity.getPostsId().getBoardNo();
        final Integer postsNo = entity.getPostsId().getPostsNo();

        Map<String, Object> params = new HashMap<>();
        params.put("postsTitle", UPDATE_POSTS_TITLE);
        params.put("postsContent", UPDATE_POSTS_CONTENT);
        params.put("postsAnswerContent", UPDATE_POSTS_ANSWER_CONTENT);
        params.put("attachmentCode", UPDATE_ATTACHMENT_CODE);
        params.put("noticeAt", UPDATE_NOTICE_AT);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(params);

        String url = URL + "/update/" + boardNo + "/" + postsNo;

        // when
        ResponseEntity<PostsResponseDto> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                httpEntity,
                new ParameterizedTypeReference<PostsResponseDto>() {
                }
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST); // 본인글 아닌 경우 예외 발생
    }

    /**
     * 게시물 삭제 작성자체크
     */
    @Test
    void 게시물_삭제_작성자체크() {
        log.info("###게시물_삭제 작성자체크");

        // given
        Posts entity = insertPost(0);

        final Integer boardNo = entity.getPostsId().getBoardNo();
        final Integer postsNo = entity.getPostsId().getPostsNo();

        String url = URL + "/remove/" + boardNo + "/" + postsNo;

        // when
        ResponseEntity<Void> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<Void>() {
                }
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST); // 본인글 아닌 경우 예외 발생
    }

    /**
     * 게시물 등록
     */
    @Test
    void 게시물_등록() {
        log.info("###게시물_등록");

        // given
        String url = URL + "/" + board.getBoardNo();

        Map<String, Object> params = new HashMap<>();
        params.put("postsTitle", INSERT_POSTS_TITLE);
        params.put("postsContent", INSERT_POSTS_CONTENT);
        params.put("postsAnswerContent", INSERT_POSTS_ANSWER_CONTENT);
        params.put("noticeAt", INSERT_NOTICE_AT);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(params);

        // when
        ResponseEntity<PostsResponseDto> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                httpEntity,
                new ParameterizedTypeReference<PostsResponseDto>() {
                }
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        PostsResponseDto dto = responseEntity.getBody();
        assertThat(dto).isNotNull();

        final Integer boardNo = dto.getBoardNo();
        final Integer postsNo = dto.getPostsNo();

        Optional<Posts> posts = selectData(boardNo, postsNo);
        assertThat(posts).isPresent();

        Posts entity = posts.get();
        assertThat(entity.getPostsId().getBoardNo()).isEqualTo(boardNo);
        assertThat(entity.getPostsId().getPostsNo()).isEqualTo(postsNo);
        assertThat(entity.getPostsTitle()).isEqualTo(INSERT_POSTS_TITLE);
        assertThat(entity.getPostsContent()).isEqualTo(INSERT_POSTS_CONTENT);
        assertThat(entity.getPostsAnswerContent()).isEqualTo(INSERT_POSTS_ANSWER_CONTENT);
        assertThat(entity.getReadCount()).isZero();
        assertThat(entity.getNoticeAt()).isEqualTo(INSERT_NOTICE_AT);
        assertThat(entity.getDeleteAt()).isZero();
    }

    /**
     * 게시물 수정
     */
    @Test
    void 게시물_수정() {
        log.info("###게시물_수정");

        // given
        Posts entity = insertPost(0);

        final Integer boardNo = entity.getPostsId().getBoardNo();
        final Integer postsNo = entity.getPostsId().getPostsNo();

        Map<String, Object> params = new HashMap<>();
        params.put("postsTitle", UPDATE_POSTS_TITLE);
        params.put("postsContent", UPDATE_POSTS_CONTENT);
        params.put("postsAnswerContent", UPDATE_POSTS_ANSWER_CONTENT);
        params.put("attachmentCode", UPDATE_ATTACHMENT_CODE);
        params.put("noticeAt", UPDATE_NOTICE_AT);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(params);

        String url = URL + "/" + boardNo + "/" + postsNo;

        // when
        ResponseEntity<PostsResponseDto> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                httpEntity,
                new ParameterizedTypeReference<PostsResponseDto>() {
                }
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        PostsResponseDto dto = responseEntity.getBody();
        assertThat(dto).isNotNull();

        Optional<Posts> posts = selectData(boardNo, postsNo);
        assertThat(posts).isPresent();

        Posts updatedPosts = posts.get();
        assertThat(updatedPosts.getPostsId().getBoardNo()).isEqualTo(boardNo);
        assertThat(updatedPosts.getPostsId().getPostsNo()).isEqualTo(postsNo);
        assertThat(updatedPosts.getPostsTitle()).isEqualTo(UPDATE_POSTS_TITLE);
        assertThat(updatedPosts.getPostsContent()).isEqualTo(UPDATE_POSTS_CONTENT);
        assertThat(updatedPosts.getPostsAnswerContent()).isEqualTo(UPDATE_POSTS_ANSWER_CONTENT);
        assertThat(updatedPosts.getAttachmentCode()).isEqualTo(UPDATE_ATTACHMENT_CODE);
        assertThat(updatedPosts.getNoticeAt()).isEqualTo(UPDATE_NOTICE_AT);
    }

    /**
     * 게시물 다건 삭제
     */
    @Test
    void 게시물_다건_삭제() {
        log.info("###게시물_다건_삭제");

        // given
        insertPosts(false);

        List<Map<String, Object>> params = new ArrayList<>();
        for (Posts post : posts) {
            Map<String, Object> param = new HashMap<>();
            param.put("boardNo", post.getPostsId().getBoardNo());
            param.put("postsNo", post.getPostsId().getPostsNo());

            params.add(param);
        }

        HttpEntity<List<Map<String, Object>>> httpEntity = new HttpEntity<>(params);

        String url = URL + "/remove";

        // when
        ResponseEntity<Long> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                httpEntity,
                Long.class
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(posts.size());

        List<Posts> list = postsRepository.findAll();

        assertThat(list).isNotNull();
        assertThat(list.size()).isEqualTo(posts.size());

        for (Posts post : list) {
            assertThat(post).isNotNull();
            assertThat(post.getDeleteAt()).isNotZero();
        }
    }

    /**
     * 게시물 다건 복원
     */
    @Test
    void 게시물_다건_복원() {
        log.info("###게시물_다건_복원");

        // given
        insertPosts(true);

        List<Map<String, Object>> params = new ArrayList<>();
        for (Posts post : posts) {
            Map<String, Object> param = new HashMap<>();
            param.put("boardNo", post.getPostsId().getBoardNo());
            param.put("postsNo", post.getPostsId().getPostsNo());

            params.add(param);
        }

        HttpEntity<List<Map<String, Object>>> httpEntity = new HttpEntity<>(params);

        String url = URL + "/restore";

        // when
        ResponseEntity<Long> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                httpEntity,
                Long.class
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(posts.size());

        List<Posts> list = postsRepository.findAll();

        assertThat(list).isNotNull();
        assertThat(list.size()).isEqualTo(posts.size());

        for (Posts post : list) {
            assertThat(post).isNotNull();
            assertThat(post.getDeleteAt()).isZero();
        }
    }

    /**
     * 게시물 다건 완전 삭제
     */
    @Test
    void 게시물_다건_완전_삭제() {
        log.info("###게시물_다건_완전_삭제");

        // given
        insertPosts(null);

        List<Map<String, Object>> params = new ArrayList<>();
        for (Posts post : posts) {
            Map<String, Object> param = new HashMap<>();
            param.put("boardNo", post.getPostsId().getBoardNo());
            param.put("postsNo", post.getPostsId().getPostsNo());

            params.add(param);
        }

        HttpEntity<List<Map<String, Object>>> httpEntity = new HttpEntity<>(params);

        String url = URL + "/delete";

        // when
        ResponseEntity<Long> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                httpEntity,
                Long.class
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        List<Posts> list = postsRepository.findAll();

        assertThat(list).isNotNull();
        assertThat(list.size()).isZero();
    }

    /**
     * 테스트 데이터 등록
     */
    private void insertPosts(Boolean deleteAt) {
        log.info("###테스트 데이터 등록");    

        // 게시물 등록
        List<Posts> list = new ArrayList<>();
        for (int i = 1; i <= GIVEN_DATA_COUNT; i++) {
            list.add(Posts.builder()
                    .board(board)
                    .postsId(PostsId.builder()
                            .boardNo(board.getBoardNo())
                            .postsNo(i)
                            .build())
                    .postsTitle(INSERT_POSTS_TITLE + i)
                    .postsContent(INSERT_POSTS_CONTENT + i)
                    .postsAnswerContent(INSERT_POSTS_ANSWER_CONTENT + i)
                    .attachmentCode(StringUtils.leftPad(String.valueOf(i), 10, '0'))
                    .readCount(0)
                    .noticeAt(i % 3 == 0)
                    .deleteAt(i % 2)
                    .build());
        }

        posts = postsRepository.saveAll(list);
    }

    /**
     * 테스트 데이터 단건 등록
     *
     * @return Posts 게시물 엔티티
     */
    private Posts insertPost(Integer deleteAt) {
        log.info("###테스트 데이터 단건 등록");

        return postsRepository.save(Posts.builder()
                .board(board)
                .postsId(PostsId.builder()
                        .boardNo(board.getBoardNo())
                        .postsNo(INSERT_POSTS_NO)
                        .build())
                .postsTitle(INSERT_POSTS_TITLE + 1)
                .postsContent(INSERT_POSTS_CONTENT + 1)
                .postsAnswerContent(INSERT_POSTS_ANSWER_CONTENT + 1)
                .attachmentCode(INSERT_ATTACHMENT_CODE)
                .readCount(INSERT_READ_COUNT)
                .noticeAt(INSERT_NOTICE_AT)
                .deleteAt(deleteAt)
                .build());
    }

    /**
     * 테스트 데이터 단건 삭제
     */
    /*private void deletePost(Posts post) {
        postsRepository.deleteById(post.getPostsId());
    }*/

    /**
     * 테스트 데이터 삭제
     */
    /*private void deletePosts() {
        postsRepository.deleteAll(posts);

        posts.clear();
    }*/

    /**
     * 테스트 데이터 단건 조회
     *
     * @param boardNo 게시판 번호
     * @param postsNo 게시물 번호
     * @return Optional<Posts> 게시물 엔티티
     */
    private Optional<Posts> selectData(Integer boardNo, Integer postsNo) {
        return postsRepository.findById(PostsId.builder().boardNo(boardNo).postsNo(postsNo).build());
    }

}