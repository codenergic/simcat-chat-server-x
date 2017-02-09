package org.codenergic.simcat.chat.core.config;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.vertx.core.Vertx;
import io.vertx.mqtt.MqttEndpoint;
import io.vertx.mqtt.MqttServer;
import io.vertx.mqtt.MqttServerOptions;

@Configuration
public class MQTTConfig {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Bean
	@ConfigurationProperties(prefix = "vertx.mqtt")
	public MqttServerOptions mqttServerOptions() {
		return new MqttServerOptions();
	}

	@Bean
	public MqttServer mqttServer(Vertx vertx, MqttServerOptions options) {
		return MqttServer.create(vertx, options);
	}

	@Bean
	public MqttEndpoint mqttEndpoint(MqttServer mqttServer) throws InterruptedException {
		BlockingQueue<MqttEndpoint> queue = new ArrayBlockingQueue<>(1);
		mqttServer.endpointHandler(queue::add);
		return queue.poll(30, TimeUnit.SECONDS);
	}

	@Autowired
	public void configureAndStartMqttServer(MqttServer mqttServer) {
		logger.info("Starting MQTT server");
		mqttServer.listen(event -> {
			if (event.failed()) {
				logger.error("Failed to start MQTT server", event.cause());
			} else {
				logger.info("MQTT server started at port {}", event.result().actualPort());
			}
		});
	}
}
