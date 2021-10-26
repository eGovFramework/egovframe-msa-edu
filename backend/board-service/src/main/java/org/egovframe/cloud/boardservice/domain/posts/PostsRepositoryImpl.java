package org.egovframe.cloud.boardservice.domain.posts;

import com.google.common.base.CaseFormat;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import lombok.RequiredArgsConstructor;
import org.egovframe.cloud.boardservice.api.board.dto.QBoardResponseDto;
import org.egovframe.cloud.boardservice.api.posts.dto.*;
import org.egovframe.cloud.boardservice.domain.board.QBoard;
import org.egovframe.cloud.boardservice.domain.comment.QComment;
import org.egovframe.cloud.boardservice.domain.user.QUser;
import org.egovframe.cloud.common.dto.RequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * org.egovframe.cloud.boardservice.domain.posts.PostsRepositoryImpl
 * <p>
 * 게시물 Querydsl 구현 클래스
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
@RequiredArgsConstructor
public class PostsRepositoryImpl implements PostsRepositoryCustom {

    /**
     * DML 생성을위한 Querydsl 팩토리 클래스
     */
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 쿼리 및 DML 절 생성을 위한 팩토리 클래스
     */
    private final SQLQueryFactory sqlQueryFactory;

    /**
     * 게시물 페이지 목록 조회
     * 가급적 Entity 보다는 Dto를 리턴 - Entity 조회시 hibernate 캐시, 불필요 컬럼 조회, oneToOne N+1 문제 발생
     *
     * @param boardNo    게시판 번호
     * @param deleteAt   삭제 여부
     * @param requestDto 요청 DTO
     * @param pageable   페이지 정보
     * @return Page<PostsListResponseDto> 페이지 게시물 목록 응답 DTO
     */
    @Override
	public Page<PostsListResponseDto> findPage(Integer boardNo, Integer deleteAt, RequestDto requestDto, Pageable pageable) {
        JPQLQuery<PostsListResponseDto> query = jpaQueryFactory
                .select(new QPostsListResponseDto(
                        QPosts.posts.postsId.boardNo,
                        QPosts.posts.postsId.postsNo,
                        QPosts.posts.postsTitle,
                        new CaseBuilder()
                                .when(QBoard.board.skinTypeCode.in("faq", "qna"))
                                .then(QPosts.posts.postsContent)
                                .otherwise(""),
                        new CaseBuilder()
                                .when(QBoard.board.skinTypeCode.in("faq", "qna"))
                                .then(QPosts.posts.postsAnswerContent)
                                .otherwise(""),
                        QPosts.posts.readCount,
                        QPosts.posts.noticeAt,
                        QPosts.posts.deleteAt,
                        QPosts.posts.createdBy,
                        QUser.user.userName.as("createdName"),
                        QPosts.posts.createdDate,
                        QBoard.board.newDisplayDayCount,
                        getCommentCountExpression(deleteAt)))
                .from(QPosts.posts)
                .innerJoin(QBoard.board).on(QPosts.posts.postsId.boardNo.eq(QBoard.board.boardNo))
                .fetchJoin()
                .leftJoin(QUser.user).on(QPosts.posts.createdBy.eq(QUser.user.userId))
                .fetchJoin()
                .where(QPosts.posts.postsId.boardNo.eq(boardNo)
                        .and(getBooleanExpression(requestDto.getKeywordType(), requestDto.getKeyword()))
                        .and(getBooleanExpression("deleteAt", deleteAt)));

        //정렬
        pageable.getSort().stream().forEach(sort -> {
            Order order = sort.isAscending() ? Order.ASC : Order.DESC;
            String property = sort.getProperty();
            Path<?> parent;
            if ("board_no".equals(property) || "posts_no".equals(property)) parent = QPosts.posts.postsId;
            else parent = QPosts.posts;

            Path<Object> target = Expressions.path(Object.class, parent, CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, property));
            @SuppressWarnings({ "unchecked", "rawtypes" })
			OrderSpecifier<?> orderSpecifier = new OrderSpecifier(order, target);
            query.orderBy(orderSpecifier);
        });

        QueryResults<PostsListResponseDto> result = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()) //페이징
                .fetchResults();

        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }

    /**
     * 게시판별 최근 게시물 목록 조회
     * <p>
     * JPQL 은 from 절에서 서브쿼리를 사용할 수 없어서 SQLQueryFactory 를 사용해 Native SQL 로 조회
     * MySQL8 부터는 ROW_NUMBER, RANK 함수를 지원, 탬플릿에서는 MySQL5.7 로 개발해서 mysql 변수를 사용하는 방법으로 조회
     * MySQL 문법이 포함되어있어서 다른 DBMS 를 사용하는 경우 수정 필요
     * <p>
     * 인프런 김영한 강사는 추천하지 않는다.
     * SqlQueryFactory는 저는 권장하지 않습니다. DB에서 메타데이터를 다 뽑아내서 생성해야 하는데... 너무 복잡하고 기능에 한계도 많습니다.
     * 따라서 JPA와 JPA용 Querydsl을 최대한 사용하고, 그래도 잘 안되는 부분은 네이티브 쿼리를 사용하는 것이 더 좋다 생각합니다.
     * <p>
     * 게시판별로 반복하여 게시물 조회하는 방법 등으로 JPQL 만을 사용해서 조회 가능
     *
     * @param boardNos   게시판 번호 목록
     * @param postsCount 게시물 수
     * @return List<PostsSimpleResponseDto> 게시물 응답 DTO List
     */
    @Override
	public List<PostsSimpleResponseDto> findAllByBoardNosLimitCount(List<Integer> boardNos, Integer postsCount) {
        // path 정의
        Path<Posts> postsPath = Expressions.path(Posts.class, "posts");
        NumberPath<Integer> boardNoPath = Expressions.numberPath(Integer.class, postsPath, "board_no");
        NumberPath<Integer> postsNoPath = Expressions.numberPath(Integer.class, postsPath, "posts_no");

        // 게시판번호, 로우넘 변수
        StringPath varPath = Expressions.stringPath("v");
        SQLQuery<Tuple> varSql = SQLExpressions.select(
                Expressions.stringTemplate("@boardNo := 0"),
                Expressions.stringTemplate("@rn := 0"));

        // 게시물 조회
        SQLQuery<Tuple> rownumSql = SQLExpressions
                .select(boardNoPath,
                        postsNoPath,
                        Expressions.stringPath(postsPath, "posts_title"),
                        Expressions.stringPath(postsPath, "posts_content"),
                        Expressions.datePath(LocalDateTime.class, postsPath, "created_date"),
                        Expressions.stringTemplate("(CASE @boardNo WHEN posts.board_no THEN @rn := @rn + 1 ELSE @rn := 1 END)").as("rn"),
                        Expressions.stringTemplate("(@boardNo := posts.board_no)").as("boardNo"))
                .from(QPosts.posts, postsPath)
                .innerJoin(varSql, varPath)
                .where(boardNoPath.in(boardNos)
                        .and(Expressions.numberPath(Integer.class, postsPath, "delete_at").eq(0)))
                .orderBy(boardNoPath.asc(), postsNoPath.desc());

        // 최근 게시물 조회
        return sqlQueryFactory
                .select(new QPostsSimpleResponseDto(boardNoPath,
                        postsNoPath,
                        Expressions.stringPath(postsPath, "posts_title"),
                        Expressions.stringPath(postsPath, "posts_content"),
                        Expressions.datePath(LocalDateTime.class, postsPath, "created_date")))
                .from(rownumSql, postsPath)
                .where(Expressions.numberPath(Integer.class, postsPath, "rn").loe(postsCount))
                .fetch();
    }

    /**
     * 게시물 상세 조회
     *
     * @param boardNo 게시판 번호
     * @param postsNo 게시물 번호
     * @param userId  사용자 id
     * @param ipAddr  ip 주소
     * @return PostsResponseDto 게시물 상세 응답 DTO
     */
    @Override
	public PostsResponseDto findById(Integer boardNo, Integer postsNo, String userId, String ipAddr) {
        return jpaQueryFactory
                .select(
                        // 게시물
                        new QPostsResponseDto(
                                QPosts.posts.postsId.boardNo,
                                QPosts.posts.postsId.postsNo,
                                QPosts.posts.postsTitle,
                                QPosts.posts.postsContent,
                                QPosts.posts.postsAnswerContent,
                                QPosts.posts.attachmentCode,
                                QPosts.posts.readCount,
                                QPosts.posts.noticeAt,
                                QPosts.posts.deleteAt,
                                QPosts.posts.createdBy,
                                QUser.user.userName.as("createdName"),
                                QPosts.posts.createdDate,
                                // 게시판
                                new QBoardResponseDto(QBoard.board.boardNo,
                                        QBoard.board.boardName,
                                        QBoard.board.skinTypeCode,
                                        QBoard.board.titleDisplayLength,
                                        QBoard.board.postDisplayCount,
                                        QBoard.board.pageDisplayCount,
                                        QBoard.board.newDisplayDayCount,
                                        QBoard.board.editorUseAt,
                                        QBoard.board.userWriteAt,
                                        QBoard.board.commentUseAt,
                                        QBoard.board.uploadUseAt,
                                        QBoard.board.uploadLimitCount,
                                        QBoard.board.uploadLimitSize),
                                // 댓글 수
                                // getCommentCountExpression(),
                                // 조회 사용자의 게시물 조회 수(조회 수 증가 확인 용)
                                ExpressionUtils.as(
                                        JPAExpressions.select(ExpressionUtils.count(QPostsRead.postsRead.userId))
                                                .from(QPostsRead.postsRead)
                                                .where(QPostsRead.postsRead.postsReadId.boardNo.eq(QPosts.posts.postsId.boardNo)
                                                        .and(QPostsRead.postsRead.postsReadId.postsNo.eq(QPosts.posts.postsId.postsNo))
                                                        .and(getBooleanExpression("userId", userId))
                                                        .and(getBooleanExpression("ipAddr", ipAddr))),
                                        "userPostsReadCount")))
                .from(QPosts.posts) // 게시물
                .innerJoin(QBoard.board).on(QPosts.posts.postsId.boardNo.eq(QBoard.board.boardNo)) // 게시판
                .leftJoin(QUser.user).on(QPosts.posts.createdBy.eq(QUser.user.userId)) // 생성자
                .fetchJoin()
                .where(QPosts.posts.postsId.boardNo.eq(boardNo)
                        .and(QPosts.posts.postsId.postsNo.eq(postsNo)))
                .fetchOne();
    }

    /**
     * 이전 게시물 조회
     *
     * @param boardNo    게시판 번호
     * @param postsNo    게시물 번호
     * @param gap        차이 -1: 이전, 1: 이후
     * @param deleteAt   삭제 여부
     * @param requestDto 요청 DTO
     * @return List<PostsSimpleResponseDto> 게시물 상세 응답 DTO List
     */
    @Override
	public List<PostsSimpleResponseDto> findNearPost(Integer boardNo, Integer postsNo, long gap, Integer deleteAt, RequestDto requestDto) {
        return jpaQueryFactory
                .select(new QPostsSimpleResponseDto(
                        QPosts.posts.postsId.boardNo,
                        QPosts.posts.postsId.postsNo,
                        QPosts.posts.postsTitle,
                        QPosts.posts.postsContent,
                        QPosts.posts.createdDate))
                .from(QPosts.posts)
                .where(QPosts.posts.postsId.boardNo.eq(boardNo)
                        .and(getBooleanExpression(requestDto.getKeywordType(), requestDto.getKeyword()))
                        .and(getBooleanExpression("deleteAt", deleteAt))
                        .and(getBooleanExpression(gap < 0 ? "postsNoLt" : "postsNoGt", postsNo)))
                .orderBy(gap < 0 ? QPosts.posts.noticeAt.asc() : QPosts.posts.noticeAt.desc(),
                        QPosts.posts.postsId.boardNo.asc(),
                        gap < 0 ? QPosts.posts.postsId.postsNo.desc() : QPosts.posts.postsId.postsNo.asc())
                .limit((gap < 0 ? -1 : 1) * gap)
                // .fetchFirst() // 단건 리턴
                .fetch();
    }

    /**
     * 다음 게시물 번호 조회
     *
     * @param boardNo 게시판 번호
     * @return Integer 다음 게시물 번호
     */
    @Override
	public Integer findNextPostsNo(Integer boardNo) {
        return jpaQueryFactory
                .select(QPosts.posts.postsId.postsNo.max().add(1).coalesce(1))
                .from(QPosts.posts)
                .where(QPosts.posts.postsId.boardNo.eq(boardNo))
                .fetchOne();
    }

    /**
     * 게시물 조회 수 증가
     *
     * @param boardNo 게시판 번호
     * @param postsNo 게시물 번호
     * @return Long 처리 건수
     */
    @Override
	public Long updateReadCount(Integer boardNo, Integer postsNo) {
        return jpaQueryFactory.update(QPosts.posts)
                .set(QPosts.posts.readCount, QPosts.posts.readCount.add(1))
                .where(QPosts.posts.postsId.boardNo.eq(boardNo)
                        .and(QPosts.posts.postsId.postsNo.eq(postsNo)))
                .execute();
    }

    /**
     * 게시물 삭제 여부 수정
     *
     * @param posts    게시물 정보(게시판번호, 게시물번호배열)
     * @param deleteAt 삭제 여부
     * @param userId   사용자 id
     * @return Long 수정 건수
     */
    @Override
	public Long updateDeleteAt(Map<Integer, List<Integer>> posts, Integer deleteAt, String userId) {
        long updateCount = 0L;

        Iterator<Integer> iterator = posts.keySet().iterator();
        while (iterator.hasNext()) {
            Integer boardNo = iterator.next();

            List<Integer> postsNoList = posts.get(boardNo);

            updateCount += jpaQueryFactory.update(QPosts.posts)
                    .set(QPosts.posts.deleteAt, deleteAt)
                    .set(QPosts.posts.lastModifiedBy, userId)
                    .set(QPosts.posts.modifiedDate, LocalDateTime.now())
                    .where(QPosts.posts.postsId.boardNo.eq(boardNo)
                            .and(QPosts.posts.postsId.postsNo.in(postsNoList)))
                    .execute();
        }

        return updateCount;
    }

    /**
     * 댓글 수 표현식
     *
     * @param deleteAt 삭제 여부
     * @return SimpleExpression<Long> 댓글 수 표현식
     */
    private SimpleExpression<Long> getCommentCountExpression(Integer deleteAt) {
        BooleanExpression deleteAtExpression = null;
        if (deleteAt != null) {
            deleteAtExpression = deleteAt == 0 ? QComment.comment.deleteAt.eq(0) : QComment.comment.deleteAt.ne(0);
        }

        return Expressions.as(new CaseBuilder()
                        .when(QBoard.board.commentUseAt.eq(true))
                        .then(JPAExpressions.select(ExpressionUtils.count(QComment.comment.commentId.commentNo))
                                .from(QComment.comment)
                                .where(QComment.comment.commentId.postsId.boardNo.eq(QPosts.posts.postsId.boardNo)
                                        .and(QComment.comment.commentId.postsId.postsNo.eq(QPosts.posts.postsId.postsNo))
                                        .and(QComment.comment.commentId.postsId.postsNo.eq(QPosts.posts.postsId.postsNo))
                                        .and(deleteAtExpression)))
                        .otherwise(0L)
                , "commentCount");
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
            case "deleteAt": // 삭제 여부
                return QPosts.posts.deleteAt.eq((Integer) attributeValue);
            case "postsData": // 게시물 제목 + 내용
                return QPosts.posts.postsTitle.containsIgnoreCase((String) attributeValue).or(QPosts.posts.postsContent.containsIgnoreCase((String) attributeValue));
            case "postsTitle": // 게시물 제목
                return QPosts.posts.postsTitle.containsIgnoreCase((String) attributeValue);
            case "postsContent": // 게시물 내용
                return QPosts.posts.postsContent.containsIgnoreCase((String) attributeValue);
            case "postsNoLt": // 게시물 번호
                return QPosts.posts.postsId.postsNo.lt((Integer) attributeValue);
            case "postsNoGt": // 게시물 번호
                return QPosts.posts.postsId.postsNo.gt((Integer) attributeValue);
            default:
                return null;
        }
    }

}
