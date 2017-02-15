package org.codenergic.simcat.chat.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

@Configuration
public class SockJSConfig {
	@Bean
	@ConfigurationProperties(prefix = "vertx.sockjs.bridge_options")
	public BridgeOptions sockJSBridgeOption() {
		return new BridgeOptions();
	}

	@Bean
	public SockJSHandler sockJSHandler(Vertx vertx, BridgeOptions bridgeOptions) {
		return SockJSHandler.create(vertx).bridge(bridgeOptions);
	}

	@Autowired
	public void configureSockJSHandler(Router router, SockJSHandler sockJSHandler) {
		router.route("/api/bus").handler(sockJSHandler);
	}
}
