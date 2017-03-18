package org.codenergic.simcat.chat.core.annotation;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public class RouterAnnotationConfiguration {
	@Bean
	public BeanPostProcessor routerAnnotationProcessor(ConfigurableListableBeanFactory beanFactory,
			@Qualifier("routerAnnotationRootRouter") Router rootRouter) {
		return new RouterAnnotationProcessor(beanFactory, rootRouter);
	}

	@Bean
	public Router routerAnnotationRootRouter(Vertx vertx) {
		return Router.router(vertx);
	}
}
