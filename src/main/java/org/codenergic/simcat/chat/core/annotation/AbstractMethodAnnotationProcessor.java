package org.codenergic.simcat.chat.core.annotation;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.ReflectionUtils;

public abstract class AbstractMethodAnnotationProcessor implements BeanPostProcessor {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	protected ConfigurableListableBeanFactory beanFactory;

	public AbstractMethodAnnotationProcessor(ConfigurableListableBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) {
		ReflectionUtils.doWithMethods(bean.getClass(),
				method -> doWithAnnotatedMethod(bean, beanName, method, getParameterInstances(method)), this::filterMethod);
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) {
		return bean;
	}

	protected abstract void doWithAnnotatedMethod(Object bean, String beanName, Method method, Object[] parameterInstances);

	protected abstract boolean filterMethod(Method method);

	protected Object[] replaceParameterInstances(Method method, Object[] parameterInstances, Object... newInstances) {
		if (newInstances.length == 0)
			return parameterInstances;
		Object[] instances = Arrays.copyOf(parameterInstances, parameterInstances.length);
		int i = 0;
		for (Class<?> parameterType : method.getParameterTypes()) {
			for (Object newInstance : newInstances) {
				if (parameterType.isInstance(newInstance)) {
					instances[i] = newInstance;
					continue;
				}
			}
			i++;
		}
		return instances;
	}

	private Object[] getParameterInstances(Method method) {
		return Arrays.asList(method.getParameters()).stream()
				.map(this::getParameterInstance)
				.collect(Collectors.toList())
				.toArray();
	}

	private Object getParameterInstance(Parameter parameter) {
		Class<?> parameterType = parameter.getType();
		try {
			if (parameter.isAnnotationPresent(Qualifier.class)) {
				Qualifier qualifier = parameter.getDeclaredAnnotation(Qualifier.class);
				return BeanFactoryAnnotationUtils.qualifiedBeanOfType(beanFactory, parameterType, qualifier.value());
			} else {
				return beanFactory.getBean(parameterType);
			}
		} catch(NoSuchBeanDefinitionException e) {
			logger.debug("{}, returning null", e.getMessage(), e);
			return null;
		}
	}
}
