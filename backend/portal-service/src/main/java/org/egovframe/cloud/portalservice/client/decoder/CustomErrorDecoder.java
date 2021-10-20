package org.egovframe.cloud.portalservice.client.decoder;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.cloud.common.exception.BusinessException;
import org.egovframe.cloud.common.exception.BusinessMessageException;
import org.egovframe.cloud.common.exception.dto.ErrorCode;

/**
 * org.egovframe.cloud.portalservice.client.decoder.CustomErrorDecoder
 * <p>
 *  feign client custom 에러 핸들링 클래스
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/08/23
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/08/23    shinmj  최초 생성
 * </pre>
 */
@Slf4j
public class CustomErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        log.info("%s 요청이 성공하지 못했습니다. status : %s, body : %s",
                methodKey, response.status(), response.body());

        switch (response.status()) {
            case 400:
                return new BusinessMessageException(response.body().toString());
            case 401:
                return new BusinessException(ErrorCode.UNAUTHORIZED);
            case 403:
                return new BusinessException(ErrorCode.JWT_EXPIRED);
            case 404:
                return new BusinessException(ErrorCode.NOT_FOUND);
            case 405:
                return new BusinessException(ErrorCode.METHOD_NOT_ALLOWED);
            case 422:
                return new BusinessException(ErrorCode.UNPROCESSABLE_ENTITY);
            default:
                return new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
