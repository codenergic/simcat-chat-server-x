package org.codenergic.simcat.chat.core.config;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.codenergic.simcat.Simcat;
import org.codenergic.simcat.chat.core.annotation.WebComponent;
import org.jboss.resteasy.plugins.server.vertx.VertxRequestHandler;
import org.jboss.resteasy.plugins.server.vertx.VertxResteasyDeployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

@Configuration
@Profile({ Simcat.PROFILE_DEVELOPMENT, Simcat.PROFILE_PRODUCTION })
public class VertxWebConfig {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Bean
	@ConfigurationProperties(prefix = "vertx.web.server_options")
	public HttpServerOptions httpServerOptions() {
		return new HttpServerOptions();
	}

	@Bean(destroyMethod = "close")
	public HttpServer httpServer(Vertx vertx, HttpServerOptions httpServerOptions) {
		return vertx.createHttpServer(httpServerOptions);
	}

	@Bean
	@ConfigurationProperties(prefix = "vertx.web.static")
	public StaticHandler staticHandler() {
		return StaticHandler.create();
	}

	@Bean
	public Router staticHandlerRouter(Vertx vertx, StaticHandler staticHandler) {
		Router router = Router.router(vertx);
		router.route().handler(staticHandler);
		return router;
	}

	@Bean
	public VertxResteasyDeployment vertxResteasyDeployment(@WebComponent List<Object> webComponents) {
		VertxResteasyDeployment deployment = new VertxResteasyDeployment();
		deployment.start();
		webComponents.stream()
				.filter(w -> w.getClass().isAnnotationPresent(Path.class))
				.map(w -> {
					logger.info("Registering web component: {}", w.getClass().getName());
					return w;
				})
				.forEach(deployment.getRegistry()::addSingletonResource);
		return deployment;
	}

	@Bean
	public Router restApiRouter(Vertx vertx, VertxResteasyDeployment deployment) {
		Router router = Router.router(vertx);
		VertxRequestHandler restHandler = new VertxRequestHandler(vertx, deployment);
		router.route().handler(r -> restHandler.handle(r.request()));
		return router;
	}

	@Bean
	public Handler<HttpServerRequest> httpServerRequestHandler(Vertx vertx,
			@Qualifier("staticHandlerRouter") Router staticRouter, @Qualifier("restApiRouter") Router restRouter,
			@Qualifier("eventBusApiRouter") Router eventBusRouter) {
		Router router = Router.router(vertx);
		router.mountSubRouter("/api", restRouter);
		router.mountSubRouter("/bus", eventBusRouter);
		router.mountSubRouter("/", staticRouter);
		return router::accept;
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

	@Bean
	@WebComponent
	public RootApi rootApi() {
		return new RootApi();
	}

	@Path("/api")
	public static class RootApi {
		@GET
		public String root() {
			return "This is the root of the API";
		}
	}
}
