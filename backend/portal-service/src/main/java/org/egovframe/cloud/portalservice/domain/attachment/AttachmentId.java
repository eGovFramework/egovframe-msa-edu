package org.egovframe.cloud.portalservice.domain.attachment;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

/**
 * org.egovframe.cloud.portalservice.domain.attachment.AttachmentId
 * <p>
 * 첨부파일 엔티티 복합키 class
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
@Data
@Embeddable
public class AttachmentId implements Serializable {
    @Column(name = "attachment_code", length = 20)
    private String code;

    @Column(name = "attachment_seq", length = 15)
    private Long seq;

    @Builder
    public AttachmentId (String code, Long seq) {
        this.code = code;
        this.seq = seq;
    }

    public AttachmentId () {
        this.code = UUID.randomUUID().toString();
        this.seq = 1L;
    }

}
