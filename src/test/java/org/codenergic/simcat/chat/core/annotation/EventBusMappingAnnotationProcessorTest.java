package org.codenergic.simcat.chat.core.annotation;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.assertj.core.api.Assertions;
import org.codenergic.simcat.chat.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class, EventBusMappingAnnotationProcessorTest.class })
@ActiveProfiles({ "test" })
public class EventBusMappingAnnotationProcessorTest {
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
		Assertions.assertThat(lock.await(2, TimeUnit.SECONDS)).isTrue();
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
		Assertions.assertThat(lock.await(2, TimeUnit.SECONDS)).isTrue();
	}

	@Test
	public void testSendMessage3() throws InterruptedException {
		CountDownLatch lock = new CountDownLatch(1);
		eventBus.send("/test/message/3", "Testing 3", m -> {
			Message<Object> replyMessage = m.result();
			Assertions.assertThat("Message 3: Testing 3").isEqualTo(replyMessage.body());
			lock.countDown();
		});
		Assertions.assertThat(lock.await(2, TimeUnit.SECONDS)).isTrue();
	}

	@Test
	public void testSendMessage4() throws InterruptedException {
		CountDownLatch lock = new CountDownLatch(1);
		eventBus.send("/test/message/4", "Testing 4", m -> {
			Message<Object> replyMessage = m.result();
			Assertions.assertThat("Message 4: Testing 4 String bean").isEqualTo(replyMessage.body());
			lock.countDown();
		});
		Assertions.assertThat(lock.await(2, TimeUnit.SECONDS)).isTrue();
	}

	@EventBusMapping("/test/message/1")
	public void consumeAndReplyMessage1(Message<String> message) {
		message.reply("Message 1: " + message.body());
	}

	@EventBusMapping("/test/message/2")
	public String consumeAndReplyMessage2(Message<String> message) {
		return "Message 2: " + message.body();
	}

	@EventBusMapping("/test/message/3")
	public void consumeAndSendToAnother(Message<String> message, EventBus eventBus) {
		eventBus.send(message.replyAddress(), "Message 3: " + message.body());
	}

	@EventBusMapping("/test/message/4")
	public void consumeAndSendToAnother(Message<String> message, EventBus eventBus, @Qualifier("stringBean") String string) {
		eventBus.send(message.replyAddress(), "Message 4: " + message.body() + " " + string);
	}

	@Bean
	public String stringBean() {
		return "String bean";
	}
}
