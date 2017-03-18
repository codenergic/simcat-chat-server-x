package org.codenergic.simcat.chat.core.annotation;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;

public class EventBusComponentAnnotationConfiguration {
	@Bean
	public BeanPostProcessor eventBusComponentAnnotationProcessor(ConfigurableListableBeanFactory beanFactory) {
		return new EventBusComponentAnnotationProcessor(beanFactory);
	}
}
