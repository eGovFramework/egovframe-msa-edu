package org.egovframe.cloud.reserverequestservice.config;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * org.egovframe.cloud.reserverequestservice.config.RequestMessage
 *
 * 예약 신청 후 이벤트 스트림 message VO class
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/09/16
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/16    shinmj       최초 생성
 * </pre>
 */
@NoArgsConstructor
@Getter
@ToString
public class RequestMessage {
    private String reserveId;
    private Boolean isItemUpdated;

    @Builder
    public RequestMessage(String reserveId, Boolean isItemUpdated) {
        this.reserveId = reserveId;
        this.isItemUpdated = isItemUpdated;
    }
}
