package org.codenergic.simcat.chat.core.event;

import org.assertj.core.api.Assertions;
import org.codenergic.simcat.chat.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;

/**
 * Created by diasa on 3/7/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class, ChatHandlerTest.class})
@ActiveProfiles({ "test" })
public class ChatHandlerTest {
	@Autowired
	private EventBus eventBus;

	@Test
	public void testSendMessage() throws InterruptedException {
		DeliveryOptions deliveryOptions = new DeliveryOptions();
		deliveryOptions.addHeader("user", "user1");
		deliveryOptions.addHeader("channel", "simcat");
		eventBus.send("/chat/message", "hi!", deliveryOptions, m->
			Assertions.assertThat("success").isEqualTo(m.result().body()));
	}
}
