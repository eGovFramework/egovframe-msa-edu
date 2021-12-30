package org.egovframe.cloud.portalservice.domain.attachment;

import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.egovframe.cloud.servlet.domain.BaseEntity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import org.springframework.util.StringUtils;

/**
 * org.egovframe.cloud.portalservice.domain.attachment.Attachment
 * <p>
 * 첨부파일 엔티티 class
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

/**
 * @TODO
 * 관련 엔티티를 어떻게 처리하는지 에 따라
 * 컬럼명이 변경되거나 삭제될 수 있음.
 * 아직 용어사전에 넣지 않았으므로 기능 완료되면 용어사전에도 fix된 컬럼명을 넣어야 함.
 * 2021/07/26 shinmj!!
 */
@Getter
@NoArgsConstructor
@ToString
@Entity
public class Attachment extends BaseEntity {

    @EmbeddedId
    private AttachmentId attachmentId;

    /**
     * 복합키를 HTTP URI에 표현하기 힘드므로 대체키를 추가한다.
     */
    @Column(name = "attachment_id", length = 50, nullable = false, unique = true)
    private String uniqueId;

    @Column(nullable = false, length = 200)
    private String physicalFileName;

    @Column(nullable = false, length = 200)
    private String originalFileName;

    @Column(name = "attachment_size", length = 20)
    private Long size;

    @Column(name = "file_type_value", length = 100)
    private String fileType;

    @Column(name = "download_count", length = 15)
    private Long downloadCnt;

    @Column(name = "delete_at", columnDefinition = "boolean default false")
    private Boolean isDelete;

    @Column(length = 200)
    private String entityName;

    @Column(length = 50)
    private String entityId;

    @Builder
    public Attachment(AttachmentId attachmentId, String uniqueId,
                      String physicalFileName, String originalFileName,
                      Long size, String fileType,
                      String entityName, String entityId) {
        this.attachmentId = attachmentId;
        this.uniqueId = uniqueId;
        this.physicalFileName = physicalFileName;
        this.originalFileName = originalFileName;
        this.size = size;
        this.fileType = fileType;
        this.entityName = entityName;
        this.entityId = entityId;
        this.isDelete = false;
        this.downloadCnt = 0L;
    }

    /**
     * 삭제 여부 토글
     *
     * @param isDelete
     * @return
     */
    public Attachment updateIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
        return this;
    }

    /**
     * 첨부파일 다운로드 할 때 마다 Download 횟수 + 1
     *
     * @return
     */
    public Attachment updateDownloadCnt() {
        this.downloadCnt = getDownloadCnt() == null ? 1 : getDownloadCnt() + 1;
        return this;
    }

    /**
     * entity 정보 update
     *
     * @param entityName
     * @param entityId
     * @return
     */
    public Attachment updateEntity(String entityName, String entityId) {
        this.entityName = entityName;
        this.entityId = entityId;
        return this;
    }

    public boolean hasEntityId()  {
        return (Objects.nonNull(entityId) || StringUtils.hasText(entityId)) && !"-1".equals(entityId);
    }

    public boolean isDeleted() {
        return Boolean.TRUE.equals(isDelete);
    }


}
