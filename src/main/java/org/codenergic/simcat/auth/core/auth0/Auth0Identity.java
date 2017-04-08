package org.codenergic.simcat.auth.core.auth0;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "user_id", "provider", "connection", "isSocial" })
public class Auth0Identity {
	@JsonProperty("user_id")
	private String userId;
	@JsonProperty("provider")
	private String provider;
	@JsonProperty("connection")
	private String connection;
	@JsonProperty("isSocial")
	private Boolean isSocial;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("user_id")
	public String getUserId() {
		return userId;
	}

	@JsonProperty("user_id")
	public void setUserId(String userId) {
		this.userId = userId;
	}

	@JsonProperty("provider")
	public String getProvider() {
		return provider;
	}

	@JsonProperty("provider")
	public void setProvider(String provider) {
		this.provider = provider;
	}

	@JsonProperty("connection")
	public String getConnection() {
		return connection;
	}

	@JsonProperty("connection")
	public void setConnection(String connection) {
		this.connection = connection;
	}

	@JsonProperty("isSocial")
	public Boolean getIsSocial() {
		return isSocial;
	}

	@JsonProperty("isSocial")
	public void setIsSocial(Boolean isSocial) {
		this.isSocial = isSocial;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

}