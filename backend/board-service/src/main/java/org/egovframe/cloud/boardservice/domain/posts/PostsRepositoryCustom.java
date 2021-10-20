package org.egovframe.cloud.boardservice.domain.posts;

import org.egovframe.cloud.boardservice.api.posts.dto.PostsListResponseDto;
import org.egovframe.cloud.boardservice.api.posts.dto.PostsResponseDto;
import org.egovframe.cloud.boardservice.api.posts.dto.PostsSimpleResponseDto;
import org.egovframe.cloud.common.dto.RequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * org.egovframe.cloud.boardservice.domain.posts.PostsRepositoryCustom
 * <p>
 * 게시물 Querydsl 인터페이스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/28
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/28    jooho       최초 생성
 * </pre>
 */
public interface PostsRepositoryCustom {

    /**
     * 게시물 페이지 목록 조회
     *
     * @param boardNo    게시판 번호
     * @param deleteAt   삭제 여부
     * @param requestDto 요청 DTO
     * @param pageable   페이지 정보
     * @return Page<PostsListResponseDto> 페이지 게시물 목록 응답 DTO
     */
    Page<PostsListResponseDto> findPage(Integer boardNo, Integer deleteAt, RequestDto requestDto, Pageable pageable);

    /**
     * 게시판별 최근 게시물 목록 조회
     *
     * @param boardNos   게시판 번호 목록
     * @param postsCount 게시물 수
     * @return List<PostsSimpleResponseDto> 게시물 응답 DTO List
     */
    List<PostsSimpleResponseDto> findAllByBoardNosLimitCount(List<Integer> boardNos, Integer postsCount);

    /**
     * 게시물 상세 조회
     *
     * @param boardNo  게시판 번호
     * @param postsNo  게시물 번호
     * @param userId   사용자 id
     * @param ipAddr   ip 주소
     * @return PostsResponseDto 게시물 상세 응답 DTO
     */
    PostsResponseDto findById(Integer boardNo, Integer postsNo, String userId, String ipAddr);

    /**
     * 근처 게시물 조회
     *
     * @param boardNo    게시판 번호
     * @param postsNo    게시물 번호
     * @param gap        차이 -1: 이전, 1: 이후
     * @param deleteAt   삭제 여부
     * @param requestDto 요청 DTO
     * @return List<PostsSimpleResponseDto> 게시물 상세 응답 DTO List
     */
    List<PostsSimpleResponseDto> findNearPost(Integer boardNo, Integer postsNo, long gap, Integer deleteAt, RequestDto requestDto);

    /**
     * 다음 게시물 번호 조회
     *
     * @param boardNo 게시판 번호
     * @return Integer 다음 게시물 번호
     */
    Integer findNextPostsNo(Integer boardNo);

    /**
     * 게시물 조회 수 증가
     *
     * @param boardNo 게시판 번호
     * @param postsNo 게시물 번호
     * @return Long 처리 건수
     */
    Long updateReadCount(Integer boardNo, Integer postsNo);

    /**
     * 게시물 삭제 여부 수정
     *
     * @param posts    게시물 정보(게시판번호, 게시물번호배열)
     * @param deleteAt 삭제 여부
     * @param userId   사용자 id
     * @return Long 처리 건수
     */
    Long updateDeleteAt(Map<Integer, List<Integer>> posts, Integer deleteAt, String userId);

}
