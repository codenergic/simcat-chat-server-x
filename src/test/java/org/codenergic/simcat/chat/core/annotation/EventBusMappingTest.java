package org.codenergic.simcat.chat.core.annotation;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EventBusMappingTest.class)
public class EventBusMappingTest {
	@Autowired
	private EventBus eventBus;


	@Test
	public void testSendMessage1() throws InterruptedException {
		CountDownLatch lock = new CountDownLatch(1);
		eventBus.send("/test/message/1", "Testing 1", m -> {
			Message<Object> replyMessage = m.result();
			Assertions.assertThat("Message 1: Testing 1").isEqualTo(replyMessage.body());
			Assertions.assertThat("Message 1: Testing 2").isNotEqualTo(replyMessage.body());
			lock.countDown();
		});
		lock.await(2, TimeUnit.SECONDS);
	}

	@Test
	public void testSendMessage2() throws InterruptedException {
		CountDownLatch lock = new CountDownLatch(1);
		eventBus.send("/test/message/2", "Testing 2", m -> {
			Message<Object> replyMessage = m.result();
			Assertions.assertThat("Message 2: Testing 2").isEqualTo(replyMessage.body());
			Assertions.assertThat("Message 2: Testing 1").isNotEqualTo(replyMessage.body());
			lock.countDown();
		});
		lock.await(2, TimeUnit.SECONDS);
	}

	@EventBusMapping("/test/message/1")
	public void consumeAndReplyMessage1(Message<String> message) {
		message.reply("Message 1: " + message.body());
	}

	@EventBusMapping("/test/message/2")
	public String consumeAndReplyMessage2(Message<String> message) {
		return "Message 2: " + message.body();
	}

	@Bean
	public Vertx vertxBean() {
		return Vertx.vertx();
	}

	@Bean
	public EventBus eventBusBean(Vertx vertx) {
		return vertx.eventBus();
	}

	@Bean
	public EventBusMappingAnnotationProcessor annotationProcessor(ConfigurableListableBeanFactory beanFactory) {
		return new EventBusMappingAnnotationProcessor(beanFactory);
	}
}
