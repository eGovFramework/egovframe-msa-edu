package org.egovframe.cloud.portalservice.domain.code;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.portalservice.api.code.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import javax.persistence.EntityManager;
import java.util.List;

import static com.querydsl.core.types.Projections.fields;
import static org.egovframe.cloud.portalservice.domain.code.QCode.code;
import static org.springframework.util.StringUtils.hasLength;

/**
 * org.egovframe.cloud.portalservice.domain.code.CodeRepositoryImpl
 * <p>
 * 공통코드 Querydsl 구현 클래스
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/07/12
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/12    jaeyeolkim  최초 생성
 * </pre>
 */
@RequiredArgsConstructor
public class CodeRepositoryImpl implements CodeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 공통코드 목록 조회
     *
     * @param requestDto
     * @param pageable
     * @return
     */
    @Override
    public Page<CodeListResponseDto> findAllByKeyword(RequestDto requestDto, Pageable pageable) {
        QCode childCode = new QCode("childCode");
        List<CodeListResponseDto> content =
                queryFactory
                        .select(fields(CodeListResponseDto.class,
                                code.codeId,
                                code.codeName,
                                code.codeDescription,
                                code.useAt,
                                code.readonly,
                                childCode.codeId.count().as("codeDetailCount")
                        ))
                        .from(code)
                        .leftJoin(childCode).on(code.parentCodeId.eq(childCode.codeId))
                        .where(
                                code.parentCodeId.isNull(),
                                keyword(requestDto.getKeywordType(), requestDto.getKeyword())
                        )
                        .groupBy(code.codeId,
                                code.codeName,
                                code.codeDescription,
                                code.useAt,
                                code.readonly)
                        .orderBy(code.sortSeq.asc(), code.createdDate.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();

        JPAQuery<Code> query = queryFactory
                .selectFrom(code)
                .where(
                        code.parentCodeId.isNull(),
                        keyword(requestDto.getKeywordType(), requestDto.getKeyword())
                );

        return PageableExecutionUtils.getPage(content, pageable, query::fetchCount);
    }

    /**
     * 공통코드 상세 목록 조회
     *
     * @param requestDto
     * @param pageable
     * @return
     */
    @Override
    public Page<CodeDetailListResponseDto> findAllDetailByKeyword(CodeDetailRequestDto requestDto, Pageable pageable) {
        List<CodeDetailListResponseDto> content =
                queryFactory
                        .select(fields(CodeDetailListResponseDto.class,
                                code.parentCodeId,
                                code.codeId,
                                code.codeName,
                                code.sortSeq,
                                code.useAt,
                                code.readonly
                        ))
                        .from(code)
                        .where(
                                code.parentCodeId.isNotNull(),
                                parentCodeIdEq(requestDto.getParentCodeId()),
                                keyword(requestDto.getKeywordType(), requestDto.getKeyword())
                        )
                        .groupBy(code.codeId,
                                code.codeName,
                                code.sortSeq,
                                code.useAt,
                                code.readonly)
                        .orderBy(code.parentCodeId.asc(), code.sortSeq.asc(), code.createdDate.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();

        JPAQuery<Code> query = queryFactory
                .selectFrom(code)
                .where(
                        code.parentCodeId.isNotNull(),
                        parentCodeIdEq(requestDto.getParentCodeId()),
                        keyword(requestDto.getKeywordType(), requestDto.getKeyword())
                );

        return PageableExecutionUtils.getPage(content, pageable, query::fetchCount);
    }

    /**
     * 공통코드 상세 목록 - parentCodeId 에 해당하는 사용중인 공통코드 목록
     *
     * @param parentCodeId
     * @return
     */
    @Override
    public List<CodeDetailResponseDto> findDetailsByParentCodeIdUseAt(String parentCodeId) {
        return queryFactory
                .select(fields(CodeDetailResponseDto.class,
                        code.parentCodeId,
                        code.codeId,
                        code.codeName,
                        code.sortSeq,
                        code.useAt,
                        code.readonly
                ))
                .from(code)
                .where(
                        code.parentCodeId.eq(parentCodeId)
                                .and(code.useAt.eq(true))
                )
                .orderBy(code.sortSeq.asc())
                .fetch();
    }

    /**
     * 공통코드 상세 목록 - parentCodeId 에 해당하는 사용중인 공통코드 목록
     * 사용여부가 false 로 변경된 경우에도 인자로 받은 공통코드를 목록에 포함되도록 한다
     *
     * @param parentCodeId
     * @param codeId
     * @return
     */
    @Override
    public List<CodeDetailResponseDto> findDetailsUnionCodeIdByParentCodeId(String parentCodeId, String codeId) {
        return queryFactory
                .select(fields(CodeDetailResponseDto.class,
                        code.parentCodeId,
                        code.codeId,
                        code.codeName,
                        code.sortSeq,
                        code.useAt,
                        code.readonly
                ))
                .from(code)
                .where(
                        code.parentCodeId.eq(parentCodeId)
                                .and(code.useAt.eq(true).or(code.codeId.eq(codeId)))
                )
                .orderBy(code.sortSeq.asc())
                .fetch();
    }

    /**
     * 공통코드 목록 - parentCodeId 가 없는 상위공통코드
     *
     * @return
     */
    @Override
    public List<CodeResponseDto> findAllParent() {
        return queryFactory
                .select(fields(CodeResponseDto.class,
                        code.codeId,
                        code.codeName,
                        code.sortSeq,
                        code.useAt
                ))
                .from(code)
                .where(code.parentCodeId.isNull())
                .orderBy(code.sortSeq.asc())
                .fetch();
    }

    /**
     * 부모 공통코드 단건 조회
     *
     * @param codeId
     * @return
     */
    @Override
    public CodeResponseDto findParentByCodeId(String codeId) {
        QCode parent = new QCode("parent");
        return queryFactory
                .select(fields(CodeResponseDto.class,
                        parent.codeId,
                        parent.codeName,
                        parent.useAt
                        ))
                .from(code)
                .join(parent)
                    .on(code.parentCodeId.eq(parent.codeId))
                .where(code.codeId.eq(codeId))
                .fetchOne();
    }

    /**
     * 공통코드 parentCodeId 에 해당코드가 존재하는지 여부를 알기 위해 건수를 카운트한다
     *
     * @param codeId
     * @return
     */
    @Override
    public long countByParentCodeId(String codeId) {
        return queryFactory
                .selectFrom(code)
                .where(code.parentCodeId.eq(codeId))
                .fetchCount();
    }

    /**
     * 공통코드 조회조건
     *
     * @param keywordType
     * @param keyword
     * @return
     */
    private BooleanExpression keyword(String keywordType, String keyword) {
        if (!hasLength(keywordType) || !hasLength(keyword)) {
            return null;
        }

        if ("codeId".equals(keywordType)) {
            return code.codeId.containsIgnoreCase(keyword);
        } else if ("codeName".equals(keywordType)) {
            return code.codeName.containsIgnoreCase(keyword);
        }
        return null;
    }

    /**
     * 공통코드 상세 목록 추가 조회조건 - 상위코드
     *
     * @param parentCodeId
     * @return
     */
    private BooleanExpression parentCodeIdEq(String parentCodeId) {
        return hasLength(parentCodeId) ? code.parentCodeId.eq(parentCodeId) : null;
    }

}
