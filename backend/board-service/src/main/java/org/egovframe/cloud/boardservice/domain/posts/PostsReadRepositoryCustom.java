package org.egovframe.cloud.boardservice.domain.posts;

/**
 * org.egovframe.cloud.boardservice.domain.posts.PostsReadRepositoryCustom
 * <p>
 * 게시물 조회 Querydsl 인터페이스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/08/02
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/08/02    jooho       최초 생성
 * </pre>
 */
public interface PostsReadRepositoryCustom {

    /**
     * 게시물 조회 데이터 수 조회
     *
     * @param boardNo 게시판 번호
     * @param postsNo 게시물 번호
     * @param userId  사용자 id
     * @param ipAddr  ip 주소
     * @return Long 데이터 수
     */
    Long countByBoardNoAndPostsNoAndUserId(Integer boardNo, Integer postsNo, String userId, String ipAddr);

    /**
     * 다음 게시물 조회 번호 조회
     *
     * @param boardNo 게시판 번호
     * @param postsNo 게시물 번호
     * @return Integer 다음 게시물 조회 번호
     */
    Integer findNextReadNo(Integer boardNo, Integer postsNo);

}
