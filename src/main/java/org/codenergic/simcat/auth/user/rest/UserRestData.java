package org.codenergic.simcat.auth.user.rest;

import org.codenergic.simcat.data.User;

import com.fasterxml.jackson.annotation.JsonProperty;


class UserRestData {
	@JsonProperty("id")
	private Long id;
	@JsonProperty("username")
	private String username;
	@JsonProperty("password")
	private String password;

	public UserRestData() {
		// do nothing
	}

	public UserRestData(User user) {
		this.id = user.id;
		this.username = user.username;
		this.password = user.password;
	}

	public Long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
}
