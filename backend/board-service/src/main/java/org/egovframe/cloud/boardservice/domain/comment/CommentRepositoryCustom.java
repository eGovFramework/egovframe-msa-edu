package org.egovframe.cloud.boardservice.domain.comment;

import org.egovframe.cloud.boardservice.api.comment.dto.CommentListResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * org.egovframe.cloud.boardservice.domain.comment.CommentRepositoryCustom
 * <p>
 * 댓글 Querydsl 인터페이스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/08/04
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/08/04    jooho       최초 생성
 * </pre>
 */
public interface CommentRepositoryCustom {

    /**
     * 댓글 전체 목록 조회
     *
     * @param boardNo  게시판 번호
     * @param postsNo  게시물 번호
     * @param deleteAt 삭제 여부
     * @return List<CommentListResponseDto> 댓글 목록 응답 DTO
     */
    List<CommentListResponseDto> findAll(Integer boardNo, Integer postsNo, Integer deleteAt);

    /**
     * 댓글 목록 조회
     *
     * @param boardNo  게시판 번호
     * @param postsNo  게시물 번호
     * @param deleteAt 삭제 여부
     * @param pageable 페이지 정보
     * @return Map<String, Object> 페이지 댓글 목록 응답 DTO
     */
    Map<String, Object> findPage(Integer boardNo, Integer postsNo, Integer deleteAt, Pageable pageable);

    /**
     * 다음 댓글 번호 조회
     *
     * @param boardNo 게시판 번호
     * @param postsNo 게시물 번호
     * @return Integer 다음 댓글 번호
     */
    Integer findNextCommentNo(Integer boardNo, Integer postsNo);

    /**
     * 다음 정렬 순서 조회
     *
     * @param boardNo 게시판 번호
     * @param postsNo 게시물 번호
     * @return Integer 다음 댓글 번호
     */
    Integer findNextSortSeq(Integer boardNo, Integer postsNo);

    /**
     * 대댓글의 정렬 마지막 순서 조회
     *
     * @param boardNo 게시판 번호
     * @param postsNo 게시물 번호
     * @param groupNo 그룹 번호
     * @return Integer 다음 댓글 번호
     */
    Integer findLastSortSeq(Integer boardNo, Integer postsNo, Integer groupNo);

    /**
     * 대댓글의 정렬 순서 조회
     * 댓글그룹내에 부모 댓글 보다 정렬 순서가 크고 깊이가 크거나 같은 가장 작은 순서
     *
     * @param boardNo         게시판 번호
     * @param postsNo         게시물 번호
     * @param groupNo         그룹 번호
     * @param parentCommentNo 부모 게시물 번호
     * @param depthSeq        깊이 순서
     * @return Integer 다음 댓글 번호
     */
    Integer findNextSortSeq(Integer boardNo, Integer postsNo, Integer groupNo, Integer parentCommentNo, Integer depthSeq);

    /**
     * 댓글 삭제 여부 수정
     *
     * @param boardNo   게시판 번호
     * @param postsNo   게시물 번호
     * @param commentNo 댓글 번호
     * @param deleteAt  삭제 여부
     * @return Integer 처리 건수
     */
    Long updateDeleteAt(Integer boardNo, Integer postsNo, Integer commentNo, Integer deleteAt);

    /**
     * 댓글 정렬 순서 수정
     *
     * @param groupNo         그룹 번호
     * @param startSortSeq    시작 정렬 순서
     * @param endSortSeq      종료 정렬 순서
     * @param increaseSortSeq 증가 정렬 순서
     * @return Long 처리 건수
     */
    Long updateSortSeq(Integer groupNo, Integer startSortSeq, Integer endSortSeq, int increaseSortSeq);

}
