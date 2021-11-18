package org.egovframe.cloud.portalservice.domain.attachment;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.cloud.common.dto.RequestDto;
import org.egovframe.cloud.portalservice.api.attachment.dto.AttachmentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.querydsl.core.types.Projections.constructor;
import static org.egovframe.cloud.portalservice.domain.attachment.QAttachment.attachment;
import static org.springframework.util.StringUtils.hasLength;

/**
 * org.egovframe.cloud.portalservice.domain.attachment.AttachmentRepositoryImpl
 * <p>
 * 첨부파일 querydsl 구현 class
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/07/14
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/14    shinmj  최초 생성
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
public class AttachmentRepositoryImpl implements AttachmentRepositoryCustom{

    /**
     * DML 생성을위한 Querydsl 팩토리 클래스
     */
    private final JPAQueryFactory queryFactory;

    /**
     * code로 첨부파일 검색
     *
     * @param attachmentCode
     * @return
     */
    @Override
    public List<Attachment> findByCode(String attachmentCode) {
         return queryFactory.selectFrom(attachment)
                 .where(attachment.attachmentId.code.eq(attachmentCode), attachment.isDelete.eq(false))
                 .orderBy(attachment.attachmentId.seq.asc())
                 .fetch();
    }

    /**
     * 첨부파일 복합키 seq 조회하여 생성
     *
     * @param attachmentCode
     * @return
     */
    @Override
    public AttachmentId getId(String attachmentCode) {
        Long seq = queryFactory.select(
                attachment.attachmentId.seq.max()
                )
                .from(attachment)
                .where(attachment.attachmentId.code.eq(attachmentCode))
                .fetchOne();

        if (seq == null) {
            seq = 0L;
        }

        return AttachmentId.builder()
                .code(attachmentCode)
                .seq(seq+1L)
                .build();

    }

    /**
     * 관리자 - 첨부파일 목록 조회
     *
     * @param searchRequestDto
     * @param pageable
     * @return
     */
    @Override
    public Page<AttachmentResponseDto> search(RequestDto searchRequestDto, Pageable pageable) {

        QueryResults<AttachmentResponseDto> results =
                queryFactory.select(constructor(AttachmentResponseDto.class, attachment))
                .from(attachment)
                .where(
                        searchTextLike(searchRequestDto)
                )
                .orderBy(
                        attachment.createdDate.desc(),
                        attachment.attachmentId.code.asc(),
                        attachment.attachmentId.seq.asc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    /**
     * dynamic query binding
     *
     * @param requestDto
     * @return
     */
    private BooleanExpression searchTextLike(RequestDto requestDto) {
        final String type = requestDto.getKeywordType();
        final String value = requestDto.getKeyword();
        if (!hasLength(type) || !hasLength(value)) {
            return null;
        }

        if ("id".equals(type)) {
            return attachment.attachmentId.code.containsIgnoreCase(value);
        } else if ("name".equals(type)) {
            return attachment.originalFileName.containsIgnoreCase(value);
        }
        return null;
    }
}
