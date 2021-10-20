package org.egovframe.cloud.apigateway.config;

import lombok.AllArgsConstructor;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.support.NameUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * org.egovframe.cloud.apigateway.config.SwaggerProvider
 *
 * Swagger API Doc aggregator class
 * Swagger Resource인 api-docs를 가져오는 provider
 *
 * @author 표준프레임워크센터 shinmj
 * @version 1.0
 * @since 2021/07/07
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일        수정자           수정내용
 *  ----------    --------    ---------------------------
 *  2021/07/07    shinmj       최초 생성
 * </pre>
 */
@AllArgsConstructor
@Component
@Primary
public class SwaggerProvider implements SwaggerResourcesProvider {

    public static final String API_URL = "/v2/api-docs";
    public static final String WEBFLUX_API_URL = "/v3/api-docs";
    private final RouteLocator routeLocator;
    private final GatewayProperties gatewayProperties;

    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>();
        List<String> routes = new ArrayList<>();

        routeLocator.getRoutes().subscribe(route -> routes.add(route.getId()));

        gatewayProperties.getRoutes().stream()
                .filter(routeDefinition -> routes.contains(routeDefinition.getId()))
                .forEach(routeDefinition -> routeDefinition.getPredicates().stream()
                        .filter(predicateDefinition -> ("Path").equalsIgnoreCase(predicateDefinition.getName()))
                        .forEach(predicateDefinition ->
                                resources.add(
                                        swaggerResource(routeDefinition.getId(),
                                                predicateDefinition.
                                                        getArgs().
                                                        get(NameUtils.GENERATED_NAME_PREFIX+"0").
                                                        replace("/**", API_URL))))
                );

        return resources;
    }

    private SwaggerResource swaggerResource(String name, String location) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        if (name.contains("reserve")) {
            swaggerResource.setLocation(location.replace(API_URL, WEBFLUX_API_URL));
        }else {
            swaggerResource.setLocation(location);
        }

        swaggerResource.setSwaggerVersion("2.0");
        return swaggerResource;
    }
}
