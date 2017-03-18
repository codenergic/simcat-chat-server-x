package org.codenergic.simcat.chat.core.annotation;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.ReflectionUtils;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class RouterAnnotationProcessor extends AbstractMethodAnnotationProcessor {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private Vertx vertx;
	private io.vertx.ext.web.Router rootRouter;
	private Map<String, io.vertx.ext.web.Router> routers = new HashMap<>();

	public RouterAnnotationProcessor(ConfigurableListableBeanFactory beanFactory, io.vertx.ext.web.Router rootRouter) {
		super(beanFactory);
		this.vertx = beanFactory.getBean(Vertx.class);
		this.rootRouter = rootRouter;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) {
		if (bean.getClass().isAnnotationPresent(Router.class)) {
			logger.info("Found router for class {}", bean.getClass().getName());
			io.vertx.ext.web.Router webRouter = io.vertx.ext.web.Router.router(vertx);
			routers.put(beanName, webRouter);
			rootRouter.mountSubRouter("/", webRouter);
			return super.postProcessBeforeInitialization(bean, beanName);
		}
		return super.postProcessBeforeInitialization(bean, beanName);
	}

	@Override
	protected void doWithAnnotatedMethod(Object bean, String beanName, Method method, Object[] parameterInstances) {
		io.vertx.ext.web.Router webRouter = routers.get(beanName);
		io.vertx.ext.web.Route webRoute;
		Route route;
		if (method.isAnnotationPresent(Route.class)) {
			route = method.getAnnotation(Route.class);
			webRoute = webRouter.route(route.path());
		} else {
			route = method.getAnnotation(RouteWithRegex.class).value();
			webRoute = webRouter.routeWithRegex(route.path());
		}
		if (route.method().length > 0) {
			for (HttpMethod httpMethod : route.method())
				webRoute.method(httpMethod);
		}
		if (route.handleBody()) {
			webRouter.route(route.path()).handler(BodyHandler.create());
		}
		if (route.blocking()) {
			webRoute.blockingHandler(h -> invokeMethod(bean, method, parameterInstances, h));
		} else {
			webRoute.handler(h -> invokeMethod(bean, method, parameterInstances, h));
		}
	}

	private void invokeMethod(Object bean, Method method, Object[] parameterInstances, RoutingContext routing) {
		ReflectionUtils.invokeMethod(method, bean, replaceParameterInstances(method, parameterInstances, routing));
	}

	@Override
	protected boolean filterMethod(Method method) {
		return method.isAnnotationPresent(Route.class) || method.isAnnotationPresent(RouteWithRegex.class);
	}
}
