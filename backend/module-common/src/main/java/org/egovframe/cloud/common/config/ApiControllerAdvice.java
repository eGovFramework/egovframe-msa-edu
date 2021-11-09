package org.egovframe.cloud.common.config;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * org.egovframe.cloud.common.config.WebControllerAdvice
 *
 * 모든 컨트롤러에 적용되는 컨트롤러 어드바이스 클래스
 * 예외 처리 (@ExceptionHandler), 바인딩 설정(@InitBinder), 모델 객체(@ModelAttributes)
 *
 * @author 표준프레임워크센터 jooho
 * @version 1.0
 * @since 2021/07/12
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/12    jooho       최초 생성
 * </pre>
 */
@ControllerAdvice
public class ApiControllerAdvice {

    /**
     * 모든 컨트롤러로 들어오는 요청 초기화
     *
     * @param binder 웹 데이터 바인더
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.initDirectFieldAccess(); // Setter 구현 없이 DTO 클래스 필드에 접근
    }

}
