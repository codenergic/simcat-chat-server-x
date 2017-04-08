package org.codenergic.simcat.auth.core.auth0;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "email", "email_verified", "clientID", "updated_at", "name", "picture", "user_id", "nickname",
		"identities", "created_at", "iss", "sub", "aud", "exp", "iat" })
public class Auth0User {
	@JsonProperty("email")
	private String email;
	@JsonProperty("email_verified")
	private Boolean emailVerified;
	@JsonProperty("clientID")
	private String clientID;
	@JsonProperty("updated_at")
	private String updatedAt;
	@JsonProperty("name")
	private String name;
	@JsonProperty("picture")
	private String picture;
	@JsonProperty("user_id")
	private String userId;
	@JsonProperty("nickname")
	private String nickname;
	@JsonProperty("identities")
	private List<Auth0Identity> identities = null;
	@JsonProperty("created_at")
	private Date createdAt;
	@JsonProperty("iss")
	private String iss;
	@JsonProperty("sub")
	private String sub;
	@JsonProperty("aud")
	private String aud;
	@JsonProperty("exp")
	private Integer exp;
	@JsonProperty("iat")
	private Integer iat;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<>();

	@JsonProperty("email")
	public String getEmail() {
		return email;
	}

	@JsonProperty("email")
	public void setEmail(String email) {
		this.email = email;
	}

	@JsonProperty("email_verified")
	public Boolean getEmailVerified() {
		return emailVerified;
	}

	@JsonProperty("email_verified")
	public void setEmailVerified(Boolean emailVerified) {
		this.emailVerified = emailVerified;
	}

	@JsonProperty("clientID")
	public String getClientID() {
		return clientID;
	}

	@JsonProperty("clientID")
	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	@JsonProperty("updated_at")
	public String getUpdatedAt() {
		return updatedAt;
	}

	@JsonProperty("updated_at")
	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("picture")
	public String getPicture() {
		return picture;
	}

	@JsonProperty("picture")
	public void setPicture(String picture) {
		this.picture = picture;
	}

	@JsonProperty("user_id")
	public String getUserId() {
		return userId;
	}

	@JsonProperty("user_id")
	public void setUserId(String userId) {
		this.userId = userId;
	}

	@JsonProperty("nickname")
	public String getNickname() {
		return nickname;
	}

	@JsonProperty("nickname")
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	@JsonProperty("identities")
	public List<Auth0Identity> getIdentities() {
		return identities;
	}

	@JsonProperty("identities")
	public void setIdentities(List<Auth0Identity> identities) {
		this.identities = identities;
	}

	@JsonProperty("created_at")
	public Date getCreatedAt() {
		return createdAt;
	}

	@JsonProperty("created_at")
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	@JsonProperty("iss")
	public String getIss() {
		return iss;
	}

	@JsonProperty("iss")
	public void setIss(String iss) {
		this.iss = iss;
	}

	@JsonProperty("sub")
	public String getSub() {
		return sub;
	}

	@JsonProperty("sub")
	public void setSub(String sub) {
		this.sub = sub;
	}

	@JsonProperty("aud")
	public String getAud() {
		return aud;
	}

	@JsonProperty("aud")
	public void setAud(String aud) {
		this.aud = aud;
	}

	@JsonProperty("exp")
	public Integer getExp() {
		return exp;
	}

	@JsonProperty("exp")
	public void setExp(Integer exp) {
		this.exp = exp;
	}

	@JsonProperty("iat")
	public Integer getIat() {
		return iat;
	}

	@JsonProperty("iat")
	public void setIat(Integer iat) {
		this.iat = iat;
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
