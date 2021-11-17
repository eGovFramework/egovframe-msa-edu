package org.egovframe.cloud.apigateway.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.SwaggerUiConfigParameters;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class OpenApiDocConfig {

	@Bean
	@Lazy(false)
	public List<GroupedOpenApi> apis(SwaggerUiConfigParameters swaggerUiConfigParameters, RouteDefinitionLocator locator) {
		List<GroupedOpenApi> groups = new ArrayList<>();

		List<RouteDefinition> definitions = locator.getRouteDefinitions().log("OpenApiDocConfig").collectList().block();

		Optional.ofNullable(definitions)
			.map(Collection::stream)
			.orElseGet(Stream::empty)
			.filter(routeDefinition -> routeDefinition.getId().matches(".*-service"))
			.forEach(routeDefinition -> {
				String name = routeDefinition.getId();
				swaggerUiConfigParameters.addGroup(name);
				GroupedOpenApi.builder().pathsToMatch("/" + name + "/**").group(name).build();
			});
		return groups;
	}
}
