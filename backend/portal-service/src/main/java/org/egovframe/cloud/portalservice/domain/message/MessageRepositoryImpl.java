package org.egovframe.cloud.portalservice.domain.message;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.egovframe.cloud.portalservice.api.message.dto.MessageListResponseDto;

import java.util.*;
import java.util.stream.Collectors;

import static com.querydsl.core.types.Projections.fields;
import static org.egovframe.cloud.portalservice.domain.message.QMessage.message;
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
public class MessageRepositoryImpl implements MessageRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * Message 목록 조회
     * 영문 요청시 값이 없는 레코드는 한글로 대체된다.
     *
     * @param lang
     * @return
     */
    @Override
    public List<MessageListResponseDto> findAllMessages(String lang) {

        return queryFactory
                .select(fields(MessageListResponseDto.class,
                        message.messageId,
                        new CaseBuilder()
                                .when(
                                        langEnEq(lang)
                                                .and(message.messageEnName.isNotNull())
                                ).then(message.messageEnName)
                                .otherwise(message.messageKoName).as("messageName")
                ))
                .from(message)
                .orderBy(message.messageId.asc())
                .fetch();
    }

    /**
     * Message 목록 조회
     * 영문 요청시 값이 없는 레코드는 한글로 대체된다.
     *
     * @param lang
     * @return
     */
    @Override
    public Map<String, String> findAllMessagesMap(String lang) {
        List<Tuple> messages = queryFactory
                .select(message.messageId,
                        new CaseBuilder()
                                .when(
                                        langEnEq(lang)
                                                .and(message.messageEnName.isNotNull())
                                ).then(message.messageEnName)
                                .otherwise(message.messageKoName).as(message.messageKoName)
                )
                .from(message)
                .orderBy(message.messageId.asc())
                .fetch();

        return messages.stream()
                .collect(Collectors.toMap(tuple -> tuple.get(message.messageId), tuple -> tuple.get(message.messageKoName), (a, b) -> b));
    }

    /**
     * 영문 요청인지 여부를 리턴한다
     *
     * @param lang
     * @return
     */
    private BooleanExpression langEnEq(String lang) {
        boolean en = hasLength(lang) && lang.equals("en");
        return Expressions.asBoolean(en).isTrue();
    }
}
