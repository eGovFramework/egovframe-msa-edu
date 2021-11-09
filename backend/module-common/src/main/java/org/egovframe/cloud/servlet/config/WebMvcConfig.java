package org.egovframe.cloud.servlet.config;

import org.egovframe.cloud.servlet.interceptor.ApiLogInterceptor;
import org.egovframe.cloud.servlet.service.ApiLogService;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * org.egovframe.cloud.userservice.domain.BaseTimeEntity
 * <p>
 * WebMvc 관련 Configuration 클래스
 *
 * @author 표준프레임워크센터 jaeyeolkim
 * @version 1.0
 * @since 2021/07/05
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/05    jaeyeolkim  최초 생성
 * </pre>
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final ApiLogService apiLogService;

    public WebMvcConfig(ApiLogService apiLogService) {
        this.apiLogService = apiLogService;
    }

    /**
     * LocalValidatorFactoryBean 에 messageSource Bean 주입하여 validtor 메시지를 지원한다.
     *
     * @param messageSource messages.properties
     * @return LocalValidatorFactoryBean
     */
    @Bean
    public LocalValidatorFactoryBean validator(MessageSource messageSource) {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource);
        return bean;
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(new ApiLogInterceptor(apiLogService))
                .excludePathPatterns(
                        "/",
                        "/error",
                        "/favicon.ico",
                        "/api/v1/authorizations/check",
                        "/api/v1/users/token/refresh",
                        "/api/v1/menu-roles/**",
                        "/api/v1/messages/**"
                );
    }
}
