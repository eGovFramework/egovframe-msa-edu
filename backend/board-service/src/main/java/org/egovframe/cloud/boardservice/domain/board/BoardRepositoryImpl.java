package org.egovframe.cloud.boardservice.domain.board;

import static com.querydsl.core.types.Projections.constructor;

import com.google.common.base.CaseFormat;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.egovframe.cloud.boardservice.api.board.dto.BoardListResponseDto;
import org.egovframe.cloud.boardservice.api.board.dto.BoardResponseDto;
import org.egovframe.cloud.boardservice.api.board.dto.QBoardListResponseDto;
import org.egovframe.cloud.boardservice.api.board.dto.QBoardResponseDto;
import org.egovframe.cloud.boardservice.domain.code.QCode;
import org.egovframe.cloud.common.dto.RequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * org.egovframe.cloud.boardservice.domain.board.BoardRepositoryImpl
 * <p>
 * 게시판 Querydsl 구현 클래스
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
@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepositoryCustom {

    /**
     * DML 생성을위한 Querydsl 팩토리 클래스
     */
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 게시판 페이지 목록 조회
     * 가급적 Entity 보다는 Dto를 리턴 - Entity 조회시 hibernate 캐시, 불필요 컬럼 조회, oneToOne N+1 문제 발생
     *
     * @param requestDto 요청 DTO
     * @param pageable   페이지 정보
     * @return Page<BoardListResponseDto> 페이지 게시판 목록 응답 DTO
     */
    @Override
	public Page<BoardListResponseDto> findPage(RequestDto requestDto, Pageable pageable) {
        JPQLQuery<BoardListResponseDto> query = jpaQueryFactory
                .select(constructor(BoardListResponseDto.class,
                        QBoard.board.boardNo,
                        QBoard.board.boardName,
                        QBoard.board.skinTypeCode,
                        Expressions.as(QCode.code.codeName, "skinTypeCodeName"),
                        QBoard.board.createdDate,
                        new CaseBuilder()
                            .when(QBoard.board.posts.size().gt(0))
                            .then(Boolean.TRUE)
                            .otherwise(Boolean.FALSE).as("isPosts")
                ))
                .from(QBoard.board)
                .leftJoin(QCode.code).on(QBoard.board.skinTypeCode.eq(QCode.code.codeId).and(QCode.code.parentCodeId.eq("skin_type_code")))
                .fetchJoin()
                .where(getBooleanExpressionKeyword(requestDto));

        //정렬
        pageable.getSort().stream().forEach(sort -> {
            Order order = sort.isAscending() ? Order.ASC : Order.DESC;
            String property = sort.getProperty();

            Path<Object> target = Expressions.path(Object.class, QBoard.board, CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, property));
            @SuppressWarnings({ "rawtypes", "unchecked" })
			OrderSpecifier<?> orderSpecifier = new OrderSpecifier(order, target);
            query.orderBy(orderSpecifier);
        });

        QueryResults<BoardListResponseDto> result = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()) //페이징
                .fetchResults();

        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }

    /**
     * 게시판 목록 조회
     *
     * @param boardNos 게시판 번호 목록
     * @return List<BoardResponseDto> 게시판 상세 응답 DTO List
     */
    @Override
	public List<BoardResponseDto> findAllByBoardNoIn(List<Integer> boardNos) {
        return jpaQueryFactory
                .select(new QBoardResponseDto(
                        QBoard.board.boardNo,
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
                        QBoard.board.uploadLimitSize
                ))
                .from(QBoard.board)
                .leftJoin(QCode.code).on(QBoard.board.skinTypeCode.eq(QCode.code.codeId).and(QCode.code.parentCodeId.eq("skin_type_code")))
                .fetchJoin()
                .where(QBoard.board.boardNo.in(boardNos))
                .orderBy(QBoard.board.boardNo.asc())
                .fetchResults().getResults();
    }

    /**
     * 요청 DTO로 동적 검색 표현식 리턴
     *
     * @param requestDto 요청 DTO
     * @return BooleanExpression 검색 표현식
     */
    private BooleanExpression getBooleanExpressionKeyword(RequestDto requestDto) {
        if (requestDto.getKeyword() == null || "".equals(requestDto.getKeyword())) return null;

        switch (requestDto.getKeywordType()) {
            case "boardName": // 게시판 명
                return QBoard.board.boardName.containsIgnoreCase(requestDto.getKeyword());
            default:
                return null;
        }
    }

}
