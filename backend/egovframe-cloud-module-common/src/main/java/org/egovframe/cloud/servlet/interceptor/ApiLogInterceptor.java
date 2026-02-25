package org.egovframe.cloud.servlet.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.cloud.servlet.service.ApiLogService;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
public class ApiLogInterceptor implements HandlerInterceptor {

    private final ApiLogService apiLogService;

    public ApiLogInterceptor(ApiLogService apiLogService) {
        this.apiLogService = apiLogService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        apiLogService.saveApiLog(request);
        log.info("[ApiLogInterceptor preHandle] {}, {}, {}", request.getMethod(), request.getRequestURI(), response.getStatus());
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("[ApiLogInterceptor postHandle] {}, {}, {}", request.getMethod(), request.getRequestURI(), response.getStatus());
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

}
