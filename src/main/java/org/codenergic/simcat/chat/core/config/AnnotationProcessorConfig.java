package org.codenergic.simcat.chat.core.config;

import org.codenergic.simcat.chat.core.annotation.EventBusMappingAnnotationProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnnotationProcessorConfig {
	@Bean
	public EventBusMappingAnnotationProcessor annotationProcessor(ConfigurableListableBeanFactory beanFactory) {
		return new EventBusMappingAnnotationProcessor(beanFactory);
	}
}