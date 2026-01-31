package com.alessandragodoy.transactionms.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

/**
 * Configuration class for WebClient.
 * This class configures the WebClient bean with the base URL for the account microservice.
 */
@Configuration
public class WebClientConfig {

	@Value("${account.ms.url}")
	private String accountMsUrl;

	@Bean
	public WebClient webClient(WebClient.Builder builder) {
		return builder.baseUrl(accountMsUrl)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.clientConnector(new ReactorClientHttpConnector(HttpClient.create()
						.responseTimeout(Duration.ofSeconds(5))))
				.build();
	}
}
