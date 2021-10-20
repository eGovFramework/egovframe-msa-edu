package org.egovframe.cloud.boardservice.domain.posts;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

/**
 * org.egovframe.cloud.boardservice.domain.posts.PostsReadRepositoryImpl
 * <p>
 * 게시물 조회 Querydsl 구현 클래스
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
@RequiredArgsConstructor
public class PostsReadRepositoryImpl implements PostsReadRepositoryCustom {

    /**
     * DML 생성을위한 Querydsl 팩토리 클래스
     */
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 게시물 조회 데이터 수 조회
     *
     * @param boardNo 게시판 번호
     * @param postsNo 게시물 번호
     * @param userId  사용자 id
     * @param ipAddr  ip 주소
     * @return Long 데이터 수
     */
    public Long countByBoardNoAndPostsNoAndUserId(Integer boardNo, Integer postsNo, String userId, String ipAddr) {
        return jpaQueryFactory
                .selectFrom(QPostsRead.postsRead)
                .where(QPostsRead.postsRead.postsReadId.boardNo.eq(boardNo)
                        .and(QPostsRead.postsRead.postsReadId.postsNo.eq(postsNo))
                        .and(getBooleanExpression("userId", userId))
                        .and(getBooleanExpression("ipAddr", ipAddr)))
                .fetchCount();
    }

    /**
     * 다음 게시물 조회 번호 조회
     *
     * @param boardNo 게시판 번호
     * @param postsNo 게시물 번호
     * @return Integer 다음 게시물 조회 번호
     */
    public Integer findNextReadNo(Integer boardNo, Integer postsNo) {
        return jpaQueryFactory.select(QPostsRead.postsRead.postsReadId.readNo.max().add(1).coalesce(1))
                .from(QPostsRead.postsRead)
                .where(QPostsRead.postsRead.postsReadId.boardNo.eq(boardNo)
                        .and(QPostsRead.postsRead.postsReadId.postsNo.eq(postsNo)))
                .fetchOne();
    }

    /**
     * 엔티티 속성별 동적 검색 표현식 리턴
     *
     * @param attributeName  속성 명
     * @param attributeValue 속성 값
     * @return BooleanExpression 검색 표현식
     */
    private BooleanExpression getBooleanExpression(String attributeName, Object attributeValue) {
        if (attributeValue == null || "".equals(attributeValue.toString())) return null;

        switch (attributeName) {
            case "userId": // 사용자 id
                return QPostsRead.postsRead.userId.eq((String) attributeValue);
            case "ipAddr": // ip 주소
                return QPostsRead.postsRead.ipAddr.eq((String) attributeValue);
            default:
                return null;
        }
    }

}
