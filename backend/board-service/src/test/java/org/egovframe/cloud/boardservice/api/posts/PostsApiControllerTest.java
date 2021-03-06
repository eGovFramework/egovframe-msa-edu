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
 * ????????? Rest API ???????????? ?????????
 *
 * @author ??????????????????????????? jooho
 * @version 1.0
 * @since 2021/08/10
 *
 * <pre>
 * << ????????????(Modification Information) >>
 *
 *     ?????????        ?????????           ????????????
 *  ----------    --------    ---------------------------
 *  2021/08/10    jooho       ?????? ??????
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
     * ????????? ??????????????? ???????????????
     */
    @Autowired
    BoardRepository boardRepository;

    /**
     * ????????? ??????????????? ???????????????
     */
    @Autowired
    PostsRepository postsRepository;

    /**
     * ????????? API ??????
     */
    private static final String URL = "/api/v1/posts";

    /**
     * ????????? ????????? ?????? ??????
     */
    private final Integer GIVEN_DATA_COUNT = 10;

    /**
     * ????????? ?????????
     */
    private Board board;
    private List<Posts> posts = new ArrayList<>();

    private final Integer INSERT_POSTS_NO = 1;
    private final String INSERT_POSTS_TITLE = "????????? ??????";
    private final String INSERT_POSTS_CONTENT = "????????? ??????";
    private final String INSERT_POSTS_ANSWER_CONTENT = "????????? ?????? ??????";
    private final String INSERT_ATTACHMENT_CODE = "0000000001";
    private final Integer INSERT_READ_COUNT = 0;
    private final Boolean INSERT_NOTICE_AT = true;

    private final String UPDATE_POSTS_TITLE = "????????? ?????? ??????";
    private final String UPDATE_POSTS_CONTENT = "????????? ?????? ??????";
    private final String UPDATE_POSTS_ANSWER_CONTENT = "????????? ?????? ?????? ??????";
    private final String UPDATE_ATTACHMENT_CODE = "0000000002";
    private final Boolean UPDATE_NOTICE_AT = false;

    /**
     * ????????? ?????? ??? ??????
     */
    @BeforeEach
    void setUp() {
        log.info("###setUp");

        // ????????? ??????
        board = boardRepository.save(Board.builder()
                .boardName("???????????????1")
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
     * ????????? ?????? ??? ??????
     */
    @AfterEach
    void tearDown() {
        log.info("###tearDown");

        postsRepository.deleteAll();

        // ????????? ??????
        boardRepository.deleteAll();
    }

    /**
     * ?????? ???????????? ????????? ????????? ?????? ??????
     * @throws JsonProcessingException json exception
     * @throws JsonMappingException json exception
     */
    @Test
    void ??????_????????????_?????????_?????????_??????_??????() throws JsonMappingException, JsonProcessingException {
        log.info("###??????_????????????_?????????_?????????_??????_??????");

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
        // assertThat(boardData.get("posts").size()).isEqualTo(postsCount); // h2?????? rownum??? ???????????? ????????? 3???, 5??? ??????????????? ??????.
    }

    /**
     * ????????? ???????????? ????????? ?????? ??????
     */
    @Test
    void ?????????_????????????_?????????_??????_??????() {
        log.info("###?????????_????????????_?????????_??????_??????");

        // given
        insertPosts(null);

        String url = URL + "/" + board.getBoardNo();
        String queryString = "?keywordType=postsTitle&keyword=" + INSERT_POSTS_TITLE; // ?????? ??????
        queryString += "&page=0&size=" + GIVEN_DATA_COUNT; // ????????? ??????

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
     * ????????? ???????????? ????????? ?????? ??????
     */
    @Test
    void ?????????_????????????_?????????_??????_??????() {
        log.info("###?????????_????????????_?????????_??????_??????");

        // given
        insertPosts(null);

        String url = URL + "/list/" + board.getBoardNo();
        String queryString = "?keywordType=postsTitle&keyword=" + INSERT_POSTS_TITLE; // ?????? ??????
        queryString += "&page=0&size=" + GIVEN_DATA_COUNT; // ????????? ??????

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
     * ????????? ???????????? ?????? ??????
     */
    @Test
    void ?????????_????????????_??????_??????() {
        log.info("###?????????_????????????_??????_??????");

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
     * ????????? ???????????? ?????? ??????
     */
    @Test
    void ?????????_????????????_??????_??????() {
        log.info("###?????????_????????????_??????_??????");

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
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST); // ?????? ????????? ?????? ??????
    }

    /**
     * ????????? ?????? ???????????????
     */
    @Test
    void ?????????_??????_???????????????() {
        log.info("###?????????_??????_???????????????");

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
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST); // ???????????? ????????? ????????? ?????? X
    }

    /**
     * ????????? ?????? ???????????????
     */
    @Test
    void ?????????_??????_???????????????() {
        log.info("###?????????_?????? ???????????????");

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
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST); // ????????? ?????? ?????? ?????? ??????
    }

    /**
     * ????????? ?????? ???????????????
     */
    @Test
    void ?????????_??????_???????????????() {
        log.info("###?????????_?????? ???????????????");

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
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST); // ????????? ?????? ?????? ?????? ??????
    }

    /**
     * ????????? ??????
     */
    @Test
    void ?????????_??????() {
        log.info("###?????????_??????");

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
     * ????????? ??????
     */
    @Test
    void ?????????_??????() {
        log.info("###?????????_??????");

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
     * ????????? ?????? ??????
     */
    @Test
    void ?????????_??????_??????() {
        log.info("###?????????_??????_??????");

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
     * ????????? ?????? ??????
     */
    @Test
    void ?????????_??????_??????() {
        log.info("###?????????_??????_??????");

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
     * ????????? ?????? ?????? ??????
     */
    @Test
    void ?????????_??????_??????_??????() {
        log.info("###?????????_??????_??????_??????");

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
     * ????????? ????????? ??????
     */
    private void insertPosts(Boolean deleteAt) {
        log.info("###????????? ????????? ??????");    

        // ????????? ??????
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
     * ????????? ????????? ?????? ??????
     *
     * @return Posts ????????? ?????????
     */
    private Posts insertPost(Integer deleteAt) {
        log.info("###????????? ????????? ?????? ??????");

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
     * ????????? ????????? ?????? ??????
     */
    /*private void deletePost(Posts post) {
        postsRepository.deleteById(post.getPostsId());
    }*/

    /**
     * ????????? ????????? ??????
     */
    /*private void deletePosts() {
        postsRepository.deleteAll(posts);

        posts.clear();
    }*/

    /**
     * ????????? ????????? ?????? ??????
     *
     * @param boardNo ????????? ??????
     * @param postsNo ????????? ??????
     * @return Optional<Posts> ????????? ?????????
     */
    private Optional<Posts> selectData(Integer boardNo, Integer postsNo) {
        return postsRepository.findById(PostsId.builder().boardNo(boardNo).postsNo(postsNo).build());
    }

}