package org.codenergic.simcat.auth.authentication.rest;

import org.codenergic.simcat.auth.core.auth0.Auth0Service.Connection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PasswordlessLoginRestData {
	@JsonProperty("code")
	private String code;
	@JsonProperty("connection")
	private Connection connection;
	@JsonProperty("username")
	private String username;

	public String getCode() {
		return code;
	}

	public Connection getConnection() {
		return connection;
	}

	public String getUsername() {
		return username;
	}
}
