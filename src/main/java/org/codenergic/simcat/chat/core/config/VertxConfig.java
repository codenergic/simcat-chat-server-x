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
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import com.hazelcast.core.HazelcastInstance;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

@Configuration
@Profile({ "!test" })
public class VertxConfig {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static final long DEFAULT_CLUSTER_TIMEOUT = 60_000; // 60 seconds

	@Autowired
	private Environment env;

	@Bean
	public ClusterManager clusterManager(HazelcastInstance hazelcastInstance) {
		return new HazelcastClusterManager(hazelcastInstance);
	}

	@Bean
	@ConfigurationProperties(prefix = "vertx.core")
	public VertxOptions clusteredVertxOptions(ClusterManager clusterManager) {
		VertxOptions options = new VertxOptions();
		options.setClusterManager(clusterManager);

		logger.info("Configuring clustered vertx instance");

		return options;
	}

	@Bean(destroyMethod = "close")
	public Vertx clusteredVertx(VertxOptions options) throws InterruptedException {
		long timeout = env.getProperty("vertx.cluster.timeout", long.class, DEFAULT_CLUSTER_TIMEOUT);

		// Synchronize asynchronous process
		BlockingQueue<Vertx> queue = new ArrayBlockingQueue<>(1);

		logger.info("Initializing clustered Vertx instance");
		Vertx.clusteredVertx(options, event -> {
			if (event.failed()) {
				logger.error("Failed initialize clustered Vertx instance", event.cause());
				queue.add(null);
			} else {
				logger.info("Clustered Vertx instance initialized");
				queue.add(event.result());
			}
		});

		return queue.poll(timeout, TimeUnit.MILLISECONDS);
	}
}
