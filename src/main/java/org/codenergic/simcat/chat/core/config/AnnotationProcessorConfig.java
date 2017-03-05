package org.codenergic.simcat.chat.core.config;

import org.codenergic.simcat.chat.core.annotation.EventBusMappingAnnotationProcessor;
import org.codenergic.simcat.chat.core.annotation.RouterAnnotationProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnnotationProcessorConfig {
	@Bean
	public BeanPostProcessor eventBusMappingAnnotationProcessor(ConfigurableListableBeanFactory beanFactory) {
		return new EventBusMappingAnnotationProcessor(beanFactory);
	}

	@Bean
	public BeanPostProcessor routerAnnotationProcessor(ConfigurableListableBeanFactory beanFactory) {
		return new RouterAnnotationProcessor(beanFactory);
	}
}