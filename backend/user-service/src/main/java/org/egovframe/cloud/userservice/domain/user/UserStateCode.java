package org.egovframe.cloud.userservice.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * org.egovframe.cloud.userservice.domain.user.UserStateCode
 *
 * 사용자 상태 코드 열거형 상수
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/09/17
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *    수정일       수정자              수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/17    jooho       최초 생성
 * </pre>
 */
@Getter
@RequiredArgsConstructor
public enum UserStateCode {

    WAIT("00", "대기"),
    NORMAL("01", "정상"),
    HALT("07", "정지"),
    LEAVE("08", "탈퇴"),
    DELETE("09", "삭제");

    private final String key;
    private final String title;

    /**
     * 사용자 상태 코드로 상수 검색
     *
     * @param key 사용자 상태 코드
     * @return UserStateCode 사용자 상태 코드 상수
     */
    public static UserStateCode findByKey(String key) {
        return Arrays.stream(UserStateCode.values()).filter(c -> c.getKey().equals(key)).findAny().orElse(null);
    }

}
