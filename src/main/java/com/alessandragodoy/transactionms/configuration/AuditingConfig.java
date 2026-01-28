package com.alessandragodoy.transactionms.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(basePackages = "com.alessandragodoy.transactionms.repository")
@EnableReactiveMongoAuditing
public class AuditingConfig {
}
