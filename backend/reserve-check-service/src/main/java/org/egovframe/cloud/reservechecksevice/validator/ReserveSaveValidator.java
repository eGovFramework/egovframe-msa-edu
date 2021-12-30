package org.egovframe.cloud.reservechecksevice.validator;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import javax.annotation.Resource;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.cloud.common.util.MessageUtil;
import org.egovframe.cloud.reservechecksevice.validator.annotation.ReserveSaveValid;
import org.springframework.util.StringUtils;

/**
 * org.egovframe.cloud.reservechecksevice.validator.ReserveSaveValidator
 * <p>
 * 예약 신청 시 validation check를 하기 위한 custom validator
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/09/23
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/23    shinmj       최초 생성
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
public class ReserveSaveValidator implements ConstraintValidator<ReserveSaveValid, Object> {

    @Resource(
        name = "messageUtil"
    )
    protected MessageUtil messageUtil;

    private String message;
    private boolean fieldValid;


    @Override
    public void initialize(ReserveSaveValid constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    /**
     * 예약 신청 시 비지니스 로직에 의한 validation check
     *
     * @param value
     * @param context
     * @return
     */
    @SneakyThrows
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        fieldValid = true;

        String categoryId = String.valueOf(getFieldValue(value, "categoryId"));
        if ("education".equals(categoryId)) {
            //교육인 경우
            //신청인원
            return checkReserveQty(value, context);
        }

        if ("equipment".equals(categoryId)) {
            //장비인 경우
            //신청일자(기간), 신청수량
            fieldValid = checkReserveDate(value, context);
            fieldValid = checkReserveQty(value, context);

            return fieldValid;
        }

        if ("place".equals(categoryId)) {
            //공간인 경우
            //신청일자(기간)
            return checkReserveDate(value, context);
        }

        return fieldValid;
    }

    /**
     * 예약 수량 체크
     *
     * @param value
     * @param context
     * @return
     */
    @SneakyThrows
    private boolean checkReserveQty(Object value, ConstraintValidatorContext context) {
        if (isNull(value, "reserveQty")) {
            context.disableDefaultConstraintViolation();
            //예약 수량 값은 필수 입니다.
            context.buildConstraintViolationWithTemplate(
                messageUtil.getMessage("reserve") + " " + messageUtil.getMessage("reserve.count")
                    + messageUtil.getMessage("valid.required"))
                .addPropertyNode("reserveQty")
                .addConstraintViolation();
            return false;
        }
        return fieldValid;
    }

    /**
     * 예약 신청 기간 체크
     *
     * @param value
     * @param context
     * @return
     */
    @SneakyThrows
    private boolean checkReserveDate(Object value, ConstraintValidatorContext context) {
        // 예약 신청 기간 필수
        if (isNull(value, "reserveStartDate")) {
            context.disableDefaultConstraintViolation();
            // 예약 신청 시작일 값은 필수 입니다.
            context.buildConstraintViolationWithTemplate(
                messageUtil.getMessage("reserve_item.request") + " " + messageUtil
                    .getMessage("common.start_datetime") + messageUtil.getMessage("valid.required"))
                .addPropertyNode("reserveStartDate")
                .addConstraintViolation();
            return false;
        }

        if (isNull(value, "reserveEndDate")) {
            context.disableDefaultConstraintViolation();
            // 예약 신청 종료일 값은 필수 입니다.
            context.buildConstraintViolationWithTemplate(
                messageUtil.getMessage("reserve_item.request") + " " + messageUtil
                    .getMessage("common.end_datetime") + messageUtil.getMessage("valid.required"))
                .addPropertyNode("reserveEndDate")
                .addConstraintViolation();
            return false;
        }

        // 예약 시작일, 종료일 체크
        LocalDateTime reserveStartDate = (LocalDateTime) getFieldValue(value, "reserveStartDate");
        LocalDateTime reserveEndDate = (LocalDateTime) getFieldValue(value, "reserveEndDate");
        if (reserveStartDate.isAfter(reserveEndDate)) {
            context.disableDefaultConstraintViolation();
            //시작일, 종료일, {0}이 {1}보다 늦습니다.
            context.buildConstraintViolationWithTemplate(messageUtil
                .getMessage("valid.to_be_slow.format",
                    new Object[]{messageUtil.getMessage("common.start_date"),
                        messageUtil.getMessage("common.end_date")}))
                .addPropertyNode("reserveStartDate")
                .addConstraintViolation();
            return false;
        }

        return fieldValid;
    }

    /**
     * 해당하는 field의 값 조회
     *
     * @param object
     * @param fieldName
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private Object getFieldValue(Object object, String fieldName)
        throws NoSuchFieldException, IllegalAccessException {
        Class<?> clazz = object.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }

    /**
     * 해당하는 Field가 null인지 체크
     *
     * @param object
     * @param fieldName
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private boolean isNull(Object object, String fieldName)
        throws NoSuchFieldException, IllegalAccessException {
        Class<?> clazz = object.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object) == null || !StringUtils
            .hasLength(String.valueOf(field.get(object)));
    }
}
