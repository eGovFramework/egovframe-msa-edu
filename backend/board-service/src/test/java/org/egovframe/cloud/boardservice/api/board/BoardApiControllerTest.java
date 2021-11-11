package org.egovframe.cloud.boardservice.api.board;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Condition;
import org.egovframe.cloud.boardservice.api.board.dto.BoardListResponseDto;
import org.egovframe.cloud.boardservice.api.board.dto.BoardResponseDto;
import org.egovframe.cloud.boardservice.domain.board.Board;
import org.egovframe.cloud.boardservice.domain.board.BoardRepository;
import org.egovframe.cloud.boardservice.util.RestResponsePage;
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

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * org.egovframe.cloud.boardservice.api.board.BoardApiControllerTest
 * <p>
 * 게시판 Rest API 컨트롤러 테스트 클래스
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
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles(profiles = "test")
public class BoardApiControllerTest {

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
     * 게시판 API 경로
     */
    private static final String URL = "/api/v1/boards";

    /**
     * 테스트 데이터 등록 횟수
     */
    private final Integer GIVEN_DATA_COUNT = 10;

    /**
     * 테스트 데이터
     */
    private final String BOARD_NAME_PREFIX = "게시판 명";
    private final String SKIN_TYPE_CODE_PREFIX = "000";

    private final Integer BOARD_NO = GIVEN_DATA_COUNT + 1;
    private final String INSERT_BOARD_NAME = BOARD_NAME_PREFIX + "_" + BOARD_NO;
    private final String INSERT_SKIN_TYPE_CODE = SKIN_TYPE_CODE_PREFIX + "_" + BOARD_NO;
    private final Integer INSERT_TITLE_DISPLAY_LENGTH = 50;
    private final Integer INSERT_POST_DISPLAY_COUNT = 10;
    private final Integer INSERT_PAGE_DISPLAY_COUNT = 10;
    private final Integer INSERT_NEW_DISPLAY_COUNT = 3;
    private final Boolean INSERT_EDITOR_USE_AT = true;
    private final Boolean INSERT_UPLOAD_USE_AT = true;
    private final Boolean INSERT_USER_WRITE_AT = true;
    private final Boolean INSERT_COMMENT_USE_AT = true;
    private final Integer INSERT_UPLOAD_FILE_COUNT = 5;
    private final BigDecimal INSERT_UPLOAD_LIMIT_SIZE = new BigDecimal("104857600");

    private final String UPDATE_BOARD_NAME = BOARD_NAME_PREFIX + "_" + (BOARD_NO + 1);
    private final String UPDATE_SKIN_TYPE_CODE = SKIN_TYPE_CODE_PREFIX + "_" + (BOARD_NO + 1);
    private final Integer UPDATE_TITLE_DISPLAY_LENGTH = 50 + 1;
    private final Integer UPDATE_POST_DISPLAY_COUNT = 10 + 1;
    private final Integer UPDATE_PAGE_DISPLAY_COUNT = 10 + 1;
    private final Integer UPDATE_NEW_DISPLAY_COUNT = 3 + 1;
    private final Boolean UPDATE_EDITOR_USE_AT = false;
    private final Boolean UPDATE_UPLOAD_USE_AT = false;
    private final Boolean UPDATE_USER_WRITE_AT = false;
    private final Boolean UPDATE_COMMENT_USE_AT = false;
    private final Integer UPDATE_UPLOAD_FILE_COUNT = 5 + 1;
    private final BigDecimal UPDATE_UPLOAD_LIMIT_SIZE = new BigDecimal("209715200");

    /**
     * 테스트 시작 전 수행
     */
    @BeforeEach
    void setUp() {
        log.info("###setUp");
    }

    /**
     * 테스트 종료 후 수행
     */
    @AfterEach
    void tearDown() {
        log.info("###tearDown");

        //게시판 삭제
        boardRepository.deleteAll();
    }

    /**
     * 게시판 페이지 목록 조회 테스트
     */
    @Test
    void 게시판_페이지_목록_조회() {
        log.info("###게시판_페이지_목록_조회");

        // given
        insertBoards();

        String queryString = "?keywordType=boardName&keyword=" + BOARD_NAME_PREFIX; // 검색 조건
        queryString += "&page=0&size=" + GIVEN_DATA_COUNT; // 페이지 정보

        // when
        ResponseEntity<RestResponsePage<BoardListResponseDto>> responseEntity = restTemplate.exchange(
                URL + queryString,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<RestResponsePage<BoardListResponseDto>>() {
                }
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        RestResponsePage<BoardListResponseDto> page = responseEntity.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getNumberOfElements()).isEqualTo(GIVEN_DATA_COUNT);
        assertThat(page.getContent())
                .isNotEmpty()
                .has(new Condition<>(l -> (BOARD_NAME_PREFIX + "_10").equals(l.get(0).getBoardName()), "BoardApiControllerTest.findPage contains " + BOARD_NAME_PREFIX + "_10"))
                .has(new Condition<>(l -> (BOARD_NAME_PREFIX + "_9").equals(l.get(1).getBoardName()), "BoardApiControllerTest.findPage contains " + BOARD_NAME_PREFIX + "_9"));
    }

    /**
     * 게시판 상세 조회 테스트
     */
    @Test
    void 게시판_상세_조회() {
        log.info("###게시판_상세_조회");

        // given
        Board entity = insertBoard();

        final Integer boardNo = entity.getBoardNo();

        String url = URL + "/" + boardNo;

        // when
        ResponseEntity<BoardResponseDto> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<BoardResponseDto>() {
                }
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        BoardResponseDto dto = responseEntity.getBody();
        assertThat(dto).isNotNull();
        assertThat(dto.getBoardNo()).isEqualTo(boardNo);
        assertThat(dto.getBoardName()).isEqualTo(INSERT_BOARD_NAME);
        assertThat(dto.getSkinTypeCode()).isEqualTo(INSERT_SKIN_TYPE_CODE);
        assertThat(dto.getTitleDisplayLength()).isEqualTo(INSERT_TITLE_DISPLAY_LENGTH);
        assertThat(dto.getPostDisplayCount()).isEqualTo(INSERT_POST_DISPLAY_COUNT);
        assertThat(dto.getPageDisplayCount()).isEqualTo(INSERT_PAGE_DISPLAY_COUNT);
        assertThat(dto.getNewDisplayDayCount()).isEqualTo(INSERT_NEW_DISPLAY_COUNT);
        assertThat(dto.getEditorUseAt()).isEqualTo(INSERT_EDITOR_USE_AT);
        assertThat(dto.getUploadUseAt()).isEqualTo(INSERT_UPLOAD_USE_AT);
        assertThat(dto.getUserWriteAt()).isEqualTo(INSERT_USER_WRITE_AT);
        assertThat(dto.getCommentUseAt()).isEqualTo(INSERT_COMMENT_USE_AT);
        assertThat(dto.getUploadLimitCount()).isEqualTo(INSERT_UPLOAD_FILE_COUNT);
        assertThat(dto.getUploadLimitSize()).isEqualTo(INSERT_UPLOAD_LIMIT_SIZE);

        deleteBoard(boardNo);
    }

    /**
     * 게시판 등록 테스트
     */
    @Test
    void 게시판_등록() {
        log.info("###게시판_등록");

        // given
        Map<String, Object> params = new HashMap<>();
        params.put("boardName", INSERT_BOARD_NAME);
        params.put("skinTypeCode", INSERT_SKIN_TYPE_CODE);
        params.put("titleDisplayLength", INSERT_TITLE_DISPLAY_LENGTH);
        params.put("postDisplayCount", INSERT_POST_DISPLAY_COUNT);
        params.put("pageDisplayCount", INSERT_PAGE_DISPLAY_COUNT);
        params.put("newDisplayDayCount", INSERT_NEW_DISPLAY_COUNT);
        params.put("editorUseAt", INSERT_EDITOR_USE_AT);
        params.put("uploadUseAt", INSERT_UPLOAD_USE_AT);
        params.put("userWriteAt", INSERT_USER_WRITE_AT);
        params.put("commentUseAt", INSERT_COMMENT_USE_AT);
        params.put("uploadLimitCount", INSERT_UPLOAD_FILE_COUNT);
        params.put("uploadLimitSize", INSERT_UPLOAD_LIMIT_SIZE);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(params);

        // when
        //ResponseEntity<PostsResponseDto> responseEntity = restTemplate.postForEntity(URL, requestDto, PostsResponseDto.class);
        ResponseEntity<BoardResponseDto> responseEntity = restTemplate.exchange(
                URL,
                HttpMethod.POST,
                httpEntity,
                new ParameterizedTypeReference<BoardResponseDto>() {
                }
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        BoardResponseDto dto = responseEntity.getBody();
        assertThat(dto).isNotNull();

        final Integer boardNo = dto.getBoardNo();

        Optional<Board> board = selectData(boardNo);
        assertThat(board).isPresent();

        Board entity = board.get();
        assertThat(entity.getBoardNo()).isEqualTo(boardNo);
        assertThat(entity.getBoardName()).isEqualTo(INSERT_BOARD_NAME);
        assertThat(entity.getSkinTypeCode()).isEqualTo(INSERT_SKIN_TYPE_CODE);
        assertThat(entity.getTitleDisplayLength()).isEqualTo(INSERT_TITLE_DISPLAY_LENGTH);
        assertThat(entity.getPostDisplayCount()).isEqualTo(INSERT_POST_DISPLAY_COUNT);
        assertThat(entity.getPageDisplayCount()).isEqualTo(INSERT_PAGE_DISPLAY_COUNT);
        assertThat(entity.getNewDisplayDayCount()).isEqualTo(INSERT_NEW_DISPLAY_COUNT);
        assertThat(entity.getEditorUseAt()).isEqualTo(INSERT_EDITOR_USE_AT);
        assertThat(entity.getUploadUseAt()).isEqualTo(INSERT_UPLOAD_USE_AT);
        assertThat(entity.getUserWriteAt()).isEqualTo(INSERT_USER_WRITE_AT);
        assertThat(entity.getCommentUseAt()).isEqualTo(INSERT_COMMENT_USE_AT);
        assertThat(entity.getUploadLimitCount()).isEqualTo(INSERT_UPLOAD_FILE_COUNT);
        assertThat(entity.getUploadLimitSize()).isEqualTo(INSERT_UPLOAD_LIMIT_SIZE);

        deleteBoard(boardNo);
    }

    /**
     * 게시판 수정 테스트
     */
    @Test
    void 게시판_수정() {
        log.info("###게시판_수정");

        // given
        Board entity = insertBoard();

        final Integer boardNo = entity.getBoardNo();

        Map<String, Object> params = new HashMap<>();
        params.put("boardName", UPDATE_BOARD_NAME);
        params.put("skinTypeCode", UPDATE_SKIN_TYPE_CODE);
        params.put("titleDisplayLength", UPDATE_TITLE_DISPLAY_LENGTH);
        params.put("postDisplayCount", UPDATE_POST_DISPLAY_COUNT);
        params.put("pageDisplayCount", UPDATE_PAGE_DISPLAY_COUNT);
        params.put("newDisplayDayCount", UPDATE_NEW_DISPLAY_COUNT);
        params.put("editorUseAt", UPDATE_EDITOR_USE_AT);
        params.put("uploadUseAt", UPDATE_UPLOAD_USE_AT);
        params.put("userWriteAt", UPDATE_USER_WRITE_AT);
        params.put("commentUseAt", UPDATE_COMMENT_USE_AT);
        params.put("uploadLimitCount", UPDATE_UPLOAD_FILE_COUNT);
        params.put("uploadLimitSize", UPDATE_UPLOAD_LIMIT_SIZE);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(params);

        String url = URL + "/" + boardNo;

        // when
        ResponseEntity<BoardResponseDto> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                httpEntity,
                new ParameterizedTypeReference<BoardResponseDto>() {
                }
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        BoardResponseDto dto = responseEntity.getBody();
        assertThat(dto).isNotNull();

        Optional<Board> board = selectData(boardNo);
        assertThat(board).isPresent();

        Board updatedBoard = board.get();
        assertThat(updatedBoard.getBoardNo()).isEqualTo(boardNo);
        assertThat(updatedBoard.getBoardName()).isEqualTo(UPDATE_BOARD_NAME);
        assertThat(updatedBoard.getSkinTypeCode()).isEqualTo(UPDATE_SKIN_TYPE_CODE);
        assertThat(updatedBoard.getTitleDisplayLength()).isEqualTo(UPDATE_TITLE_DISPLAY_LENGTH);
        assertThat(updatedBoard.getPostDisplayCount()).isEqualTo(UPDATE_POST_DISPLAY_COUNT);
        assertThat(updatedBoard.getPageDisplayCount()).isEqualTo(UPDATE_PAGE_DISPLAY_COUNT);
        assertThat(updatedBoard.getNewDisplayDayCount()).isEqualTo(UPDATE_NEW_DISPLAY_COUNT);
        assertThat(updatedBoard.getEditorUseAt()).isEqualTo(UPDATE_EDITOR_USE_AT);
        assertThat(updatedBoard.getUploadUseAt()).isEqualTo(UPDATE_UPLOAD_USE_AT);
        assertThat(updatedBoard.getUserWriteAt()).isEqualTo(UPDATE_USER_WRITE_AT);
        assertThat(updatedBoard.getCommentUseAt()).isEqualTo(UPDATE_COMMENT_USE_AT);
        assertThat(updatedBoard.getUploadLimitCount()).isEqualTo(UPDATE_UPLOAD_FILE_COUNT);
        assertThat(updatedBoard.getUploadLimitSize()).isEqualTo(UPDATE_UPLOAD_LIMIT_SIZE);

        deleteBoard(boardNo);
    }

    /**
     * 게시판 삭제 테스트
     */
    @Test
    void 게시판_삭제() {
        log.info("###게시판_삭제");

        // given
        Board entity = insertBoard();

        final Integer boardNo = entity.getBoardNo();

        String url = URL + "/" + boardNo;

        // when
        ResponseEntity<BoardResponseDto> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                null,
                BoardResponseDto.class
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Optional<Board> board = selectData(boardNo);
        assertThat(board).isNotPresent();
    }

    /**
     * 테스트 데이터 등록
     */
    private void insertBoards() {
        log.info("###테스트 데이터 등록");

        List<Board> list = new ArrayList<>();
        for (int i = 1; i <= GIVEN_DATA_COUNT; i++) {
            list.add(Board.builder()
                    .boardName(BOARD_NAME_PREFIX + "_" + i)
                    .skinTypeCode(SKIN_TYPE_CODE_PREFIX + "_" + i)
                    .titleDisplayLength(INSERT_TITLE_DISPLAY_LENGTH + i)
                    .postDisplayCount(INSERT_POST_DISPLAY_COUNT + i)
                    .pageDisplayCount(INSERT_PAGE_DISPLAY_COUNT + i)
                    .newDisplayDayCount(INSERT_NEW_DISPLAY_COUNT + i)
                    .editorUseAt(i % 2 == 1)
                    .uploadUseAt(i % 3 == 0)
                    .userWriteAt(i % 3 == 0)
                    .commentUseAt(i % 2 == 0)
                    .uploadLimitCount(INSERT_UPLOAD_FILE_COUNT + i)
                    .uploadLimitSize(INSERT_UPLOAD_LIMIT_SIZE.add(new BigDecimal(String.valueOf(i))))
                    .build());
        }

        boardRepository.saveAll(list);
    }

    /**
     * 테스트 데이터 단건 등록
     *
     * @return Board 게시판 엔티티
     */
    private Board insertBoard() {
        log.info("###테스트 데이터 단건 등록");

        return boardRepository.save(Board.builder()
                .boardName(INSERT_BOARD_NAME)
                .skinTypeCode(INSERT_SKIN_TYPE_CODE)
                .titleDisplayLength(INSERT_TITLE_DISPLAY_LENGTH)
                .postDisplayCount(INSERT_POST_DISPLAY_COUNT)
                .pageDisplayCount(INSERT_PAGE_DISPLAY_COUNT)
                .newDisplayDayCount(INSERT_NEW_DISPLAY_COUNT)
                .editorUseAt(INSERT_EDITOR_USE_AT)
                .uploadUseAt(INSERT_UPLOAD_USE_AT)
                .userWriteAt(INSERT_USER_WRITE_AT)
                .commentUseAt(INSERT_COMMENT_USE_AT)
                .uploadLimitCount(INSERT_UPLOAD_FILE_COUNT)
                .uploadLimitSize(INSERT_UPLOAD_LIMIT_SIZE)
                .build());
    }

    /**
     * 테스트 데이터 단건 삭제
     */
    private void deleteBoard(Integer boardNo) {
        log.info("###테스트 데이터 단건 삭제");

        boardRepository.deleteById(boardNo);
    }

    /**
     * 테스트 데이터 단건 조회
     *
     * @param boardNo 게시판 번호
     * @return Optional<Board> 게시판 엔티티
     */
    private Optional<Board> selectData(Integer boardNo) {
        log.info("###테스트 데이터 단건 조회");

        return boardRepository.findById(boardNo);
    }

}
