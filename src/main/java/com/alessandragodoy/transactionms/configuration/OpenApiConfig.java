package com.alessandragodoy.transactionms.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration class for OpenAPI documentation.
 * This class sets up the OpenAPI definition for the Transaction Microservice.
 */
@Configuration
public class OpenApiConfig {

	@Value("${spring.profiles.active}")
	private String activeProfile;

	@Value("${server.port}")
	private String serverPort;

	@Bean
	public OpenAPI customOpenAPI() {
		List<Server> servers = new ArrayList<>();

		if ("dev".equals(activeProfile)) {
			servers.add(new Server()
					.url("http://localhost:" + serverPort)
					.description("Local development server"));
		}

		servers.add(new Server()
				.url("https://transactions.alessandragodoy.com")
				.description("Production server"));

		return new OpenAPI()
				.info(new Info()
						.title("Banking System - Transaction Microservice API")
						.version("1.2.0")
						.description("""
								Reactive API for managing banking transactions.
								This API provides endpoints for:
								- Retrieving transaction history
								- Getting transaction details
								- Registering transfer transactions
								- Querying transactions by account
								""")
						.contact(new Contact().name("Alessandra Godoy")
								.email("api@alessandragodoy.com")))
				.servers(servers);
	}
}

