package org.codenergic.simcat.chat.core.annotation;

import java.lang.reflect.Method;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.ReflectionUtils;

import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;

public class EventBusMappingAnnotationProcessor implements BeanPostProcessor {
	private EventBus eventBus;

	public EventBusMappingAnnotationProcessor(ConfigurableListableBeanFactory beanFactory) {
		this.eventBus = beanFactory.getBean(EventBus.class);
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		configureAnnotatedMethod(bean);
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	private void configureAnnotatedMethod(Object bean) {
		ReflectionUtils.doWithMethods(bean.getClass(), method -> doWithAnnotatedMethod(bean, method),
				this::filterAnnotation);
	}

	private void doWithAnnotatedMethod(Object bean, Method method) {
		String busAddress = method.getDeclaredAnnotation(EventBusMapping.class).value();
		eventBus.consumer(busAddress, m -> ReflectionUtils.invokeMethod(method, bean, m));
	}

	private boolean filterAnnotation(Method method) {
		if (method.getParameterCount() != 1) return false;

		return method.isAnnotationPresent(EventBusMapping.class) &&
				method.getParameterTypes()[0].isAssignableFrom(Message.class);
	}
}
