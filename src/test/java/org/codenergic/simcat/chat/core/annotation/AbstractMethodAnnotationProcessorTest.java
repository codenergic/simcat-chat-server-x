package org.codenergic.simcat.chat.core.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class AbstractMethodAnnotationProcessorTest {
	@Autowired
	private ConfigurableListableBeanFactory beanFactory;
	@Autowired
	private ApplicationContext applicationContext;
	private AbstractMethodAnnotationProcessor methodAnnotationProcessor;
	private final Class<AbstractMethodAnnotationProcessorTest> testClass = AbstractMethodAnnotationProcessorTest.class;

	@Before
	public void initialize() {
		methodAnnotationProcessor = new AbstractMethodAnnotationProcessor(beanFactory) {
			@Override
			protected boolean filterMethod(Method method) {
				return false;
			}

			@Override
			protected void doWithAnnotatedMethod(Object bean, String beanName, Method method, Object[] parameterInstances) {
				// do nothing
			}
		};
		methodAnnotationProcessor.filterMethod(null);
		methodAnnotationProcessor.doWithAnnotatedMethod(null, null, null, null);
		testReplaceParameterInstancesMethod(null);
		testReplaceParameterInstancesMethod(null, null);
		testReplaceParameterInstancesMethod(null, null, null);
	}

	@Test
	public void testReplaceParameterInstances1() throws NoSuchMethodException, SecurityException {
		Method method = testClass.getMethod("testReplaceParameterInstancesMethod", ApplicationContext.class);
		Object[] parameterInstances = { applicationContext };
		Object[] newParameterInstances = methodAnnotationProcessor.replaceParameterInstances(method, parameterInstances);
		assertThat(newParameterInstances).containsExactly(applicationContext);
	}

	@Test
	public void testReplaceParameterInstances2() throws NoSuchMethodException, SecurityException {
		Method method = testClass.getMethod("testReplaceParameterInstancesMethod", ApplicationContext.class,
				AbstractMethodAnnotationProcessorTest.class);
		Object[] parameterInstances = { applicationContext, null };
		Object[] newParameterInstances = methodAnnotationProcessor.replaceParameterInstances(method, parameterInstances);
		assertThat(newParameterInstances).containsExactly(applicationContext, null);
		newParameterInstances = methodAnnotationProcessor.replaceParameterInstances(method, parameterInstances, this);
		assertThat(newParameterInstances).containsExactly(applicationContext, this);
	}

	@Test
	public void testReplaceParameterInstances3() throws NoSuchMethodException, SecurityException {
		Method method = testClass.getMethod("testReplaceParameterInstancesMethod", ApplicationContext.class,
				AbstractMethodAnnotationProcessorTest.class, String.class);
		Object[] parameterInstances = { applicationContext, null, null };
		Object[] newParameterInstances = methodAnnotationProcessor.replaceParameterInstances(method, parameterInstances, this, "test");
		assertThat(newParameterInstances).containsExactly(applicationContext, this, "test");
	}

	public void testReplaceParameterInstancesMethod(ApplicationContext context) {
		// do nothing
	}

	public void testReplaceParameterInstancesMethod(ApplicationContext context, AbstractMethodAnnotationProcessorTest test) {
		// do nothing
	}

	public void testReplaceParameterInstancesMethod(ApplicationContext context, AbstractMethodAnnotationProcessorTest test, String string) {
		// do nothing
	}
}
