package com.alessandragodoy.transactionms.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * Web configuration for CORS (Cross-Origin Resource Sharing).
 * Allows Swagger UI and external clients to access the API.
 */
@Configuration
public class WebConfig implements WebFluxConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins(
						"https://transactions.alessandragodoy.com",
						"https://customers.alessandragodoy.com",
						"https://accounts.alessandragodoy.com",
						"http://localhost:8087"
				)
				.allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
				.allowedHeaders("*")
				.allowCredentials(true)
				.maxAge(3600);
	}
}

