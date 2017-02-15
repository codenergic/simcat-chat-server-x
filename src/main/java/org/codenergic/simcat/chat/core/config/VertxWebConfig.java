package org.codenergic.simcat.chat.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

@Configuration
public class VertxWebConfig {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Bean
	@ConfigurationProperties(prefix = "vertx.web.server_options")
	public HttpServerOptions httpServerOptions() {
		return new HttpServerOptions();
	}

	@Bean
	public HttpServer httpServer(Vertx vertx, HttpServerOptions httpServerOptions) {
		return vertx.createHttpServer(httpServerOptions);
	}

	@Bean
	@ConfigurationProperties(prefix = "vertx.web.static")
	public StaticHandler staticHandler() {
		return StaticHandler.create();
	}

	@Bean
	public Router httpRouter(Vertx vertx, StaticHandler staticHandler) {
		return Router.router(vertx);
	}

	@Bean
	public Handler<HttpServerRequest> httpServerRequestHandler(Router router) {
		return router::accept;
	}

	@Autowired
	public void configureStaticHandler(Router router, StaticHandler staticHandler) {
		router.route().handler(staticHandler);
	}

	@Autowired
	public void configureAndStartHttpServer(HttpServer httpServer, Handler<HttpServerRequest> httpServerRequestHandler) {
		logger.info("Starting HTTP server");
		httpServer.requestHandler(httpServerRequestHandler).listen(h -> {
			if (h.succeeded()) {
				logger.info("HTTP server started at {}", h.result().actualPort());
			} else {
				logger.error("Failed to start HTTP server", h.cause());
			}
		});
	}
}
