package org.egovframe.cloud.reserverequestservice.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReserveStatus {
    REQUEST("request", "예약 신청"),
    APPROVE("approve", "예약 승인"),
    CANCEL("cancel", "예약 취소"),
    DONE("done", "완료");

    private final String key;
    private final String title;
}
