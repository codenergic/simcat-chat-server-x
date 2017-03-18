package org.codenergic.simcat.chat.core.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;

@EventBusComponent
public class TestEventBusComponent {
	@Autowired
	private EventBus eventBus;

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

	@EventBusMapping("/test/message/5")
	public void consumeAndSendToAnother() {
		eventBus.send("/test/message/5/reply", "Message 5");
	}

	@Bean
	public String stringBean() {
		return "String bean";
	}
}