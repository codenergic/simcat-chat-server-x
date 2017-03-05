package org.codenergic.simcat.chat.core.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;

@Configuration
@Profile({ "test", "webtest" })
public class VertxWebTestConfig {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Bean(destroyMethod = "close")
	public HttpServer httpServer(Vertx vertx) {
		return vertx.createHttpServer();
	}

	@Bean
	public Handler<HttpServerRequest> httpServerRequestHandler(Vertx vertx, List<Router> routers) {
		logger.info("Routers: {}", routers);
		Router router = Router.router(vertx);
		routers.forEach(r -> router.mountSubRouter("/", r));
		return router::accept;
	}

	@Autowired
	public void configureAndStartHttpServer(HttpServer httpServer, Handler<HttpServerRequest> httpServerRequestHandler) {
		logger.info("Starting HTTP server");
		httpServer.requestHandler(httpServerRequestHandler).listen(8080, h -> {
			if (h.succeeded()) {
				logger.info("HTTP server started at {}", h.result().actualPort());
			}
		});
	}
}
