package org.codenergic.simcat.chat.event;

import org.codenergic.simcat.chat.core.annotation.EventBusComponent;
import org.codenergic.simcat.chat.core.annotation.EventBusMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;

/**
 * Created by diasa on 3/7/17.
 */
@EventBusComponent
public class ChatHandler {

	private static final Logger LOG = LoggerFactory.getLogger(ChatHandler.class);

	@EventBusMapping("/chat/message")
	public void handleMessage(Message<String> message, EventBus eventBus) {
		MultiMap headers = message.headers();
		String channel = headers.get("channel");
		String user = headers.get("user");
		LOG.info("processing message from {} from channel {}", user, channel);
		DeliveryOptions options = new DeliveryOptions();
		options.addHeader("user", user);
		message.reply("success");
		eventBus.send("/chat/message/" + channel, message.body(), options);
	}

}
