package org.codenergic.simcat.chat.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.vertx.core.Vertx;

@Configuration
@Profile("test")
public class VertxTestConfig {
	@Bean
	public Vertx vertx() {
		return Vertx.vertx();
	}
}
