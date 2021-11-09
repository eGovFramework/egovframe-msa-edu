package org.egovframe.cloud.servlet.service;

import lombok.extern.slf4j.Slf4j;
import org.egovframe.cloud.servlet.domain.log.ApiLog;
import org.egovframe.cloud.common.service.AbstractService;
import org.egovframe.cloud.servlet.domain.log.ApiLogRepository;
import org.egovframe.cloud.common.util.LogUtil;
import org.egovframe.cloud.servlet.interceptor.ApiLogInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

/**
 * org.egovframe.cloud.servlet.service.ApiLogService
 * <p>
 * API Log 처리 서비스
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/09/01
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/09/01    jaeyeolkim  최초 생성
 * </pre>
 */
@Slf4j
@Service
public class ApiLogService extends AbstractService {

    private final ApiLogRepository apiLogRepository;

    public ApiLogService(ApiLogRepository apiLogRepository) {
        this.apiLogRepository = apiLogRepository;
    }

    /**
     * API log 입력
     * LogInterceptor 에서 호출된다
     *
     * @param request
     * @see ApiLogInterceptor
     */
    @Transactional
    public void saveApiLog(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        apiLogRepository.save(
                ApiLog.builder()
                        .siteId(LogUtil.getSiteId(request))
                        .httpMethod(request.getMethod())
                        .requestUrl(request.getRequestURI())
                        .userId(authentication.getName())
                        .remoteIp(LogUtil.getUserIp())
                        .build()
        );
    }
}
