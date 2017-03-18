package org.codenergic.simcat.chat.core.annotation;

import java.lang.reflect.Method;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.ReflectionUtils;

import io.vertx.core.eventbus.EventBus;

public class EventBusMappingAnnotationProcessor extends AbstractMethodAnnotationProcessor {
	private EventBus eventBus;

	public EventBusMappingAnnotationProcessor(ConfigurableListableBeanFactory beanFactory) {
		super(beanFactory);
		this.eventBus = beanFactory.getBean(EventBus.class);
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) {
		if (bean.getClass().isAnnotationPresent(EventBusComponent.class)) {
			return super.postProcessAfterInitialization(bean, beanName);
		}
		return bean;
	}

	@Override
	protected void doWithAnnotatedMethod(Object bean, String beanName, Method method, Object[] parameterInstances) {
		String busAddress = method.getDeclaredAnnotation(EventBusMapping.class).value();
		final boolean voidMethod = method.getReturnType().equals(Void.TYPE);

		eventBus.consumer(busAddress, m -> {
			Object result = ReflectionUtils
					.invokeMethod(method, bean, replaceParameterInstances(method, parameterInstances, m));
			if (!voidMethod) {
				m.reply(result);
			}
		});
	}

	@Override
	protected boolean filterMethod(Method method) {
		return method.isAnnotationPresent(EventBusMapping.class);
	}
}
