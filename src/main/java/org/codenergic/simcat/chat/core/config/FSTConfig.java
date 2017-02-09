package org.codenergic.simcat.chat.core.config;

import org.nustaq.serialization.FSTConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FSTConfig {
	@Bean
	public FSTConfiguration fstConfiguration() {
		return FSTConfiguration.createDefaultConfiguration().setForceSerializable(true);
	}
}
