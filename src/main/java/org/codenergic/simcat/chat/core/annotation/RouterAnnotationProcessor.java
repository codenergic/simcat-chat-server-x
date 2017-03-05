package org.codenergic.simcat.chat.core.annotation;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.ReflectionUtils;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

public class RouterAnnotationProcessor extends AbstractMethodAnnotationProcessor {
	private Vertx vertx;
	private Map<String, io.vertx.ext.web.Router> routers = new HashMap<>();

	public RouterAnnotationProcessor(ConfigurableListableBeanFactory beanFactory) {
		super(beanFactory);
		this.vertx = beanFactory.getBean(Vertx.class);
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) {
		if (bean.getClass().isAnnotationPresent(Router.class)) {
			io.vertx.ext.web.Router webRouter = io.vertx.ext.web.Router.router(vertx);
			beanFactory.registerSingleton(beanName + "$Router", webRouter);
			routers.put(beanName, webRouter);
			return super.postProcessAfterInitialization(bean, beanName);
		}
		return bean;
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
