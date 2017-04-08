package org.codenergic.simcat.auth.core.config;

import java.io.UnsupportedEncodingException;

import org.codenergic.simcat.auth.core.auth0.Auth0Credentials;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

@Configuration
@EnableConfigurationProperties(Auth0Credentials.class)
public class JwtConfig {
	@Bean
	public JWTVerifier jwtVerifier(Auth0Credentials credentials) throws UnsupportedEncodingException {
		return JWT
				.require(Algorithm.HMAC256(credentials.getClientSecret()))
				.build();
	}
}
