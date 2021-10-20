package org.egovframe.cloud.portalservice.domain.banner;

import static com.querydsl.core.types.Projections.*;

import java.util.List;
import java.util.Optional;

import org.egovframe.cloud.portalservice.api.banner.dto.BannerImageResponseDto;
import org.egovframe.cloud.portalservice.api.banner.dto.BannerListResponseDto;
import org.egovframe.cloud.portalservice.api.banner.dto.BannerRequestDto;
import org.egovframe.cloud.portalservice.domain.attachment.QAttachment;
import org.egovframe.cloud.portalservice.domain.code.QCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

/**
 * org.egovframe.cloud.portalservice.domain.banner.BannerRepositoryImpl
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/08/18
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *    수정일       수정자              수정내용
 *  ----------    --------    ---------------------------
 *  2021/08/18    jooho       최초 생성
 * </pre>
 */
@RequiredArgsConstructor
public class BannerRepositoryImpl implements BannerRepositoryCustom {

    /**
     * DML 생성을위한 Querydsl 팩토리 클래스
     */
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 배너 페이지 목록 조회
     * 가급적 Entity 보다는 Dto를 리턴 - Entity 조회시 hibernate 캐시, 불필요 컬럼 조회, oneToOne N+1 문제 발생
     *
     * @param requestDto 요청 DTO
     * @param pageable   페이지 정보
     * @return Page<BannerListResponseDto> 페이지 배너 목록 응답 DTO
     */
    public Page<BannerListResponseDto> findPage(BannerRequestDto requestDto, Pageable pageable) {
        QueryResults<BannerListResponseDto> result = jpaQueryFactory
                .select(fields(BannerListResponseDto.class,
                    QBanner.banner.bannerNo,
                    QBanner.banner.bannerTypeCode,
                    Expressions.as(QCode.code.codeName, "bannerTypeCodeName"),
                    QBanner.banner.bannerTitle,
                    QBanner.banner.useAt,
                    QBanner.banner.createdDate,
                    QBanner.banner.site.name.as("siteName")
                    ))
                .from(QBanner.banner)
                .leftJoin(QCode.code).on(QBanner.banner.bannerTypeCode.eq(QCode.code.codeId).and(QCode.code.parentCodeId.eq("banner_type_code")))
                .fetchJoin()
                .where(getBooleanExpressionKeyword(requestDto), getEqualsBooleanExpression("siteId", requestDto.getSiteId()))
                .orderBy(QBanner.banner.site.sortSeq.asc(), QBanner.banner.sortSeq.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()) //페이징
                .fetchResults();

        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }

    /**
     * 배너 목록 조회
     *
     * @param bannerTypeCode 배너 유형 코드
     * @param bannerCount    배너 수
     * @param useAt          사용 여부
     * @return List<BannerImageResponseDto> 배너 이미지 응답 DTO List
     */
    public List<BannerImageResponseDto> findList(String bannerTypeCode, Integer bannerCount, Boolean useAt, Long siteId) {
        JPQLQuery<BannerImageResponseDto> query = jpaQueryFactory
                .select(constructor(BannerImageResponseDto.class,
                    QBanner.banner.bannerNo,
                    QBanner.banner.bannerTypeCode,
                    QBanner.banner.bannerTitle,
                    QBanner.banner.attachmentCode,
                    JPAExpressions.select(QAttachment.attachment.uniqueId)
                        .from(QAttachment.attachment)
                        .where(QAttachment.attachment.attachmentId.code.eq(QBanner.banner.attachmentCode)
                            .and(QAttachment.attachment.isDelete.eq(Boolean.FALSE))
                            .and(QAttachment.attachment.attachmentId.seq.eq(
                                JPAExpressions.select(QAttachment.attachment.attachmentId.seq.max())
                                    .from(QAttachment.attachment)
                                    .where(QAttachment.attachment.attachmentId.code.eq(QBanner.banner.attachmentCode)
                                        .and(QAttachment.attachment.isDelete.eq(Boolean.FALSE)))))),
                    QBanner.banner.urlAddr,
                    QBanner.banner.newWindowAt,
                    QBanner.banner.bannerContent
                    ))
                .from(QBanner.banner)
                .where(QBanner.banner.site.id.eq(siteId),
                    getEqualsBooleanExpression("bannerTypeCode", bannerTypeCode),
                        getEqualsBooleanExpression("useAt", useAt))
                .orderBy(QBanner.banner.sortSeq.asc());

        if (bannerCount != null && bannerCount > 0) {
            query.limit(bannerCount);
        }

        return query.fetch();
    }

    /**
     * 배너 다음 정렬 순서 조회
     *
     * @param siteId siteId
     * @return Integer 다음 정렬 순서
     */
    public Integer findNextSortSeq(Long siteId) {
        return jpaQueryFactory
                .select(QBanner.banner.sortSeq.max().add(1).coalesce(1))
                .from(QBanner.banner)
                .where(QBanner.banner.site.id.eq(siteId))
                .fetchOne();
    }

    /**
     * 배너 정렬 순서 수정
     *
     * @param startSortSeq    시작 정렬 순서
     * @param endSortSeq      종료 정렬 순서
     * @param increaseSortSeq 증가 정렬 순서
     * @param siteId          siteId
     * @return Long 수정 건수
     */
    public Long updateSortSeq(Integer startSortSeq, Integer endSortSeq, int increaseSortSeq, Long siteId) {
        return jpaQueryFactory.update(QBanner.banner)
                .set(QBanner.banner.sortSeq, QBanner.banner.sortSeq.add(increaseSortSeq))
                .where(QBanner.banner.site.id.eq(siteId),
                    isGoeSortSeq(startSortSeq),
                        isLoeSortSeq(endSortSeq))
                .execute();
    }

    @Override
    public Optional<Banner> findBySortSeqAndSiteId(Integer sortSeq, Long siteId) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(QBanner.banner)
            .where(QBanner.banner.site.id.eq(siteId), QBanner.banner.sortSeq.eq(sortSeq)).fetchOne());

    }

    /**
     * 엔티티 속성별 동적 검색 표현식 리턴
     *
     * @param attributeName  속성 명
     * @param attributeValue 속성 값
     * @return BooleanExpression 검색 표현식
     */
    private BooleanExpression getEqualsBooleanExpression(String attributeName, Object attributeValue) {
        if (attributeValue == null || "".equals(attributeValue.toString())) return null;

        switch (attributeName) {
            case "bannerTypeCode": // 배너 유형 코드
                return QBanner.banner.bannerTypeCode.eq((String) attributeValue);
            case "useAt": // 사용 여부
                return QBanner.banner.useAt.eq((Boolean) attributeValue);
            case "siteId":
                return QBanner.banner.site.id.eq((Long) attributeValue);
            default:
                return null;
        }
    }

    /**
     * 요청 DTO로 동적 검색 표현식 리턴
     *
     * @param requestDto 요청 DTO
     * @return BooleanExpression 검색 표현식
     */
    private BooleanExpression getBooleanExpressionKeyword(BannerRequestDto requestDto) {
        if (requestDto.getKeyword() == null || "".equals(requestDto.getKeyword())) return null;

        switch (requestDto.getKeywordType()) {
            case "bannerTitle": // 배너 제목
                return QBanner.banner.bannerTitle.containsIgnoreCase(requestDto.getKeyword());
            case "bannerContent": // 배너 내용
                return QBanner.banner.bannerContent.containsIgnoreCase(requestDto.getKeyword());
            default:
                return null;
        }
    }

    /**
     * 정렬 순서 이하 검색 표현식
     *
     * @param sortSeq 정렬 순서
     * @return BooleanExpression 검색 표현식
     */
    private BooleanExpression isLoeSortSeq(Integer sortSeq) {
        return sortSeq == null ? null : QBanner.banner.sortSeq.loe(sortSeq);
    }

    /**
     * 정렬 순서 이상 검색 표현식
     *
     * @param sortSeq 정렬 순서
     * @return BooleanExpression 검색 표현식
     */
    private BooleanExpression isGoeSortSeq(Integer sortSeq) {
        return sortSeq == null ? null : QBanner.banner.sortSeq.goe(sortSeq);
    }

}
