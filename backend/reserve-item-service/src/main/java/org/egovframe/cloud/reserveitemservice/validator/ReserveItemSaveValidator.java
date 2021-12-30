package org.egovframe.cloud.reserveitemservice.validator;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import javax.annotation.Resource;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.cloud.common.util.MessageUtil;
import org.egovframe.cloud.reserveitemservice.validator.annotation.ReserveItemSaveValid;
import org.springframework.util.StringUtils;

/**
 * org.egovframe.cloud.reserveitemservice.validator.ReserveItemSaveValidator
 * <p>
 * 예약 물품 저장 시 validation check를 하기 위한 custom validator
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
@Slf4j
public class ReserveItemSaveValidator implements ConstraintValidator<ReserveItemSaveValid, Object> {

    @Resource(
        name = "messageUtil"
    )
    protected MessageUtil messageUtil;

    private String message;
    private boolean fieldValid;

    @Override
    public void initialize(ReserveItemSaveValid constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    /**
     * 예약 물품 저장 시 비지니스 로직에 의한 validation check
     *
     * @param value
     * @param context
     * @return
     */
    @SneakyThrows
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        fieldValid = true;

        // 운영 시작일, 종료일 체크
        validateOperationDate(value, context);

        // 유료인 경우 이용 요금 필수
        validatePaid(value, context);

        String reserveMethodId = String.valueOf(getFieldValue(value, "reserveMethodId"));
        //예약 방법이 '인터넷' 인경우
        if ("internet".equals(reserveMethodId)) {
            return validateInternet(value, context);
        }

        if ("phone".equals(reserveMethodId)) {
            return validateTelephone(value, context);
        }

        if ("visit".equals(reserveMethodId)) {
            return validateVisit(value, context);
        }

        return fieldValid;
    }

    /**
     * 운영 시작일, 종료일 체크
     *
     * @param value
     * @param context
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private void validateOperationDate(Object value, ConstraintValidatorContext context)
        throws NoSuchFieldException, IllegalAccessException {
        LocalDateTime operationStartDate = (LocalDateTime) getFieldValue(value,
            "operationStartDate");
        LocalDateTime operationEndDate = (LocalDateTime) getFieldValue(value, "operationEndDate");

        if (operationStartDate.isAfter(operationEndDate)) {
            context.disableDefaultConstraintViolation();
            //시작일, 종료일, {0}이 {1}보다 늦습니다.
            context.buildConstraintViolationWithTemplate(messageUtil
                .getMessage("valid.to_be_slow.format",
                    new Object[]{messageUtil.getMessage("common.start_date"),
                        messageUtil.getMessage("common.end_date")}))
                .addPropertyNode("operationStartDate")
                .addConstraintViolation();
            fieldValid = false;
        }
    }

    /**
     * 유료인 경우 이용 요금 필수
     *
     * @param value
     * @param context
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private void validatePaid(Object value, ConstraintValidatorContext context)
        throws NoSuchFieldException, IllegalAccessException {
        Boolean isPaid = Boolean.valueOf(String.valueOf(getFieldValue(value, "isPaid")));
        if (isPaid && isNull(value, "usageCost")) {
            context.disableDefaultConstraintViolation();
            //이용요금 값은 필수입니다.
            context.buildConstraintViolationWithTemplate(
                messageUtil.getMessage("reserve_item.usage_fee") + messageUtil
                    .getMessage("valid.required"))
                .addPropertyNode("usageCost")
                .addConstraintViolation();
            fieldValid = false;
        }
    }

    /**
     * 예약 방법인 '방문'인 경우 주소 필수
     *
     * @param value
     * @param context
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private boolean validateVisit(Object value, ConstraintValidatorContext context)
        throws NoSuchFieldException, IllegalAccessException {
        if (isNull(value, "address")) {
            context.disableDefaultConstraintViolation();
            //주소 값은 필수 입니다.
            context.buildConstraintViolationWithTemplate(
                messageUtil.getMessage("common.address") + messageUtil.getMessage("valid.required"))
                .addPropertyNode("address")
                .addConstraintViolation();
            return false;
        }

        return fieldValid;
    }

    /**
     * 예약 방법인 '전화'인 경우 contact 필수
     *
     * @param value
     * @param context
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private boolean validateTelephone(Object value, ConstraintValidatorContext context)
        throws NoSuchFieldException, IllegalAccessException {

        if (isNull(value, "contact")) {
            context.disableDefaultConstraintViolation();
            //문의처 값은 필수입니다.
            context.buildConstraintViolationWithTemplate(
                messageUtil.getMessage("reserve_item.contact") + messageUtil.getMessage("valid.required"))
                .addPropertyNode("contact")
                .addConstraintViolation();
            return false;
        }
        return fieldValid;
    }

    /**
     * 인터넷 예약인 경우 정합성 체크
     *
     * @param value
     * @param context
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private boolean validateInternet(Object value, ConstraintValidatorContext context)
        throws NoSuchFieldException, IllegalAccessException {
        // 예약 구분 필수
        if (isNull(value, "reserveMeansId")) {
            context.disableDefaultConstraintViolation();
            //인터넷 예약 구분 값은 필수 입니다.
            context.buildConstraintViolationWithTemplate(
                messageUtil.getMessage("reserve_item.reserve_means") + messageUtil
                    .getMessage("valid.required"))
                .addPropertyNode("reserveMeansId")
                .addConstraintViolation();
            return false;
        }

        String reserveMeansId = String.valueOf(getFieldValue(value, "reserveMeansId"));
        //예약 구분이 실시간 인 경우
        if ("realtime".equals(reserveMeansId)) {
            return validateRealTime(value, context);
        }

        if ("external".equals(reserveMeansId)) {
            //예약 구분이 외부 링크인 경우 외부 링크 url 필수
            if (isNull(value, "externalUrl")) {
                context.disableDefaultConstraintViolation();
                //외부링크 URL 값은 필수 입니다.
                context.buildConstraintViolationWithTemplate(
                    messageUtil.getMessage("reserve_item.external_url") + messageUtil
                        .getMessage("valid.required"))
                    .addPropertyNode("externalUrl")
                    .addConstraintViolation();
                return false;
            }
        }

        return fieldValid;
    }

    /**
     * 실시간 인 경우 정합성 체크
     *
     * @param value
     * @param context
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private boolean validateRealTime(Object value, ConstraintValidatorContext context)
        throws NoSuchFieldException, IllegalAccessException {
        // 예약 신청 기간 필수
        fieldValid = validateRequestDate(value, context);

        //기간 지정 필수
        fieldValid = validatePeriod(value, context);

        return fieldValid;
    }

    /**
     * 기간 지정 정합성 체크
     *
     * @param value
     * @param context
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private boolean validatePeriod(Object value, ConstraintValidatorContext context)
        throws NoSuchFieldException, IllegalAccessException {
        if (isNull(value, "isPeriod")) {
            context.disableDefaultConstraintViolation();
            //기간 지정 가능 여부 값은 필수 입니다.
            context.buildConstraintViolationWithTemplate(
                messageUtil.getMessage("reserve_item.period_possible") + messageUtil
                    .getMessage("valid.required"))
                .addPropertyNode("requestEndDate")
                .addConstraintViolation();
            return false;
        }

        Boolean isPeriod = Boolean.valueOf(String.valueOf(getFieldValue(value, "isPeriod")));
        // 기간 지정 가능인 경우 최대 얘약일 수 필수
        if (isPeriod && isNull(value, "periodMaxCount")) {
            context.disableDefaultConstraintViolation();
            //최대 예약 가능 일수 값은 필수 입니다.
            context.buildConstraintViolationWithTemplate(
                messageUtil.getMessage("reserve_item.max_period_days") + messageUtil
                    .getMessage("valid.required"))
                .addPropertyNode("periodMaxCount")
                .addConstraintViolation();
            return false;
        }

        return fieldValid;
    }

    /**
     * 예약 신청 일자 정합성 체크
     *
     * @param value
     * @param context
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private boolean validateRequestDate(Object value, ConstraintValidatorContext context)
        throws NoSuchFieldException, IllegalAccessException {

        if (isNull(value, "requestStartDate")) {
            context.disableDefaultConstraintViolation();
            // 예약 신청 시작일 값은 필수 입니다.
            context.buildConstraintViolationWithTemplate(
                messageUtil.getMessage("reserve_item.request") + " " + messageUtil
                    .getMessage("common.start_datetime") + messageUtil.getMessage("valid.required"))
                .addPropertyNode("requestStartDate")
                .addConstraintViolation();

            return false;
        }

        if (isNull(value, "requestEndDate")) {
            context.disableDefaultConstraintViolation();
            // 예약 신청 종료일 값은 필수 입니다.
            context.buildConstraintViolationWithTemplate(
                messageUtil.getMessage("reserve_item.request") + " " + messageUtil
                    .getMessage("common.end_datetime") + messageUtil.getMessage("valid.required"))
                .addPropertyNode("requestEndDate")
                .addConstraintViolation();
            return false;
        }

        LocalDateTime requestStartDate = (LocalDateTime) getFieldValue(value, "requestStartDate");
        LocalDateTime requestEndDate = (LocalDateTime) getFieldValue(value, "requestEndDate");
        if (requestStartDate.isAfter(requestEndDate)) {
            context.disableDefaultConstraintViolation();
            //시작일, 종료일, {0}이 {1}보다 늦습니다.
            context.buildConstraintViolationWithTemplate(messageUtil
                .getMessage("valid.to_be_slow.format",
                    new Object[]{messageUtil.getMessage("common.start_date"),
                        messageUtil.getMessage("common.end_date")}))
                .addPropertyNode("requestStartDate")
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
