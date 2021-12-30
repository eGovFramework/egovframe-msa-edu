package org.egovframe.cloud.reserveitemservice.validator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import org.egovframe.cloud.reserveitemservice.validator.ReserveItemSaveValidator;

/**
 * org.egovframe.cloud.reserveitemservice.validator.annotation.ReserveItemSaveValid
 *
 * 예약 물품 저장 시 validation check를 하기 위한 custom annotation
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/09/13
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/13    shinmj       최초 생성
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReserveItemSaveValidator.class)
public @interface ReserveItemSaveValid {

    String message() default "저장할 수 없습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
