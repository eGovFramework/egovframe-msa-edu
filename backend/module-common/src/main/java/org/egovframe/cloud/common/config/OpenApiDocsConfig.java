package org.egovframe.cloud.common.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiDocsConfig {

	@Value("${spring.application.name}")
	private String appName;

	/**
	 * @TODO
	 * api info update 필요
	 *
	 */
	@Bean
	public OpenAPI customOpenAPI() {
		Server server = new Server();
		server.url("/"+appName);
		List<Server> servers = new ArrayList<>();
		servers.add(server);
		return new OpenAPI()
			.components(new Components())
			.servers(servers)
			.info(new io.swagger.v3.oas.models.info.Info().title(appName+" API"));
	}
}
