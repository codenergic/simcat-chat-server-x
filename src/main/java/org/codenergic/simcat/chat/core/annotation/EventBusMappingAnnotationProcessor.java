package org.codenergic.simcat.chat.core.annotation;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.ReflectionUtils;

import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;

public class EventBusMappingAnnotationProcessor implements BeanPostProcessor {
	private ConfigurableListableBeanFactory beanFactory;
	private EventBus eventBus;

	public EventBusMappingAnnotationProcessor(ConfigurableListableBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
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

		final Object[] methodParameterInstances = getParameterInstances(method);
		final List<Integer> messageIndexes = getMessageParameterIndexes(method);
		final boolean voidMethod = method.getReturnType().equals(Void.TYPE);

		eventBus.consumer(busAddress, m -> {
			Object result = ReflectionUtils
					.invokeMethod(method, bean, replaceMessage(methodParameterInstances, messageIndexes, m));
			if (!voidMethod) {
				m.reply(result);
			}
		});
	}

	private boolean filterAnnotation(Method method) {
		return method.isAnnotationPresent(EventBusMapping.class);
	}

	private List<Integer> getMessageParameterIndexes(Method method) {
		List<Integer> messageIndexes = new ArrayList<>();
		int i = 0;
		for (Parameter parameter : method.getParameters()) {
			if (parameter.getType().isAssignableFrom(Message.class)) {
				messageIndexes.add(i);
			}
			i++;
		}
		return messageIndexes;
	}

	private Object[] getParameterInstances(Method method) {
		List<Object> parameterInstances = new ArrayList<>(method.getParameterCount());
		for (Parameter parameter : method.getParameters()) {
			parameterInstances.add(getParameterInstance(parameter));
		}
		return parameterInstances.toArray();
	}

	private Object getParameterInstance(Parameter parameter) {
		if (parameter.getType().isAssignableFrom(Message.class)) {
			return null;
		}

		if (parameter.isAnnotationPresent(Qualifier.class)) {
			Qualifier qualifier = parameter.getDeclaredAnnotation(Qualifier.class);
			return BeanFactoryAnnotationUtils.qualifiedBeanOfType(beanFactory, parameter.getType(), qualifier.value());
		} else {
			return beanFactory.getBean(parameter.getType());
		}
	}

	private Object[] replaceMessage(Object[] parameterInstances, List<Integer> messageIndexes, Message<Object> message) {
		Object[] instances = Arrays.copyOf(parameterInstances, parameterInstances.length);
		for (int idx : messageIndexes) {
			instances[idx] = message;
		}
		return instances;
	}
}
