package org.egovframe.cloud.portalservice.domain.message;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egovframe.cloud.servlet.domain.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * org.egovframe.cloud.portalservice.domain.message.Message
 * <p>
 * 메시지 엔티티
 * Spring MessageSouce 데이터 관리
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/07/22
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/22    jaeyeolkim  최초 생성
 * </pre>
 */
@Getter
@NoArgsConstructor
@Entity
public class Message extends BaseEntity {

    @Id
    private String messageId;

    @Column(nullable = false, length = 2000)
    private String messageKoName; // 한글명

    @Column(length = 2000)
    private String messageEnName; // 영문명

    @Column(length = 500)
    private String messageDescription; // 메시지 설명

    @Builder
    public Message(String messageId, String messageKoName, String messageEnName, String messageDescription) {
        this.messageId = messageId;
        this.messageKoName = messageKoName;
        this.messageEnName = messageEnName;
        this.messageDescription = messageDescription;
    }
}
