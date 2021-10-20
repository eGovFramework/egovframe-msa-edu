package org.egovframe.cloud.boardservice.domain.comment;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.SQLQueryFactory;
import lombok.RequiredArgsConstructor;
import org.egovframe.cloud.boardservice.api.comment.dto.CommentListResponseDto;
import org.egovframe.cloud.boardservice.api.comment.dto.QCommentListResponseDto;
import org.egovframe.cloud.boardservice.domain.user.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.egovframe.cloud.boardservice.domain.comment.QComment.comment;
import static org.egovframe.cloud.boardservice.domain.user.QUser.user;

/**
 * org.egovframe.cloud.boardservice.domain.comment.CommentRepositoryImpl
 * <p>
 * 댓글 Querydsl 구현 클래스
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
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    /**
     * DML 생성을위한 Querydsl 팩토리 클래스
     */
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 쿼리 및 DML 절 생성을 위한 팩토리 클래스
     */
    private final SQLQueryFactory sqlQueryFactory;


    /**
     * 댓글 목록 조회
     *
     * @param boardNo  게시판 번호
     * @param postsNo  게시물 번호
     * @param deleteAt 삭제 여부
     * @return List<CommentListResponseDto> 댓글 목록 응답 DTO
     */
    public List<CommentListResponseDto> findAll(Integer boardNo, Integer postsNo, Integer deleteAt) {
        return jpaQueryFactory
                .select(new QCommentListResponseDto(
                        comment.commentId.postsId.boardNo,
                        comment.commentId.postsId.postsNo,
                        comment.commentId.commentNo,
                        comment.commentContent,
                        comment.groupNo,
                        comment.parentCommentNo,
                        comment.depthSeq,
                        comment.sortSeq,
                        comment.deleteAt,
                        comment.createdBy,
                        QUser.user.userName.as("createdName"),
                        comment.createdDate))
                .from(comment)
                .leftJoin(user).on(comment.createdBy.eq(user.userId))
                .fetchJoin()
                .where(comment.commentId.postsId.boardNo.eq(boardNo)
                        .and(comment.commentId.postsId.postsNo.eq(postsNo))
                        .and(isEqualsDeleteAt(deleteAt)))
                .orderBy(comment.commentId.postsId.boardNo.asc(), comment.commentId.postsId.postsNo.asc(), comment.groupNo.asc(), comment.sortSeq.asc())
                .fetch();
    }

    /**
     * 댓글 목록 조회
     * <p>
     * JPQL 은 from 절에서 서브쿼리를 사용할 수 없어서 SQLQueryFactory 를 사용해 Native SQL 로 조회
     * <p>
     * Native SQL 을 사용하지 않고 서브쿼리를 먼저 조회한 후 JPQL 만을 사용해서 조회 가능
     *
     * @param boardNo  게시판 번호
     * @param postsNo  게시물 번호
     * @param deleteAt 삭제 여부
     * @param pageable 페이지 정보
     * @return Map<String, Object> 페이지 댓글 목록 응답 DTO
     */
    public Map<String, Object> findPage(Integer boardNo, Integer postsNo, Integer deleteAt, Pageable pageable) {
        // 전체 댓글 수, 최상위 댓글 수(페이징 기준) 조회
        String totalElementsKey = "totalElements";
        Tuple countInfo = jpaQueryFactory
                .select(Expressions.asNumber(1).count().as(totalElementsKey),
                        new CaseBuilder()
                                .when(comment.parentCommentNo.isNull())
                                .then(1)
                                .otherwise(0)
                                .sum()
                                .coalesce(0)
                                .as("count"))
                .from(comment)
                .where(comment.commentId.postsId.boardNo.eq(boardNo)
                        .and(comment.commentId.postsId.postsNo.eq(postsNo))
                        .and(isEqualsDeleteAt(deleteAt)))
                .fetchOne();

        Long totalElements = 0L;
        Integer groupElements = 0;
        if (countInfo != null) {
            totalElements = countInfo.get(Expressions.numberPath(Long.class, totalElementsKey)); // 전체 댓글 수
            groupElements = countInfo.get(Expressions.numberPath(Integer.class, "count")); // 최상위 댓글 수
        }

        // path 정의
        Path<Comment> commentPath = Expressions.path(Comment.class, "comment");
        NumberPath<Integer> boardNoPath = Expressions.numberPath(Integer.class, commentPath, "board_no");
        NumberPath<Integer> postsNoPath = Expressions.numberPath(Integer.class, commentPath, "posts_no");
        NumberPath<Integer> commentNoPath = Expressions.numberPath(Integer.class, commentPath, "comment_no");
        NumberPath<Integer> groupNoPath = Expressions.numberPath(Integer.class, commentPath, "group_no");
        NumberPath<Integer> parentCommentNoPath = Expressions.numberPath(Integer.class, commentPath, "parent_comment_no");
        NumberPath<Integer> sortSeqPath = Expressions.numberPath(Integer.class, commentPath, "sort_seq");
        NumberPath<Integer> deleteAtPath = Expressions.numberPath(Integer.class, commentPath, "delete_at");

        StringPath userPath = Expressions.stringPath("user");

        BooleanExpression deleteAtExpression = null;
        if (deleteAt != null) {
            deleteAtExpression = deleteAt == 0 ? deleteAtPath.eq(0) : deleteAtPath.ne(0);
        }

        // 댓글 그룹 조회
        Path<Comment> groupCommentPath = Expressions.path(Comment.class, "groupComment");
        SubQueryExpression<Tuple> groupComment = JPAExpressions.select(boardNoPath,
                        postsNoPath,
                        commentNoPath)
                .from(comment)
                .where(boardNoPath.eq(boardNo)
                        .and(postsNoPath.eq(postsNo))
                        .and(parentCommentNoPath.isNull())
                        .and(deleteAtExpression))
                .orderBy(boardNoPath.asc(), postsNoPath.asc(), commentNoPath.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // 댓글 조회
        List<CommentListResponseDto> comments = sqlQueryFactory
                .select(new QCommentListResponseDto(
                        boardNoPath,
                        postsNoPath,
                        commentNoPath,
                        Expressions.stringPath(commentPath, "comment_content"),
                        groupNoPath,
                        parentCommentNoPath,
                        Expressions.numberPath(Integer.class, commentPath, "depth_seq"),
                        Expressions.numberPath(Integer.class, commentPath, "sort_seq"),
                        Expressions.numberPath(Integer.class, commentPath, "delete_at"),
                        Expressions.stringPath(commentPath, "created_by"),
                        Expressions.stringPath(userPath, "user_name"),
                        Expressions.datePath(LocalDateTime.class, commentPath, "created_date")))
                .from(comment)
                .innerJoin(groupComment, groupCommentPath).on(Expressions.numberPath(Integer.class, groupCommentPath, "board_no").eq(boardNoPath)
                        .and(Expressions.numberPath(Integer.class, groupCommentPath, "posts_no").eq(postsNoPath))
                        .and(Expressions.numberPath(Integer.class, groupCommentPath, "comment_no").eq(groupNoPath)))
                .leftJoin(user).on(Expressions.stringPath(commentPath, "created_by").eq(Expressions.stringPath(userPath, "user_id")))
                .where(boardNoPath.eq(boardNo)
                        .and(postsNoPath.eq(postsNo))
                        .and(deleteAtExpression))
                .orderBy(boardNoPath.asc(), postsNoPath.asc(), groupNoPath.asc(), sortSeqPath.asc())
                .fetch();

        Page<CommentListResponseDto> page = new PageImpl<>(comments, pageable, groupElements == null ? 0 : groupElements);

        // 페이지 인터페이스와 동일한 속성의 맵 리턴
        Map<String, Object> result = new HashMap<>();

        result.put("content", comments);
        result.put("empty", page.isEmpty());
        result.put("first", page.isFirst());
        result.put("last", page.isLast());
        result.put("number", page.getNumber());
        result.put("numberOfElements", comments.size());
        result.put("size", page.getSize());
        result.put(totalElementsKey, totalElements);
        result.put("groupElements", groupElements);
        result.put("totalPages", page.getTotalPages());

        return result;
    }

    /**
     * 다음 댓글 번호 조회
     *
     * @param boardNo 게시판 번호
     * @param postsNo 게시물 번호
     * @return Integer 다음 게시물 번호
     */
    public Integer findNextCommentNo(Integer boardNo, Integer postsNo) {
        return jpaQueryFactory
                .select(comment.commentId.commentNo.max().add(1).coalesce(1))
                .from(comment)
                .where(comment.commentId.postsId.boardNo.eq(boardNo)
                        .and(comment.commentId.postsId.postsNo.eq(postsNo)))
                .fetchOne();
    }

    /**
     * 다음 정렬 순서 조회
     *
     * @param boardNo 게시판 번호
     * @param postsNo 게시물 번호
     * @return Integer 다음 게시물 번호
     */
    public Integer findNextSortSeq(Integer boardNo, Integer postsNo) {
        return jpaQueryFactory
                .select(comment.sortSeq.max().add(1).coalesce(1))
                .from(comment)
                .where(comment.commentId.postsId.boardNo.eq(boardNo)
                        .and(comment.commentId.postsId.postsNo.eq(postsNo))
                        .and(comment.parentCommentNo.isNull()))
                .fetchOne();
    }

    /**
     * 대댓글의 정렬 마지막 순서 조회
     *
     * @param boardNo 게시판 번호
     * @param postsNo 게시물 번호
     * @param groupNo 그룹 번호
     * @return Integer 다음 게시물 번호
     */
    public Integer findLastSortSeq(Integer boardNo, Integer postsNo, Integer groupNo) {
        return jpaQueryFactory
                .select(comment.sortSeq.max().add(1).coalesce(1))
                .from(comment)
                .where(comment.commentId.postsId.boardNo.eq(boardNo)
                        .and(comment.commentId.postsId.postsNo.eq(postsNo))
                        .and(comment.groupNo.eq(groupNo)))
                .fetchOne();
    }

    /**
     * 대댓글의 정렬 순서 조회
     * 댓글그룹내에 부모 댓글 보다 정렬 순서가 크고 깊이가 크거나 같은 가장 작은 순서
     *
     * @param boardNo         게시판 번호
     * @param postsNo         게시물 번호
     * @param groupNo         그룹 번호
     * @param parentCommentNo 부모 게시물 번호
     * @param depthSeq        깊이 순서
     * @return Integer 다음 게시물 번호
     */
    public Integer findNextSortSeq(Integer boardNo, Integer postsNo, Integer groupNo, Integer parentCommentNo, Integer depthSeq) {
        return jpaQueryFactory
                .select(comment.sortSeq.min())
                .from(comment)
                .where(comment.commentId.postsId.boardNo.eq(boardNo)
                        .and(comment.commentId.postsId.postsNo.eq(postsNo))
                        .and(comment.groupNo.eq(groupNo))
                        .and(comment.sortSeq.gt(JPAExpressions.select(comment.sortSeq)
                                .from(comment)
                                .where(comment.commentId.postsId.boardNo.eq(boardNo)
                                        .and(comment.commentId.postsId.postsNo.eq(postsNo))
                                        .and(comment.commentId.commentNo.eq(parentCommentNo)))))
                        .and(comment.depthSeq.lt(depthSeq)))
                .fetchOne();
    }

    /**
     * 댓글 정렬 순서 수정
     *
     * @param groupNo         그룹 번호
     * @param startSortSeq    시작 정렬 순서
     * @param endSortSeq      종료 정렬 순서
     * @param increaseSortSeq 증가 정렬 순서
     * @return Long 수정 건수
     */
    public Long updateSortSeq(Integer groupNo, Integer startSortSeq, Integer endSortSeq, int increaseSortSeq) {
        return jpaQueryFactory.update(comment)
                .set(comment.sortSeq, comment.sortSeq.add(increaseSortSeq))
                .where(isEqualsGroupNo(groupNo),
                        isGoeSortSeq(startSortSeq),
                        isLoeSortSeq(endSortSeq))
                .execute();
    }

    /**
     * 댓글 삭제 여부 수정
     *
     * @param boardNo   게시판 번호
     * @param postsNo   게시물 번호
     * @param commentNo 댓글 번호
     * @param deleteAt  삭제 여부
     * @return Integer 처리 건수
     */
    public Long updateDeleteAt(Integer boardNo, Integer postsNo, Integer commentNo, Integer deleteAt) {
        return jpaQueryFactory.update(comment)
                .set(comment.deleteAt, deleteAt)
                .set(comment.modifiedDate, LocalDateTime.now())
                .where(comment.commentId.postsId.boardNo.eq(boardNo)
                        .and(comment.commentId.postsId.postsNo.eq(postsNo))
                        .and(comment.commentId.commentNo.eq(commentNo)))
                .execute();
    }

    /**
     * 삭제여부 검색 표현식
     *
     * @param deleteAt 삭제 여부
     * @return BooleanExpression 검색 표현식
     */
    private BooleanExpression isEqualsDeleteAt(Integer deleteAt) {
        if (deleteAt == null) return null;

        if (deleteAt == 0) return comment.deleteAt.eq(deleteAt);
        else return comment.deleteAt.ne(0);
    }

    /**
     * 그룹 번호 동일 검색 표현식
     *
     * @param groupNo 그룹 번호
     * @return BooleanExpression 검색 표현식
     */
    private BooleanExpression isEqualsGroupNo(Integer groupNo) {
        return groupNo == null ? null : comment.groupNo.eq(groupNo);
    }

    /**
     * 정렬 순서 이하 검색 표현식
     *
     * @param sortSeq 정렬 순서
     * @return BooleanExpression 검색 표현식
     */
    private BooleanExpression isLoeSortSeq(Integer sortSeq) {
        return sortSeq == null ? null : comment.sortSeq.loe(sortSeq);
    }

    /**
     * 정렬 순서 이상 검색 표현식
     *
     * @param sortSeq 정렬 순서
     * @return BooleanExpression 검색 표현식
     */
    private BooleanExpression isGoeSortSeq(Integer sortSeq) {
        return sortSeq == null ? null : comment.sortSeq.goe(sortSeq);
    }

}
