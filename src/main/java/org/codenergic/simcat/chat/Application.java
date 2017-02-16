package org.codenergic.simcat.chat;

import java.io.IOException;
import java.util.UUID;

import org.codenergic.simcat.chat.core.annotation.EventBusMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;

@SpringBootApplication
public class Application {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public static void main(String[] args) throws IOException {
		SpringApplication.run(Application.class, args);
	}

	@Autowired
	public void sendToken(EventBus eventBus) {
		eventBus.consumer("chat.token", msg -> {
			logger.info("Generating token");
			msg.reply(UUID.randomUUID().toString());
		});
	}

	@Autowired
	public void sendMessagePeriodically(Vertx vertx, EventBus eventBus) {
		vertx.setPeriodic(5000, l -> {
			eventBus.publish("chat.message.1", "This is message from server");
			System.out.println("publishing");
		});
	}

	@EventBusMapping("chat.message.1")
	public void getChatMessage(Message<String> message) {
		logger.info(message.body());
	}
}
