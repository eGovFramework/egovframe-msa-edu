package org.egovframe.cloud.boardservice.util;

import org.egovframe.cloud.common.domain.Role;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * org.egovframe.cloud.boardservice.util.HttpUtil
 * <p>
 * HTTP 관련 유틸 클래스
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/09/09
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *    수정일       수정자              수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/09    jooho       최초 생성
 * </pre>
 */
public class HttpUtil {

    /**
     * static method 만으로 구성된 유틸리티 클래스
     * 객체 생성 금지
     */
    private HttpUtil() throws IllegalStateException {
        throw new IllegalStateException("Http Utility Class");
    }

    /***
     * 관리자 권한 확인
     * @return boolean 관리자 여부
     */
    public static boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(r -> r.toString().equals(Role.ADMIN.getKey()));
    }

}
