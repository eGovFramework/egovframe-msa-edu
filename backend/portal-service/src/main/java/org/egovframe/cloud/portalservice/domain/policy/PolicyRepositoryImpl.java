package org.egovframe.cloud.portalservice.domain.policy;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.portalservice.api.policy.dto.PolicyResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;

import static com.querydsl.core.types.Projections.*;
import static com.querydsl.core.types.Projections.fields;
import static org.egovframe.cloud.portalservice.domain.policy.QPolicy.policy;
import static org.springframework.util.StringUtils.hasLength;

/**
 * org.egovframe.cloud.portalservice.domain.policy.PolicyRepositoryImpl
 * <p>
 * 이용약관/개인정보수집동의(Policy) querydsl 사용 확장 repository class
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/07/06
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/06    shinmj  최초 생성
 * </pre>
 */
@Slf4j
public class PolicyRepositoryImpl implements PolicyRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public PolicyRepositoryImpl(EntityManager em) {
        queryFactory = new JPAQueryFactory(em);
    }

    /**
     * 목록 조회
     *
     * @param requestDto
     * @param pageable
     * @return
     */
    @Override
    public Page<PolicyResponseDto> search(RequestDto requestDto, Pageable pageable) {

        QueryResults<PolicyResponseDto> results = queryFactory.select(
                constructor(PolicyResponseDto.class, policy)
        )
                .from(policy)
                .where(
                        searchTextLike(requestDto)
                )
                .orderBy( policy.regDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());

    }

    /**
     * 회원가입 시 사용여부=true인 최근 등록된 자료 조회
     *
     * @param type
     * @return
     */
    @Override
    public PolicyResponseDto searchOne(String type) {
        return queryFactory.select(constructor(PolicyResponseDto.class, policy))
                .from(policy)
                .where(
                        policy.isUse.eq(true),
                        policy.type.eq(type)
                )
                .orderBy(policy.regDate.desc())
                .limit(1)
                .fetchOne();
    }

    /**
     * dynamic query binding
     *
     * @param requestDto
     * @return
     */
    private BooleanExpression searchTextLike(RequestDto requestDto) {
        final String searchType = requestDto.getKeywordType();
        final String value = requestDto.getKeyword();
        if (!hasLength(searchType) || !hasLength(value)) {
            return null;
        }

        if ("title".equals(searchType)) {
            return policy.title.containsIgnoreCase(value);
        } else if ("contents".equals(searchType)) {
            return policy.contents.containsIgnoreCase(value);
        }
        return null;
    }
}
