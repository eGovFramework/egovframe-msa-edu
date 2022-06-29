package org.egovframe.cloud.reservechecksevice.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * org.egovframe.cloud.reservechecksevice.domain.ReserveStatus
 * <p>
 * 예약 상태 enum class
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/09/15
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/15    shinmj       최초 생성
 * </pre>
 */
@Getter
@RequiredArgsConstructor
public enum ReserveStatus {
    REQUEST("request", "예약 신청"),
    APPROVE("approve", "예약 승인"),
    CANCEL("cancel", "예약 취소"),
    DONE("done", "완료");

    private final String key;
    private final String title;

    public boolean isEquals(String status) {
        return this.getKey().equals(status);
    }
}
