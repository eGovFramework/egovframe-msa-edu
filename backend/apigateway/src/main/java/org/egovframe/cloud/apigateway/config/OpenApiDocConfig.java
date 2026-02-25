package org.egovframe.cloud.apigateway.config;

import java.util.List;

import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

import lombok.extern.slf4j.Slf4j;

/**
 * OpenAPI 3.0 설정
 * 
 * 라우트 기반으로 동적 그룹/URL 구성을 유지하여 API Gateway를 통해서만 Swagger UI에 접근
 */
@Configuration
@Slf4j
public class OpenApiDocConfig implements ApplicationListener<ApplicationReadyEvent> {

	private static final String SERVICE_SUFFIX = "-service";
	private static final String API_DOCS_PATH = "/v3/api-docs";

	private final ApplicationContext applicationContext;
	private final RouteDefinitionLocator routeDefinitionLocator;

	public OpenApiDocConfig(ApplicationContext applicationContext, RouteDefinitionLocator routeDefinitionLocator) {
		this.applicationContext = applicationContext;
		this.routeDefinitionLocator = routeDefinitionLocator;
	}

	@Override
	public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
		log.info("Starting Swagger UI configuration based on Gateway routes");
		
		// SwaggerUiConfigParameters 빈이 존재하는지 확인 후 사용
		try {
			SwaggerUiConfigParameters swaggerUiConfigParameters = applicationContext.getBean(SwaggerUiConfigParameters.class);
			
			// 모든 라우트를 수집한 후 처리
			List<RouteDefinition> routes = routeDefinitionLocator.getRouteDefinitions()
					.filter(route -> route.getId().endsWith(SERVICE_SUFFIX))
					.collectList()
					.block();
			
			if (routes != null && !routes.isEmpty()) {

				// 기존 정적 설정 제거 (동적 설정으로 대체)
				if (swaggerUiConfigParameters.getUrls() != null) {
					swaggerUiConfigParameters.getUrls().clear();
				}
				
				routes.forEach(route -> {
					String serviceName = route.getId();
					String apiDocsUrl = "/" + serviceName + API_DOCS_PATH;
					swaggerUiConfigParameters.addGroup(serviceName);
					swaggerUiConfigParameters.addUrl(apiDocsUrl);
				});

			} else {
				log.warn("No routes found matching suffix: {}", SERVICE_SUFFIX);
			}
		} catch (Exception e) {
			log.warn("Swagger UI configuration skipped. {}", e.getMessage());
		}
	}

}
