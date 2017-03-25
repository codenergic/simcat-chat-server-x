package org.codenergic.simcat.chat.core.annotation;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.assertj.core.api.Assertions;
import org.codenergic.simcat.Simcat;
import org.codenergic.simcat.chat.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class })
@ActiveProfiles({ Simcat.PROFILE_TEST })
public class EventBusComponentAnnotationProcessorTest {
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

	@Test
	public void testSendMessage5() throws InterruptedException {
		CountDownLatch lock = new CountDownLatch(1);
		eventBus.consumer("/test/message/5/reply", m -> {
			Assertions.assertThat("Message 5").isEqualTo(m.body());
			lock.countDown();
		});
		eventBus.send("/test/message/5", null);
		Assertions.assertThat(lock.await(2, TimeUnit.SECONDS)).isTrue();
	}
}
