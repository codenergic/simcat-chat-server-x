package org.codenergic.simcat.chat.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

@Configuration
@Profile({ "!test" })
public class HazelcastConfig {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Bean
	@ConfigurationProperties(prefix = "hazelcast")
	public Config hazelcastInstanceConfig() {
		Config config = new Config();
		logger.info("Configuring hazelcast");

		return config;
	}

	@Bean(destroyMethod = "shutdown")
	public HazelcastInstance hazelcastInstance(Config config) {
		logger.info("Initializing HazelcastInstance");
		HazelcastInstance instance = Hazelcast.newHazelcastInstance(config);
		logger.info("HazelcastInstance initialized: {}", instance.getName());

		return instance;
	}
}
