package com.alessandragodoy.transactionms.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for creating a {@link ModelMapper} bean.
 * This class provides a default configuration for mapping objects.
 */
@Configuration
public class MapperConfig {

	@Bean
	public ModelMapper defaultModelMapper() {
		return new ModelMapper();
	}
}
