package org.codenergic.simcat.chat.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

@Configuration
public class SockJSConfig {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Bean
	@ConfigurationProperties(prefix = "vertx.sockjs.bridge_options")
	public BridgeOptions sockJSBridgeOption() {
		return new BridgeOptions();
	}

	@Bean
	public SockJSHandler sockJSHandler(Vertx vertx, BridgeOptions bridgeOptions) {
		logger.info("Configuring SockJS handler");
		logger.info("SockJS inbound permitted: {}", Json.encode(bridgeOptions.getInboundPermitteds()));
		logger.info("SockJS outbound permitted: {}", Json.encode(bridgeOptions.getOutboundPermitteds()));
		return SockJSHandler.create(vertx).bridge(bridgeOptions);
	}

	@Bean
	public Router eventBusApiRouter(Vertx vertx, SockJSHandler sockJSHandler) {
		logger.info("Registering EventBus API");
		Router router = Router.router(vertx);
		router.route("/api/bus/*").handler(sockJSHandler);
		return router;
	}
}
