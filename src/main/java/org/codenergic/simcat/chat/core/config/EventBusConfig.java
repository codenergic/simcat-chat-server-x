package org.codenergic.simcat.chat.core.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.codenergic.simcat.chat.core.eventbus.FSTCodec;
import org.nustaq.serialization.FSTConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageCodec;

@Configuration
public class EventBusConfig {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Bean
	public EventBus eventBus(Vertx vertx, @Autowired(required = false) List<MessageCodec<?, ?>> messageCodecs) {
		logger.info("Configuring clustered event bus");
		EventBus eventBus = vertx.eventBus();

		Optional.ofNullable(messageCodecs)
				.orElse(new ArrayList<>())
				.forEach(eventBus::registerCodec);

		return eventBus;
	}

	@Bean
	public FSTCodec fstCodec(FSTConfiguration fstConfiguration) {
		return new FSTCodec(fstConfiguration);
	}
}